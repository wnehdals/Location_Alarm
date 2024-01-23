package com.jdm.alarmlocation.presentation.ui.main

import android.content.Intent
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseActivity
import com.jdm.alarmlocation.databinding.ActivityMainBinding
import com.jdm.alarmlocation.presentation.ui.create.CreateAlarmActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_main

    override fun initView() {
    }

    override fun subscribe() {
    }

    override fun initEvent() {
        with(binding) {
            fab.setOnClickListener {
                val intent = Intent(this@MainActivity, CreateAlarmActivity::class.java)
                goToActivity(intent)
            }
        }

    }

    override fun initData() {
    }
}