package com.jdm.alarmlocation.presentation.dialog

import android.os.Build
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseDialogFragment
import com.jdm.alarmlocation.databinding.AdUnifiedBinding
import com.jdm.alarmlocation.databinding.DialogAdmobBinding
import com.jdm.alarmlocation.presentation.util.GoogleMobileAdsConsentManager
import java.util.concurrent.atomic.AtomicBoolean

class AdmobDialog(
    private val msg: String,
    private val leftText: String? = null,
    private val rightText: String,
    private val leftClick: (() -> Unit)? = null,
    private val rightClick: () -> Unit,
    private val isCancel: Boolean = true
) : BaseDialogFragment<DialogAdmobBinding>() {
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private var currentNativeAd: NativeAd? = null
    override val layoutResId: Int
        get() = R.layout.dialog_admob

    override fun initView(view: View) {
        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(requireContext())
        googleMobileAdsConsentManager.gatherConsent(requireActivity()) { consentError ->
            if (consentError != null) {
                // Consent not obtained in current session.
                Log.w("CreateAlarmActivity", "${consentError.errorCode}. ${consentError.message}")
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }


        }

        // This sample attempts to load ads using consent obtained in the previous session.
        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk()
        }
        isCancelable = isCancel
        with(binding) {
            commonDialogEvenMsg.text = msg
            tvDialogCommonEvenRight.text = rightText
            tvDialogCommonEvenRight.isEnabled = true
            if (leftText != null) {
                tvDialogCommonEvenLeft.visibility = android.view.View.VISIBLE
                tvDialogCommonEvenLeft.text = leftText
            }
        }
    }
    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(requireContext()) { initializationStatus ->
            // Load an ad.
            refreshAd()
        }
    }
    private fun refreshAd() {

        val builder = AdLoader.Builder(requireContext(), ADMOB_AD_UNIT_ID)

        builder.forNativeAd { nativeAd ->
            var activityDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                activityDestroyed = requireActivity().isDestroyed
            }
            if (activityDestroyed || requireActivity().isFinishing || requireActivity().isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            currentNativeAd?.destroy()
            currentNativeAd = nativeAd
            val unifiedAdBinding = AdUnifiedBinding.inflate(layoutInflater)
            populateNativeAdView(nativeAd, unifiedAdBinding)
            binding.adFrame.removeAllViews()
            binding.adFrame.addView(unifiedAdBinding.root)
        }

        val videoOptions =
            VideoOptions.Builder().setStartMuted(true).build()

        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

        builder.withNativeAdOptions(adOptions)

        val adLoader =
            builder
                .withAdListener(
                    object : AdListener() {
                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            val error =
                                """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
                        }

                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            binding.tvDialogCommonEvenRight.isEnabled = true
                        }
                    }
                )
                .build()

        adLoader.loadAd(AdRequest.Builder().build())

    }
    private fun populateNativeAdView(nativeAd: NativeAd, unifiedAdBinding: AdUnifiedBinding) {
        val nativeAdView: NativeAdView = unifiedAdBinding.nativeAdView

        // Set the media view.
        nativeAdView.mediaView = unifiedAdBinding.adMedia

        // Set other ad assets.
        nativeAdView.headlineView = unifiedAdBinding.adHeadline
        nativeAdView.bodyView = unifiedAdBinding.adBody
        nativeAdView.callToActionView = unifiedAdBinding.adCallToAction
        nativeAdView.iconView = unifiedAdBinding.adAppIcon
        nativeAdView.priceView = unifiedAdBinding.adPrice
        nativeAdView.starRatingView = unifiedAdBinding.adStars
        nativeAdView.storeView = unifiedAdBinding.adStore
        nativeAdView.advertiserView = unifiedAdBinding.adAdvertiser

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        unifiedAdBinding.adHeadline.text = nativeAd.headline
        nativeAd.mediaContent?.let { unifiedAdBinding.adMedia.setMediaContent(it) }

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            unifiedAdBinding.adBody.visibility = View.INVISIBLE
        } else {
            unifiedAdBinding.adBody.visibility = View.VISIBLE
            unifiedAdBinding.adBody.text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            unifiedAdBinding.adCallToAction.visibility = View.INVISIBLE
        } else {
            unifiedAdBinding.adCallToAction.visibility = View.VISIBLE
            unifiedAdBinding.adCallToAction.text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            unifiedAdBinding.adAppIcon.visibility = View.GONE
        } else {
            unifiedAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
            unifiedAdBinding.adAppIcon.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            unifiedAdBinding.adPrice.visibility = View.INVISIBLE
        } else {
            unifiedAdBinding.adPrice.visibility = View.VISIBLE
            unifiedAdBinding.adPrice.text = nativeAd.price
        }

        if (nativeAd.store == null) {
            unifiedAdBinding.adStore.visibility = View.INVISIBLE
        } else {
            unifiedAdBinding.adStore.visibility = View.VISIBLE
            unifiedAdBinding.adStore.text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            unifiedAdBinding.adStars.visibility = View.INVISIBLE
        } else {
            unifiedAdBinding.adStars.rating = nativeAd.starRating!!.toFloat()
            unifiedAdBinding.adStars.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            unifiedAdBinding.adAdvertiser.visibility = View.INVISIBLE
        } else {
            unifiedAdBinding.adAdvertiser.text = nativeAd.advertiser
            unifiedAdBinding.adAdvertiser.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        nativeAdView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val mediaContent = nativeAd.mediaContent
        val vc = mediaContent?.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc != null && mediaContent.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks =
                object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        super.onVideoEnd()
                    }
                }
        } else {
        }
    }

    override fun initEvent() {
        with(binding) {
            tvDialogCommonEvenRight.setOnClickListener {
                dialog?.dismiss()
                rightClick()
            }
            tvDialogCommonEvenLeft.setOnClickListener {
                dialog?.dismiss()
                leftClick?.let { it() }
            }
        }
    }

    override fun subscribe() {
    }

    override fun initData() {
    }

    companion object {
        val TAG = this.javaClass.simpleName
        val ADMOB_AD_UNIT_ID = "ca-app-pub-9955048675507406/2393232572"
    }
}
