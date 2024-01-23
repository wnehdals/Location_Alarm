package com.jdm.alarmlocation.presentation.dialog

import android.view.View
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseDialogFragment
import com.jdm.alarmlocation.databinding.DialogTimePickerBinding
import java.util.Calendar

class CommonTimePickerDialog(
    val title: String,
    private val msg: String,
    private val leftText: String? = null,
    private val rightText: String,
    private val leftClick: (() -> Unit)? = null,
    private val rightClick: (Calendar) -> Unit,
    private val isCancel: Boolean = true
) : BaseDialogFragment<DialogTimePickerBinding>() {
    override val layoutResId: Int
        get() = R.layout.dialog_time_picker
    private val cal = Calendar.getInstance()
    override fun initView(view: View) {
        isCancelable = isCancel
        with(binding) {
            commonDialogEvenTitle.text = title
            commonDialogEvenMsg.text = msg
            tvDialogCommonEvenRight.text = rightText

            if (leftText != null) {
                tvDialogCommonEvenLeft.visibility = android.view.View.VISIBLE
                tvDialogCommonEvenLeft.text = leftText
            }
        }
    }


    override fun initEvent() {
        with(binding) {
            tvDialogCommonEvenRight.setOnClickListener {
                rightClick(cal)
                dialog?.dismiss()

            }
            tvDialogCommonEvenLeft.setOnClickListener {
                dialog?.dismiss()
                leftClick?.let { it() }
            }
            tpTimePickerDialog.setOnTimeChangedListener { view, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                cal.isLenient = false
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