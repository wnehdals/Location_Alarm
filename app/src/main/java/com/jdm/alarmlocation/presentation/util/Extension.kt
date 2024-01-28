package com.jdm.alarmlocation.presentation.ui.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.TypedValue
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jdm.alarmlocation.R

//*************************************************************************************************************************************************
// Context, Activity, Fragment
//

fun Context.hasPermissions(permissions: Array<String>): Boolean =
    permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
fun AppCompatActivity.requestMultiplePermission(
    permissions: Array<String>,
    passAction: () -> Unit,
    failAction: () -> Unit
) {
    val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        var permissionFlag = true
        for (entry in it.entries) {
            if (!entry.value) {
                permissionFlag = false
                break
            }
        }
        if (!permissionFlag) {
            failAction()
        } else {
            passAction()
        }
    }
    launcher.launch(permissions)
}

fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp,
    resources.displayMetrics
).toInt()

fun Activity.slideLeft() {
    overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
}

fun Activity.slideRight() {
    overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
}

fun Activity.slideUp() {
    overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_exit)
}

fun Activity.slideDown() {
    overridePendingTransition(R.anim.slide_down_enter, R.anim.slide_down_exit)
}
val Int.toPx get() = (this * Resources.getSystem().displayMetrics.density).toInt()

