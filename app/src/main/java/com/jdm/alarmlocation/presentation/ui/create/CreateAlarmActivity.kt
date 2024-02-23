package com.jdm.alarmlocation.presentation.ui.create

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
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
import com.jdm.alarmlocation.base.BaseActivity
import com.jdm.alarmlocation.databinding.ActivityAddAlarmBinding
import com.jdm.alarmlocation.databinding.AdUnifiedBinding
import com.jdm.alarmlocation.domain.model.NameLocation
import com.jdm.alarmlocation.domain.model.Range
import com.jdm.alarmlocation.domain.model.Way
import com.jdm.alarmlocation.presentation.dialog.CommonDialog
import com.jdm.alarmlocation.presentation.dialog.CommonTimePickerDialog
import com.jdm.alarmlocation.presentation.dialog.ListDialog
import com.jdm.alarmlocation.presentation.ui.location.SearchLocationActivity
import com.jdm.alarmlocation.presentation.util.GoogleMobileAdsConsentManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class CreateAlarmActivity : BaseActivity<ActivityAddAlarmBinding>() {
    private val viewModel: CreateAlarmViewModel by viewModels()
    override val layoutResId: Int
        get() = R.layout.activity_add_alarm
    private val locationSearchActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val nameLocation: NameLocation? = it.data?.getParcelableExtra("location")
                if (nameLocation != null) {
                    viewModel.nameLocationData.value = nameLocation
                }
            }
        }
    private val rangeList = listOf<Range>(
        Range(300, "300", false),
        Range(600, "600", false),
        Range(900, "900", false),
        Range(1200, "1200", false),
        Range(1500, "1500", false),
    )
    private val wayList = listOf<Way>(
        Way(0, "진입하면", false),
        Way(1, "벗어나면", false)
    )

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private var currentNativeAd: NativeAd? = null


    override fun initView() {
        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
        googleMobileAdsConsentManager.gatherConsent(this) { consentError ->
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

    }
    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) { initializationStatus ->
            // Load an ad.
            refreshAd()
        }
    }
    private fun refreshAd() {

        val builder = AdLoader.Builder(this, ADMOB_AD_UNIT_ID)

        builder.forNativeAd { nativeAd ->
            var activityDestroyed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                activityDestroyed = isDestroyed
            }
            if (activityDestroyed || isFinishing || isChangingConfigurations) {
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



    override fun subscribe() {
        viewModel.leftCalendar.observe(this) {
            if (it != null) {
                binding.tvLeftTime.text = "${String.format("%02d",it.get(Calendar.HOUR_OF_DAY))}:${String.format("%02d",it.get(Calendar.MINUTE))}"
                binding.tvLeftTime.setTextColor(ContextCompat.getColor(this, R.color.blue_400))
            } else {
                binding.tvLeftTime.text = getString(R.string.str_default_time)
                binding.tvLeftTime.setTextColor(ContextCompat.getColor(this, R.color.gray_400))
            }
        }
        viewModel.rightCalendar.observe(this) {
            if (it != null) {
                binding.tvRightTime.text = "${String.format("%02d",it.get(Calendar.HOUR_OF_DAY))}:${String.format("%02d",it.get(Calendar.MINUTE))}"
                binding.tvRightTime.setTextColor(ContextCompat.getColor(this, R.color.blue_400))
            } else {
                binding.tvRightTime.text = getString(R.string.str_default_time)
                binding.tvRightTime.setTextColor(ContextCompat.getColor(this, R.color.gray_400))
            }
        }
        viewModel.nameLocationData.observe(this) {
            if (it != null) {
                binding.tvAddress.text = it.name
                binding.tvAddress.setTextColor(ContextCompat.getColor(this, R.color.blue_400))
            } else {
                binding.tvAddress.text = getString(R.string.str_address)
                binding.tvAddress.setTextColor(ContextCompat.getColor(this, R.color.gray_400))
            }
        }
        viewModel.range.observe(this) {
            if (it != null) {
                binding.tvRange.text = it.text
                binding.tvRange.setTextColor(ContextCompat.getColor(this, R.color.blue_400))
            } else {
                binding.tvRange.text = getString(R.string.str_range)
                binding.tvRange.setTextColor(ContextCompat.getColor(this, R.color.gray_400))
            }
        }
        viewModel.way.observe(this) {
            if (it != null) {
                binding.tvWay.text = it.text
                binding.tvWay.setTextColor(ContextCompat.getColor(this, R.color.blue_400))
            } else {
                binding.tvWay.text = getString(R.string.str_range)
                binding.tvWay.setTextColor(ContextCompat.getColor(this, R.color.gray_400))
            }
        }
        viewModel.dialogMsg.observe(this) {
            if (it != null) {
                CommonDialog(
                    title = getString(R.string.str_noti),
                    msg = it,
                    rightText = getString(R.string.str_confirm),
                    rightClick = {

                    }
                ).show(supportFragmentManager, CommonDialog.TAG)
            }

        }
        viewModel.insertResult.observe(this) {
            finish()
        }
    }

    override fun initEvent() {
        with(binding) {
            appbarCreateAlarm.lyAppbarBack.setOnClickListener {
                finish()
            }
            tvLeftTime.setOnClickListener {
                CommonTimePickerDialog(
                    title = getString(R.string.str_time_picker_title),
                    msg = "",
                    leftText = getString(R.string.str_cancel),
                    leftClick = {},
                    rightText = getString(R.string.str_confirm),
                    rightClick = {
                           viewModel.selectLeftTime(it)
                    }
                ).show(supportFragmentManager, CommonTimePickerDialog.TAG)
            }
            tvRightTime.setOnClickListener {
                CommonTimePickerDialog(
                    title = getString(R.string.str_time_picker_title),
                    msg = "",
                    leftText = getString(R.string.str_cancel),
                    leftClick = {},
                    rightText = getString(R.string.str_confirm),
                    rightClick = {
                        viewModel.selectRightTime(it)
                    }
                ).show(supportFragmentManager, CommonTimePickerDialog.TAG)
            }
            tvAddress.setOnClickListener {
                val intent = Intent(this@CreateAlarmActivity, SearchLocationActivity::class.java)
                locationSearchActivityLauncher.launch(intent)
            }
            tvRange.setOnClickListener {
                ListDialog(
                    this@CreateAlarmActivity,
                    rangeList
                ) {
                    rangeList.forEach { range ->
                        if (range.id == it.id) range.isSelected = true
                    }
                    viewModel.range.value = it as Range
                }.show(supportFragmentManager, ListDialog.TAG)
            }
            tvWay.setOnClickListener {
                ListDialog(
                    this@CreateAlarmActivity,
                    wayList
                ) {
                    wayList.forEach { way ->
                        if (way.id == it.id) way.isSelected = true
                    }
                    viewModel.way.value = it as Way
                }.show(supportFragmentManager, ListDialog.TAG)
            }
            btAlarmAdd.setOnClickListener {
                viewModel.createAlarm()
            }

        }
    }

    override fun initData() {
    }

    override fun onDestroy() {
        currentNativeAd?.destroy()
        super.onDestroy()
    }
    companion object {
        private val ADMOB_AD_UNIT_ID = "ca-app-pub-9955048675507406/4307598407"
    }
}