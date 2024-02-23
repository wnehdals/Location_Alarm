package com.jdm.alarmlocation.presentation.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseBottomSheetDialogFragment
import com.jdm.alarmlocation.base.BaseDialogFragment
import com.jdm.alarmlocation.databinding.DialogCommonEvenBinding
import com.jdm.alarmlocation.databinding.DialogPermissionBinding

class PermissionDialog(
    private val context: Context,
    @DrawableRes private val icon: Int,
    private val msg: String,
    private val permissionName: String,
    private val rightClick: () -> Unit,
    private val isCancel: Boolean = true
) : BaseBottomSheetDialogFragment<DialogPermissionBinding>() {
    override val layoutResId: Int
        get() = R.layout.dialog_permission
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.TopRoundBottomDialog)
            .apply {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                isCancelable = isCancel
            }
    }
    override fun initView(view: View) {
        isCancelable = isCancel
        binding.ivLocation.setImageDrawable(ContextCompat.getDrawable(context, icon))
        binding.commonDialogEvenMsg.text = msg
        binding.tvPermissionName.text = permissionName
    }

    override fun initEvent() {
        with(binding) {
            tvDialogCommonEvenRight.setOnClickListener {
                dialog?.dismiss()
                rightClick()
            }
        }
    }

    override fun subscribe() {
    }

    override fun initData() {
    }

    companion object {
        val TAG = this.javaClass.simpleName
    }
}
