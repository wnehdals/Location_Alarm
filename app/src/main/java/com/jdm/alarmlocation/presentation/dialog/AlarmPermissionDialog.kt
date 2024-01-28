package com.jdm.alarmlocation.presentation.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseBottomSheetDialogFragment
import com.jdm.alarmlocation.databinding.DialogNotiPermissionBinding
import com.jdm.alarmlocation.domain.model.ListDialogItem

class AlarmPermissionDialog(
    private val onClickItem: () -> Unit,
) : BaseBottomSheetDialogFragment<DialogNotiPermissionBinding>() {

    override val layoutResId: Int
    get() = R.layout.dialog_noti_permission
    override var heightPercent: Float = 0.8f

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.TopRoundBottomDialog)
            .apply {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                isCancelable = false
            }
    }
    override fun initView(view: View) {
    }

    override fun initEvent() {
        with(binding) {
            tvDialogCommonEvenRight.setOnClickListener {
                dismiss()
                onClickItem
            }
        }
    }

    override fun subscribe() {
    }

    override fun initData() {
    }
    companion object {
        val TAG = "AlarmPermissionDialog"
    }
}
