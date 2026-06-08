package com.jdm.alarmlocation.presentation.ui.main

import android.Manifest
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseActivity
import com.jdm.alarmlocation.databinding.ActivityMainBinding
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.presentation.dialog.AlarmPermissionDialog
import com.jdm.alarmlocation.presentation.dialog.CommonDialog
import com.jdm.alarmlocation.presentation.dialog.PermissionDialog
import com.jdm.alarmlocation.presentation.service.FusedLocationService
import com.jdm.alarmlocation.presentation.util.Const.ACTION_START_LOCATION_SERVICE
import com.jdm.alarmlocation.presentation.util.Const.ACTION_STOP_LOCATION_SERVICE
import com.jdm.alarmlocation.presentation.util.Const.BUNDLE_KEY_ALARM
import com.jdm.alarmlocation.presentation.util.Const.SERVICE_NAME
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.ads.*
import com.jdm.alarmlocation.BuildConfig
import com.jdm.alarmlocation.domain.model.Routine
import com.jdm.alarmlocation.presentation.dialog.LocationDialog
import com.jdm.alarmlocation.presentation.dialog.NotificationDialog
import com.jdm.alarmlocation.presentation.dialog.ReminderDialog
import com.jdm.alarmlocation.presentation.service.GeofenceHelper
import com.jdm.alarmlocation.presentation.service.MidnightReceiver
import com.jdm.alarmlocation.presentation.ui.routine.CreateRoutineActivity
import com.jdm.alarmlocation.presentation.ui.util.slideLeft
import java.util.Calendar
import java.util.concurrent.atomic.AtomicBoolean

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_main
    private val viewModel: MainViewModel by viewModels()
    private val backgroundPermission = android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
    private var updatedAlarm: Alarm? = null
    private val notiPermssion = android.Manifest.permission.POST_NOTIFICATIONS
    private lateinit var requirePermission: Array<String>
    private val alarmManager by lazy {
        this.getSystemService<AlarmManager>()
    }

    private val notiPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }


    private val systemSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        }
    private val createAlarmLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.getAlarmList()
        }
    private val alarmAdapter: AlarmAdapter by lazy {
        AlarmAdapter(this, this::onClickRoutine, this::onCheckChangeAlarm)
    }
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private val initialLayoutComplete = AtomicBoolean(false)
    //private lateinit var adView: AdView

    /*
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

     */
    override fun initView() {
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        /*
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

         */

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
    /*
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

     */

    override fun subscribe() {
        viewModel.alarmListData.observe(this) {
            alarmAdapter.submitList(it)
        }
    }

    override fun initEvent() {
        binding.fab.setOnClickListener {
            checkAlarmPermission(
                onGranted = {
                    createAlarmLauncher.launch(CreateRoutineActivity.getIntent(this@MainActivity))
                }
            )
        }
    }

    private fun checkAlarmPermission(onGranted: () -> Unit) {
        when {
            alarmManager!!.canScheduleExactAlarms() -> {
                onGranted()
            }

            else -> {
                val dialog: ReminderDialog = ReminderDialog.newInstance()
                dialog.setOnResultListener(object : ReminderDialog.ReminderDialogListener {
                    override fun onClickPositive() {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        intent.data = Uri.parse("package:" + this@MainActivity.packageName)
                        startActivity(intent)
                    }

                    override fun onClickNegative() {
                    }
                })
                dialog.show(supportFragmentManager, ReminderDialog.TAG)
            }
        }
    }

    private fun checkLocationPermission(onGranted: () -> Unit) {
        if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PERMISSION_DENIED) {
            val dialog = LocationDialog.newInstance()
            dialog.setOnResultListener(object : LocationDialog.LocationDialogListener {
                override fun onClickPositive() {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:" + this@MainActivity.packageName)
                    }
                    systemSettingLauncher.launch(intent)
                }

                override fun onClickNegative() {

                }
            })
            dialog.show(supportFragmentManager, LocationDialog.TAG)
        } else {
            onGranted()
        }
    }

    override fun initData() {
        viewModel.getAlarmList()
    }


    private fun onClickRoutine(item: Routine) {
        val intent = CreateRoutineActivity.getIntent(this, item.id)
        createAlarmLauncher.launch(intent)
    }

    private fun clearUpdateAlarm() {
        updatedAlarm = null
    }

    private fun onCheckChangeAlarm(item: Routine) {
        val newAlarm = item.copy(
            isOn = !item.isOn
        )
        if (newAlarm.isOn) {
            checkAlarmPermission(
                onGranted = {
                    checkNotiPermission(
                        onGranted = {
                            checkLocationPermission(
                                onGranted = {
                                    viewModel.updateAlarm(
                                        routine = newAlarm,
                                        onRefresh = {
                                            val intent = Intent(this, MidnightReceiver::class.java).apply {
                                                action = "ACTION_DAILY_MIDNIGHT_UPDATE"
                                            }
                                            sendBroadcast(intent)
                                            Toast.makeText(
                                                this@MainActivity,
                                                getRoutineMessage(newAlarm),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )

                                }
                            )
                        }
                    )
                }
            )
        } else {
            viewModel.updateAlarm(
                routine = newAlarm,
                onRefresh = {
                    val intent = Intent(this, MidnightReceiver::class.java).apply {
                        action = "ACTION_DAILY_MIDNIGHT_UPDATE"
                    }
                    sendBroadcast(intent)
                }
            )
        }
    }

    private fun getRoutineMessage(item: Routine): String {
        val way = if (item.isIn) "진입하면" else "벗어나면"
        return "${item.placeTitle} ${way} 알려주는 알람이 설정되었어요."
    }


    private fun goToSystemSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:" + this@MainActivity.packageName)
        }
        systemSettingLauncher.launch(intent)
    }

    override fun onPause() {
        //adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        checkNotiPermission(
            onGranted = {}
        )
        //adView.resume()
    }

    override fun onDestroy() {
        //adView.destroy()
        super.onDestroy()
    }

    private fun checkNotiPermission(onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PERMISSION_DENIED) {
                val dialog = NotificationDialog.newInstance()
                dialog.setOnResultListener(object : NotificationDialog.ReminderDialogListener {
                    override fun onClickPositive() {
                        notiPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }

                    override fun onClickNegative() {
                    }
                })
                dialog.show(supportFragmentManager, NotificationDialog.TAG)
            } else {
                onGranted()
            }
        } else {
            onGranted()
        }
    }

    companion object {
        private val AD_UNIT_ID = "ca-app-pub-9955048675507406/4307598407"
        val TEST_DEVICE_ID = "65DE933BBB628C8922A2BB958C2011FB"
    }
}