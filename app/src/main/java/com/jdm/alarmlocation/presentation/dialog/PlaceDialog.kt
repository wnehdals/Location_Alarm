package com.jdm.alarmlocation.presentation.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.base.BaseBottomSheetDialogFragment
import com.jdm.alarmlocation.databinding.DialogListBinding
import com.jdm.alarmlocation.domain.model.ListDialogItem
import com.jdm.alarmlocation.domain.model.Place

class PlaceDialog(
    private val context: Context,
    private val listData: List<Place>,
    private val onClickItem: (Place) -> Unit,
) : BaseBottomSheetDialogFragment<DialogListBinding>() {

    override val layoutResId: Int
        get() = R.layout.dialog_list
    override var heightPercent: Float = 0.8f
    private val listDialogAdapter: PlaceDialogAdapter by lazy {
        PlaceDialogAdapter(
            context,
            this::onClickDialogItem
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.TopRoundBottomDialog)
            .apply {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
    }

    override fun initView(view: View) {
        with(binding) {
            rvDialogContentsFilter.adapter = listDialogAdapter
            rvDialogContentsFilter.itemAnimator = null
            rvDialogContentsFilter.setHasFixedSize(true)
        }
    }

    override fun initEvent() {
        with(binding) {
            ivDialogTagRecordDetail.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun subscribe() {
    }

    override fun initData() {
        listDialogAdapter.submitList(listData)
    }

    private fun onClickDialogItem(item: Place) {
        onClickItem(item)
        dismiss()
    }

    companion object {
        val TAG = "PlaceDialog"
    }
}