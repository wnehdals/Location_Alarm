package com.jdm.alarmlocation.presentation.ui.splash

import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseActivity
import com.jdm.alarmlocation.databinding.ActivitySplashBinding
import com.jdm.alarmlocation.presentation.dialog.CommonDialog
import com.jdm.alarmlocation.presentation.dialog.PermissionDialog
import com.jdm.alarmlocation.presentation.ui.main.MainActivity
import com.jdm.alarmlocation.presentation.ui.util.hasPermissions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val viewModel: SplashViewModel by viewModels()
    override val layoutResId: Int
        get() = R.layout.activity_splash
    private val notiPermssion = android.Manifest.permission.POST_NOTIFICATIONS
    private val notiPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            goToMainActivity()
        } else {
            goToSystemSetting()
        }
    }
    private val systemSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (checkSelfPermission(notiPermssion) == PERMISSION_GRANTED) {
                goToMainActivity()
            } else {
                showPermissionDialog {
                    goToSystemSetting()
                }
            }
        }
    override fun initView() {
        viewModel.setRemoteConfig()
    }

    override fun subscribe() {
        viewModel.splashSideEffect.observe(this) {
            when (it) {
                ForceVersionUpdate -> {
                    showForceUpdateDialog()
                }
                SelectVersionUpdate -> {
                    showSelectUpdateDialog()
                }
                MoveMain -> {
                    goToMainActivity()
                    //checkNotiPermission()
                }
            }
        }
    }

    override fun initEvent() {
    }

    override fun initData() {
        viewModel.getAppVersion(this)
    }
    private fun goToSystemSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:" + this@SplashActivity.packageName)
        }
        systemSettingLauncher.launch(intent)
    }
    fun showForceUpdateDialog() {
        CommonDialog(
            title = getString(R.string.str_notification_update),
            msg = getString(R.string.str_update_guide),
            rightText = getString(R.string.str_exit_app),
            rightClick = {
                exitApp()
            },
            isCancel = false
        ).show(supportFragmentManager, CommonDialog.TAG)
    }
    fun showSelectUpdateDialog() {
        CommonDialog(
            title = getString(R.string.str_notification_update),
            msg = getString(R.string.str_update_guide),
            rightText = getString(R.string.str_do_next),
            rightClick = {
                checkNotiPermission()
            },
            isCancel = false
        ).show(supportFragmentManager, CommonDialog.TAG)
    }
    fun checkNotiPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(notiPermssion) == PERMISSION_GRANTED) {
                goToMainActivity()
            } else {
                showPermissionDialog {
                    notiPermissionLauncher.launch(notiPermssion)
                }
            }

        } else {
            goToMainActivity()
        }

    }
    fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        goToActivity(intent)
        finish()
    }
    fun showPermissionDialog(rightClick: () -> Unit) {
        PermissionDialog(
            context = this,
            icon = R.drawable.ic_noti_black,
            msg = getString(R.string.str_app_permission_noti_desc),
            permissionName = getString(R.string.str_noti),
            isCancel = false,
            rightClick = rightClick
        ).show(supportFragmentManager, PermissionDialog.TAG)
    }

    companion object {
        val ForceVersionUpdate = 1
        val SelectVersionUpdate = 2
        val MoveMain = 3
    }
}