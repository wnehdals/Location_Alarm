package com.jdm.alarmlocation.presentation.dialog

import android.view.View
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseBottomSheetDialogFragment
import com.jdm.alarmlocation.databinding.DialogLocationBinding

class LocationDialog: BaseBottomSheetDialogFragment<DialogLocationBinding>() {
    override val layoutResId: Int
        get() = R.layout.dialog_location
    private var listener: LocationDialogListener? = null
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

    fun setOnResultListener(listener: LocationDialogListener) {
        this.listener = listener
    }



    interface LocationDialogListener {
        fun onClickPositive()
        fun onClickNegative()
    }
    companion object {
        fun newInstance(): LocationDialog {
            return LocationDialog()
        }
        const val TAG = "LocationDialog"
    }
}