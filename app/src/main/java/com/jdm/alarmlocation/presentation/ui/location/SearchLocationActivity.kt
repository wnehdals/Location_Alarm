package com.jdm.alarmlocation.presentation.ui.location

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseActivity
import com.jdm.alarmlocation.databinding.ActivitySearchLocationBinding
import com.jdm.alarmlocation.domain.model.NameLocation
import com.jdm.alarmlocation.presentation.dialog.PermissionDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchLocationActivity : BaseActivity<ActivitySearchLocationBinding>() {
    private val viewModel: SearchLocationViewModel by viewModels()
    override val layoutResId: Int
        get() = R.layout.activity_search_location
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            checkPermissions()
        }

    private val systemSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkPermissions()
        }
    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    private val searchLocationAdapter: SearchLocationAdapter by lazy {
        SearchLocationAdapter(this, ::onClickRecentlyLocation)
    }
    override fun initView() {
        with(binding) {
            rvSearchLocationRecently.adapter = searchLocationAdapter
            rvSearchLocationRecently.setHasFixedSize(true)
        }
    }

    override fun subscribe() {
        viewModel.direction.observe(this) {
            when (it) {
                FINISH -> {
                    goToCreateAlarmActivity()
                }
            }
        }
        viewModel.searchResultMsgData.observe(this) {
            if (it != null) {
                binding.tvSearchLocationGuide.text = it
            }
        }
        viewModel.locationList.observe(this) {
            if (it != null) {
                searchLocationAdapter.submitList(it)
            }
        }
    }

    override fun initEvent() {
        with(binding) {
            btSearchLocationCurrent.setOnClickListener {
                requestPermission()
                binding.llSearchLocationDefault.visibility = View.GONE

            }
        }
    }

    override fun initData() {
        viewModel.getLocationList()

    }
    private fun requestPermission() {
        permissionLauncher.launch(permissions)
    }
    private fun checkPermissions() {
        if (!isPermissionAllow(permissions)) {
            PermissionDialog(
                rightClick = {
                    if (permissions.all { shouldShowRequestPermissionRationale(it) }) {
                        requestPermission()
                    } else {
                        goToSystemSetting()
                    }
                }
            ).show(supportFragmentManager, PermissionDialog.TAG)

        } else {
            viewModel.getLocationInfo(this@SearchLocationActivity)
        }
    }
    private fun goToSystemSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        systemSettingLauncher.launch(intent)
    }
    private fun onClickRecentlyLocation(item: NameLocation) {
        viewModel.selectedNameLocation = item
        goToCreateAlarmActivity()
    }
    private fun goToCreateAlarmActivity() {
        var intent = Intent()
        intent.putExtra("location", viewModel.selectedNameLocation)
        setResult(RESULT_OK, intent)
        finish()
    }
    companion object {
        val FINISH = "finish"
    }

}