package com.jdm.core.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.Surface
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import java.util.concurrent.atomic.AtomicInteger

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {
    @get:LayoutRes
    abstract val layoutResId: Int
    lateinit var binding: T
        private set




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResId)
        initState()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }


    abstract fun initView()
    abstract fun subscribe()
    abstract fun initEvent()
    abstract fun initData()


    open fun initState() {
        initView()
        initEvent()
        subscribe()
        initData()
    }

    fun goToActivity(intent: Intent) {
        startActivity(intent)
    }
    protected fun isPermissionAllow(permission: String): Boolean {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
    protected fun isPermissionAllow(permission: Array<String>): Boolean {
        return permission.none { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
    }
    protected fun exitApp() {
        this.moveTaskToBack(true)
        this.finish()
        System.exit(0)
    }
}
