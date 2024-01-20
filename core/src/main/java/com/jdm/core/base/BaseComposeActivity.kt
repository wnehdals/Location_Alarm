package com.jdm.core.base

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

abstract class BaseComposeActivity : ComponentActivity(){
    protected open var onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.e("test", "onbackpressed")
            finish()
            //slideLeft()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
    protected fun exitApp() {
        this.moveTaskToBack(true)
        this.finish()
        System.exit(0)
    }
    fun goToActivity(intent: Intent) {
        startActivity(intent)
    }

    override fun finish() {
        super.finish()
    }
    protected fun isPermissionAllow(permission: String): Boolean {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
    protected fun isPermissionAllow(permission: Array<String>): Boolean {
        return permission.filter { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED }.isEmpty()
    }

}