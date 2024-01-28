package com.jdm.alarmlocation.presentation.ui.create

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseActivity
import com.jdm.alarmlocation.databinding.ActivityAddAlarmBinding
import com.jdm.alarmlocation.domain.model.NameLocation
import com.jdm.alarmlocation.domain.model.Range
import com.jdm.alarmlocation.domain.model.Way
import com.jdm.alarmlocation.presentation.dialog.CommonTimePickerDialog
import com.jdm.alarmlocation.presentation.dialog.ListDialog
import com.jdm.alarmlocation.presentation.dialog.CommonDialog
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
}