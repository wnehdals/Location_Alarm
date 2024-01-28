package com.jdm.alarmlocation.presentation.ui.main

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseActivity
import com.jdm.alarmlocation.databinding.ActivityMainBinding
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.presentation.dialog.AlarmPermissionDialog
import com.jdm.alarmlocation.presentation.dialog.PermissionDialog
import com.jdm.alarmlocation.presentation.service.FusedLocationService
import com.jdm.alarmlocation.presentation.ui.create.CreateAlarmActivity
import com.jdm.alarmlocation.presentation.ui.util.hasPermissions
import com.jdm.alarmlocation.presentation.util.Const.ACTION_START_LOCATION_SERVICE
import com.jdm.alarmlocation.presentation.util.Const.ACTION_STOP_LOCATION_SERVICE
import com.jdm.alarmlocation.presentation.util.Const.BUNDLE_KEY_ALARM
import com.jdm.alarmlocation.presentation.util.Const.SERVICE_NAME
import dagger.hilt.android.AndroidEntryPoint

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
        AlarmAdapter(this, this::onClickAlarm, this::onCheckChangeAlarm)
    }
    fun showNotiPermissionDialog(rightClick: () -> Unit) {
        PermissionDialog(
            context = this,
            icon = R.drawable.ic_noti_black,
            msg = getString(R.string.str_app_permission_noti_desc),
            permissionName = getString(R.string.str_noti),
            isCancel = false,
            rightClick = rightClick
        ).show(supportFragmentManager, PermissionDialog.TAG)
    }

    fun showPermissionDialog(rightClick: () -> Unit) {
        PermissionDialog(
            context = this,
            icon = R.drawable.ic_noti_black,
            msg = getString(R.string.background_permission_desc),
            permissionName = getString(R.string.str_location),
            isCancel = false,
            rightClick = rightClick
        ).show(supportFragmentManager, AlarmPermissionDialog.TAG)
    }

    override fun initView() {
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

    override fun subscribe() {
        viewModel.alarmListData.observe(this) {
            Log.e("livedata", it.toString())
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
    private fun onClickAlarm(item: Alarm) {

    }

    private fun clearUpdateAlarm() {
        updatedAlarm = null
    }
    private fun permissionSuccessProcess() {
        viewModel.updateAlarm(updatedAlarm)
        startLocationService()
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
        updatedAlarm = item
        if (item.isOn) {
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
            Log.e("serviceName", it.service.className)
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
}