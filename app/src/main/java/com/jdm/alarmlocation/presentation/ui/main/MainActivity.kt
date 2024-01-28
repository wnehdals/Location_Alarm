package com.jdm.alarmlocation.presentation.ui.main

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseActivity
import com.jdm.alarmlocation.databinding.ActivityMainBinding
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.presentation.dialog.AlarmPermissionDialog
import com.jdm.alarmlocation.presentation.dialog.CommonDialog
import com.jdm.alarmlocation.presentation.dialog.PermissionDialog
import com.jdm.alarmlocation.presentation.service.FusedLocationService
import com.jdm.alarmlocation.presentation.ui.create.CreateAlarmActivity
import com.jdm.alarmlocation.presentation.util.Const.ACTION_START_LOCATION_SERVICE
import com.jdm.alarmlocation.presentation.util.Const.ACTION_STOP_LOCATION_SERVICE
import com.jdm.alarmlocation.presentation.util.Const.BUNDLE_KEY_ALARM
import com.jdm.alarmlocation.presentation.util.Const.SERVICE_NAME
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.ads.*
import com.jdm.alarmlocation.BuildConfig
import com.jdm.alarmlocation.presentation.dialog.AdmobDialog
import com.jdm.alarmlocation.presentation.ui.util.slideLeft
import com.jdm.alarmlocation.presentation.util.GoogleMobileAdsConsentManager
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_main
    private val viewModel : MainViewModel by viewModels()
    private val backgroundPermission = android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
    private var updatedAlarm: Alarm? = null
    private val notiPermssion = android.Manifest.permission.POST_NOTIFICATIONS
    private lateinit var requirePermission : Array<String>
    private val notiPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            checkRequirePermission()
        }


    private val systemSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkRequirePermission()
        }
    private val createAlarmLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.getAlarmList()
        }
    private val alarmAdapter: AlarmAdapter by lazy {
        AlarmAdapter(this, this::onDeleteAlarm, this::onCheckChangeAlarm)
    }
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val fm = supportFragmentManager.findFragmentByTag(AdmobDialog.TAG)
            if (fm == null) {
                AdmobDialog(
                    msg = getString(R.string.str_review_desc),
                    leftText = getString(R.string.str_cancel),
                    rightText = getString(R.string.str_exit),
                    rightClick = {
                        finish()
                    },
                    isCancel = false
                ).show(supportFragmentManager, AdmobDialog.TAG)
            }
        }
    }
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private val initialLayoutComplete = AtomicBoolean(false)
    private lateinit var adView: AdView
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager


    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.adBanner.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }
    override fun initView() {
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        MobileAds.initialize(this) {}

        adView = AdView(this)
        binding.adBanner.addView(adView)

        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
        googleMobileAdsConsentManager.gatherConsent(this) { error ->
            if (error != null) {
                // Consent not obtained in current session.
            }

            if (googleMobileAdsConsentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }

            if (googleMobileAdsConsentManager.isPrivacyOptionsRequired) {
                // Regenerate the options menu to include a privacy setting.
                invalidateOptionsMenu()
            }
        }

        // This sample attempts to load ads using consent obtained in the previous session.
        if (googleMobileAdsConsentManager.canRequestAds) {
            initializeMobileAdsSdk()
        }

        binding.adBanner.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete.getAndSet(true) && googleMobileAdsConsentManager.canRequestAds) {
                loadBanner()
            }
        }
        if (BuildConfig.DEBUG) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder().setTestDeviceIds(listOf(TEST_DEVICE_ID)).build()
            )
        }

        val permissionList = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionList.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionList.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        requirePermission = permissionList.toTypedArray()

        binding.rvMain.adapter = alarmAdapter
    }
    private fun loadBanner() {
        adView.adUnitId = AD_UNIT_ID
        adView.setAdSize(adSize)

        val adRequest = AdRequest.Builder().build()

        adView.loadAd(adRequest)
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        // Load an ad.
        if (initialLayoutComplete.get()) {
            loadBanner()
        }
    }

    override fun subscribe() {
        viewModel.alarmListData.observe(this) {
            alarmAdapter.submitList(it)
        }
        viewModel.updateAlarmData.observe(this) {
            clearUpdateAlarm()
        }
    }

    override fun initEvent() {
        with(binding) {
            fab.setOnClickListener {
                val intent = Intent(this@MainActivity, CreateAlarmActivity::class.java)
                createAlarmLauncher.launch(intent)
            }
        }

    }

    override fun initData() {
        viewModel.getAlarmList()
    }
    private fun onDeleteAlarm(item: Alarm) {
        viewModel.deleteAlarm(item)
    }

    private fun clearUpdateAlarm() {
        updatedAlarm = null
    }

    private fun checkRequirePermission() {
        var idx = -1
        for(i in requirePermission.indices) {
            if (checkSelfPermission(requirePermission[i]) == PERMISSION_DENIED) {
                idx = i
                break
            }
        }
        if (idx != -1) {
            if (requirePermission[idx] == backgroundPermission) {
                showPermissionDialog {
                    goToSystemSetting()
                }
            } else {
                showNotiPermissionDialog {
                    notiPermissionLauncher.launch(notiPermssion)
                }
            }
        } else {
            permissionSuccessProcess()
        }


    }
    private fun onCheckChangeAlarm(item: Alarm) {
        item.isOn = !item.isOn
        updatedAlarm = item
        if (updatedAlarm!!.isOn) {

            checkRequirePermission()
            /*
            if (checkSelfPermission(backgroundPermission) == PERMISSION_GRANTED) {
                permissionSuccessProcess()
            } else {
                showPermissionDialog {
                    goToSystemSetting()
                }
            }

             */
        } else {
            clearUpdateAlarm()
            viewModel.updateAlarm(item)
            stopLocationService()
        }

    }
    private fun goToSystemSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:" + this@MainActivity.packageName)
        }
        systemSettingLauncher.launch(intent)
    }
    private fun permissionSuccessProcess() {
        if (isLocationServiceRunning()) {
            CommonDialog(
                title = getString(R.string.str_warning),
                msg = getString(R.string.str_warning_duplicate_desc1),
                rightText = getString(R.string.str_confirm),
                rightClick = {}
            ).show(supportFragmentManager, CommonDialog.TAG)
        } else {
            viewModel.updateAlarm(updatedAlarm)
            startLocationService()
        }

    }
    private fun startLocationService() {
        if (updatedAlarm == null) {
            return
        }

        if (!isLocationServiceRunning()) {
            Intent(applicationContext, FusedLocationService::class.java)
                .putExtra(BUNDLE_KEY_ALARM, updatedAlarm)
                .setAction(ACTION_START_LOCATION_SERVICE)
                .also { startService(it) }
        }
    }
    private fun stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent(applicationContext, FusedLocationService::class.java)
                .setAction(ACTION_STOP_LOCATION_SERVICE)
                .also { startService(it) }
        }
    }
    private fun isLocationServiceRunning(): Boolean {
        val activityManager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getRunningServices(Int.MAX_VALUE).forEach {
            if (it.service.className == SERVICE_NAME) {
                if (it.foreground) {
                    return true
                }
            } else {
                return false
            }
        }
        return false
    }
    private fun showNotiPermissionDialog(rightClick: () -> Unit) {
        PermissionDialog(
            context = this,
            icon = R.drawable.ic_noti_black,
            msg = getString(R.string.str_app_permission_noti_desc),
            permissionName = getString(R.string.str_noti),
            isCancel = false,
            rightClick = rightClick
        ).show(supportFragmentManager, PermissionDialog.TAG)
    }

    private fun showPermissionDialog(rightClick: () -> Unit) {
        PermissionDialog(
            context = this,
            icon = R.drawable.ic_noti_black,
            msg = getString(R.string.background_permission_desc),
            permissionName = getString(R.string.str_location),
            isCancel = false,
            rightClick = rightClick
        ).show(supportFragmentManager, AlarmPermissionDialog.TAG)
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }
    companion object {
        private val AD_UNIT_ID = "ca-app-pub-9955048675507406/4307598407"
        val TEST_DEVICE_ID = "65DE933BBB628C8922A2BB958C2011FB"
    }
}