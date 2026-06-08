package com.jdm.alarmlocation.presentation.ui.routine

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseActivity
import com.jdm.alarmlocation.databinding.ActivityCreateRoutineBinding
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.domain.model.Place
import com.jdm.alarmlocation.domain.toDayOfKor
import com.jdm.alarmlocation.domain.toPlace
import com.jdm.alarmlocation.presentation.dialog.LocationDialog
import com.jdm.alarmlocation.presentation.ui.location.SearchLocationActivity
import com.jdm.alarmlocation.presentation.ui.time.TimeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateRoutineActivity : BaseActivity<ActivityCreateRoutineBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_create_routine
    private val viewModel: CreateRoutineViewModel by viewModels()
    private val systemSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PERMISSION_GRANTED) {

            }
        }
    private val timeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val days = it.data?.getIntArrayExtra(TimeActivity.PARAM_DAYS)

                days?.forEach {
                    Log.e("sdfs", it.toString())
                }
                viewModel.daysData.value = days!!.toList().sorted()
            }
        }

    private val locationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val place = it.data?.getParcelableExtra<Place>(SearchLocationActivity.PARAM_PLACE)
                val range = it.data?.getIntExtra(SearchLocationActivity.PARAM_RANGE, 50)
                val isIn = it.data?.getBooleanExtra(SearchLocationActivity.PARAM_DIRECTION, false)
                if (place == null || range == null || isIn == null) {
                    return@registerForActivityResult
                }
                Log.e("locationlauncher", "${place}")
                viewModel.placeData.value = place
                viewModel.rangeData.value = range
                viewModel.isInData.value = isIn
            }
        }

    override fun initView() {
    }

    override fun subscribe() {
        viewModel.placeData.observe(this) {
            binding.llCreateRoutineHow.visibility = View.GONE
            binding.llCreateRoutineHowView.visibility = View.GONE

            if (it == null) {
                binding.llCreateRoutineHow.visibility = View.VISIBLE
            } else {
                binding.llCreateRoutineHowView.visibility = View.VISIBLE

                binding.tvCreateRoutineHowHour2.text = "${it.title}"
            }
        }
        viewModel.isInData.observe(this) {
            val isInText = if (it) "들어올 때" else "벗어날 때"
            binding.tvCreateRoutineHowHour.text = "${isInText} : "
        }
        viewModel.rangeData.observe(this) {
            binding.tvCreateRoutineHowDays.text = "반경 ${it}m"
        }

        viewModel.daysData.observe(this) {
            binding.llCreateRoutineTime.visibility = View.GONE
            binding.llCreateRoutineTimeView.visibility = View.GONE

            if (it.isEmpty()) {
                binding.llCreateRoutineTime.visibility = View.VISIBLE
            } else {
                binding.llCreateRoutineTimeView.visibility = View.VISIBLE
                val days = it.map { day -> day.toDayOfKor() }.joinToString(",")
                binding.tvCreateRoutineTimeDays.text = "매주 $days"
            }
        }
    }

    override fun initEvent() {
        binding.llCreateRoutineHow.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PERMISSION_DENIED) {
                val dialog = LocationDialog.newInstance()
                dialog.setOnResultListener(object : LocationDialog.LocationDialogListener {
                    override fun onClickPositive() {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:" + this@CreateRoutineActivity.packageName)
                        }
                        systemSettingLauncher.launch(intent)
                    }

                    override fun onClickNegative() {

                    }
                })
                dialog.show(supportFragmentManager, LocationDialog.TAG)
            } else {
                locationLauncher.launch(
                    SearchLocationActivity.getIntent(this, null, 50, false)
                )
            }

        }
        binding.llCreateRoutineHowView.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PERMISSION_DENIED) {
                val dialog = LocationDialog.newInstance()
                dialog.setOnResultListener(object : LocationDialog.LocationDialogListener {
                    override fun onClickPositive() {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:" + this@CreateRoutineActivity.packageName)
                        }
                        systemSettingLauncher.launch(intent)
                    }

                    override fun onClickNegative() {

                    }
                })
                dialog.show(supportFragmentManager, LocationDialog.TAG)
            } else {
                val notNullPlace = if (viewModel.placeData.value == null) {
                    Place()
                } else {
                    viewModel.placeData.value!!
                }
                val range = if (viewModel.rangeData.value == null) {
                    50
                } else {
                    viewModel.rangeData.value!!
                }
                val notNullIsIn = if (viewModel.isInData.value == null) {
                    false
                } else {
                    viewModel.isInData.value!!
                }
                locationLauncher.launch(
                    SearchLocationActivity.getIntent(
                        this,
                        notNullPlace,
                        range,
                        notNullIsIn
                    )
                )
            }

        }
        binding.llCreateRoutineTime.setOnClickListener {
            timeLauncher.launch(TimeActivity.getIntent(this,  listOf()))
        }
        binding.llCreateRoutineTimeView.setOnClickListener {
            timeLauncher.launch(
                TimeActivity.getIntent(
                    this,
                    viewModel.daysData.value!!
                )
            )
        }
        binding.btTimeCancel.setOnClickListener {
            viewModel.removeRoutine()
            finish()
        }
        binding.btTimeComplete.setOnClickListener {
            if (viewModel.placeData.value == null) {
                return@setOnClickListener
            }
            if (viewModel.daysData.value.isNullOrEmpty()) {
                return@setOnClickListener
            }
            if (viewModel.isInData.value == null) {
                return@setOnClickListener
            }
            if (viewModel.rangeData.value == null) {
                return@setOnClickListener
            }
            viewModel.saveAlarm()
            finish()
        }
        binding.btTimeRemove.setOnClickListener {
            viewModel.removeRoutine()
            finish()
        }
    }

    override fun initData() {
        val id = intent.getLongExtra(PARAM_ALARM_ID, -1)
        if (id == -1L) {
            binding.btTimeRemove.visibility = View.GONE
            binding.btTimeCancel.visibility = View.VISIBLE
        } else {
            binding.btTimeRemove.visibility = View.VISIBLE
            binding.btTimeCancel.visibility = View.GONE
        }
        viewModel.getRoutine(id)
    }



    companion object {
        const val PARAM_ALARM_ID = "id"
        fun getIntent(context: Context, id: Long? = null): Intent {
            val intent = Intent(context, CreateRoutineActivity::class.java)
            intent.putExtra(PARAM_ALARM_ID, id)
            return intent
        }
    }
}