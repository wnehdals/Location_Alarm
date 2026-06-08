package com.jdm.alarmlocation.presentation.dialog

import android.view.View
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseBottomSheetDialogFragment
import com.jdm.alarmlocation.databinding.DialogReminderBinding

class ReminderDialog: BaseBottomSheetDialogFragment<DialogReminderBinding>() {
    override val layoutResId: Int
        get() = R.layout.dialog_reminder
    private var listener: ReminderDialogListener? = null
    override fun initView(view: View) {
        isCancelable = false
    }

    override fun initEvent() {
        binding.tvDialogCommonEvenTop.setOnClickListener {
            dialog?.dismiss()
            listener?.onClickPositive()
        }
        binding.tvDialogCommonEvenBottom.setOnClickListener {
            dialog?.dismiss()
            listener?.onClickNegative()
        }
    }

    override fun subscribe() {
    }

    override fun initData() {
    }

    fun setOnResultListener(listener: ReminderDialogListener) {
        this.listener = listener
    }



    interface ReminderDialogListener {
        fun onClickPositive()
        fun onClickNegative()
    }
    companion object {
        fun newInstance(): ReminderDialog {
            return ReminderDialog()
        }
        const val TAG = "ReminderDialog"
    }
}