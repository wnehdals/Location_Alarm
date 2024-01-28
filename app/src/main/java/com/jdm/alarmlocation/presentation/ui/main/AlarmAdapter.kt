package com.jdm.alarmlocation.presentation.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.databinding.ItemAlarmBinding
import com.jdm.alarmlocation.databinding.ItemSpinnerBinding
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.domain.model.ListDialogItem

class AlarmAdapter(
    private val context: Context,
    private val onClickItem: (Alarm) -> Unit,
    private val onCheckedChangeListener: (Alarm) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {
    private val itemList = mutableListOf<Alarm>()
    private val onItemClick = { item: Alarm -> onClickItem.invoke(item) }
    private val onItemSwitchClick = { item: Alarm -> onCheckedChangeListener.invoke(item) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false), onItemClick, onItemSwitchClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    fun submitList(data: List<Alarm>) {
        itemList.clear()
        itemList.addAll(data)
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: ItemAlarmBinding, onClickItem: (Alarm) -> Unit, onItemSwitchClick: (item: Alarm) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clAlarmItem.setOnClickListener {
                onClickItem(itemList[bindingAdapterPosition])
            }
            binding.lsItemAlarm.setOnClickListener {
                itemList[bindingAdapterPosition].isOn = !itemList[bindingAdapterPosition].isOn
                onItemSwitchClick(itemList[bindingAdapterPosition])
            }
        }
        fun bindView(item: Alarm) {
            with(binding) {
                tvAlarmWay.text = if (item.way == 0) context.getString(R.string.str_way_0) else context.getString(R.string.str_way_1)
                tvAlarmWay.background = if (item.way == 0) ContextCompat.getDrawable(context, R.drawable.bg_r4_s_blue100_f_blue50) else ContextCompat.getDrawable(context, R.drawable.bg_r4_s_red100_f_red50)
                tvItemAlarmLocation.text = item.address
                tvItemAlarmTime.text = "${timeFormat(item.leftTimeHour)}:${timeFormat(item.leftTImeMinute)} ~ ${timeFormat(item.rightTimeHour)}:${timeFormat(item.rightTimeMinute)}"
                tvItemAlarmRange.text = "${item.range}m"
                if (item.isOn) {
                    lvItemAlarm.playAnimation()
                } else {
                    lvItemAlarm.cancelAnimation()
                }
                lsItemAlarm.isSelected = item.isOn
            }
        }
        fun timeFormat(value: Int): String {
            return String.format("%02d", value)
        }
    }
}
