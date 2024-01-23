package com.jdm.alarmlocation.presentation.ui.create

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseActivity
import com.jdm.alarmlocation.databinding.ActivityAddAlarmBinding
import com.jdm.alarmlocation.domain.model.NameLocation
import com.jdm.alarmlocation.presentation.dialog.CommonTimePickerDialog
import com.jdm.alarmlocation.presentation.dialog.PermissionDialog
import com.jdm.alarmlocation.presentation.ui.location.SearchLocationActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

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
    override fun initView() {
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
    }

    override fun initEvent() {
        with(binding) {
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
        }
    }

    override fun initData() {
    }
}