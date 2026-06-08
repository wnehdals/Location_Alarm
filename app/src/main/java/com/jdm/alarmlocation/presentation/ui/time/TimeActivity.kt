package com.jdm.alarmlocation.presentation.ui.time

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseActivity
import com.jdm.alarmlocation.databinding.ActivityTimeBinding
import com.jdm.alarmlocation.presentation.util.AppUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class TimeActivity : BaseActivity<ActivityTimeBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_time
    private val viewModel: TimeViewModel by viewModels()
    private val hoursArr = Array(24) { (it).toString() }
    private val minuteArr = Array(60) { (it).toString() }
    override fun initView() {
        binding.timeHourPicker.minValue = 0
        binding.timeHourPicker.maxValue = 23
        binding.timeMinutePicker.minValue = 0
        binding.timeMinutePicker.maxValue = 59
        binding.timeHourPicker.displayedValues = hoursArr
        binding.timeMinutePicker.displayedValues = minuteArr
        binding.timeHourPicker.wrapSelectorWheel = false
        binding.timeMinutePicker.wrapSelectorWheel = false
    }

    override fun subscribe() {
        viewModel.hourData.observe(this) {
            binding.timeHourPicker.value = it
        }

        viewModel.minuteData.observe(this) {
            binding.timeMinutePicker.value = it
        }



        viewModel.selectedDayData.observe(this) {
            binding.tvTimeSun.setBackgroundDrawable(null)
            binding.tvTimeSun.setTextColor(ContextCompat.getColor(this, R.color.red_400))

            binding.tvTimeMon.setBackgroundDrawable(null)
            binding.tvTimeMon.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding.tvTimeTue.setBackgroundDrawable(null)
            binding.tvTimeTue.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding.tvTimeWed.setBackgroundDrawable(null)
            binding.tvTimeWed.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding.tvTimeThu.setBackgroundDrawable(null)
            binding.tvTimeThu.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding.tvTimeFri.setBackgroundDrawable(null)
            binding.tvTimeFri.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding.tvTimeSat.setBackgroundDrawable(null)
            binding.tvTimeSat.setTextColor(ContextCompat.getColor(this, R.color.black))

            it.forEach {
                when (it) {
                    Calendar.SUNDAY -> {
                        binding.tvTimeSun.setBackgroundDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.cir_blue_400
                            )
                        )
                        binding.tvTimeSun.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }

                    Calendar.MONDAY -> {
                        binding.tvTimeMon.setBackgroundDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.cir_blue_400
                            )
                        )
                        binding.tvTimeMon.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }

                    Calendar.TUESDAY -> {
                        binding.tvTimeTue.setBackgroundDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.cir_blue_400
                            )
                        )
                        binding.tvTimeTue.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }

                    Calendar.WEDNESDAY -> {
                        binding.tvTimeWed.setBackgroundDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.cir_blue_400
                            )
                        )
                        binding.tvTimeWed.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }

                    Calendar.THURSDAY -> {
                        binding.tvTimeThu.setBackgroundDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.cir_blue_400
                            )
                        )
                        binding.tvTimeThu.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }

                    Calendar.FRIDAY -> {
                        binding.tvTimeFri.setBackgroundDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.cir_blue_400
                            )
                        )
                        binding.tvTimeFri.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }

                    Calendar.SATURDAY -> {
                        binding.tvTimeSat.setBackgroundDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.cir_blue_400
                            )
                        )
                        binding.tvTimeSat.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }

                    else -> {}
                }
            }
        }
    }

    override fun initEvent() {
        binding.tvTimeSun.setOnClickListener {
            viewModel.onClickDay(Calendar.SUNDAY)
        }
        binding.tvTimeMon.setOnClickListener {
            viewModel.onClickDay(Calendar.MONDAY)
        }
        binding.tvTimeTue.setOnClickListener {
            viewModel.onClickDay(Calendar.TUESDAY)
        }
        binding.tvTimeWed.setOnClickListener {
            viewModel.onClickDay(Calendar.WEDNESDAY)
        }
        binding.tvTimeThu.setOnClickListener {
            viewModel.onClickDay(Calendar.THURSDAY)
        }
        binding.tvTimeFri.setOnClickListener {
            viewModel.onClickDay(Calendar.FRIDAY)
        }
        binding.tvTimeSat.setOnClickListener {
            viewModel.onClickDay(Calendar.SATURDAY)
        }
        binding.ivTimeBack.setOnClickListener {
            finish()
        }
        binding.btTimeCancel.setOnClickListener {
            finish()
        }
        binding.btTimeComplete.setOnClickListener {
            if (viewModel.selectedDayData.value.isNullOrEmpty()) {
                Toast.makeText(this, "요일을 선택해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            setResult(RESULT_OK, getFinishIntent())
            finish()
        }
    }

    override fun initData() {
        val days = intent.getIntArrayExtra(PARAM_DAYS)?.toList()?: listOf()
        viewModel.hourData.value = intent.getIntExtra(PARAM_HOUR, 0)?: 0
        viewModel.minuteData.value = intent.getIntExtra(PARAM_MINUTE, 0)?: 0
        viewModel.selectedDayData.value = intent.getIntArrayExtra(PARAM_DAYS)?.toList()?: listOf()
    }

    fun getFinishIntent(): Intent {
        val intent = Intent()
        val notNullList = viewModel.selectedDayData.value ?: emptyList()
        intent.putExtra(PARAM_DAYS, notNullList.toIntArray())
        return intent
    }

    companion object {
        const val PARAM_HOUR = "hour"
        const val PARAM_MINUTE = "minute"
        const val PARAM_DAYS = "days"
        fun getIntent(context: Context, days: List<Int>): Intent {
            val intent = Intent(context, TimeActivity::class.java)
            intent.putExtra(PARAM_DAYS, days.toIntArray())
            return intent
        }

    }
}