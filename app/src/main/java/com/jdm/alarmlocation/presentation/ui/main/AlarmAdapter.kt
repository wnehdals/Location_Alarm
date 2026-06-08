package com.jdm.alarmlocation.presentation.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.databinding.ItemAlarmBinding
import com.jdm.alarmlocation.databinding.ItemSpinnerBinding
import com.jdm.alarmlocation.domain.model.Alarm
import com.jdm.alarmlocation.domain.model.ListDialogItem
import com.jdm.alarmlocation.domain.model.Routine
import com.jdm.alarmlocation.domain.toDayOfKor

class AlarmAdapter(
    private val context: Context,
    private val onClickItem: (Routine) -> Unit,
    private val onCheckedChangeListener: (Routine) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {
    private val itemList = mutableListOf<Routine>()
    private val onItemDeleteClick = { item: Routine -> onClickItem.invoke(item) }
    private val onItemSwitchClick = { item: Routine -> onCheckedChangeListener.invoke(item) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false), onItemDeleteClick, onItemSwitchClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    fun submitList(data: List<Routine>) {
        itemList.clear()
        itemList.addAll(data)
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: ItemAlarmBinding, onClickItem: (Routine) -> Unit, onItemSwitchClick: (item: Routine) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clAlarmItem.setOnClickListener {
                onClickItem(itemList[bindingAdapterPosition].copy())
            }
            binding.lsItemAlarm.setOnClickListener {
                onItemSwitchClick(itemList[bindingAdapterPosition].copy())
            }
        }
        fun bindView(item: Routine) {
            with(binding) {

                tvWay.setTextColor(ContextCompat.getColor(context, if (item.isIn) R.color.blue_400 else R.color.red_400))
                tvWay.text = if (item.isIn) "IN" else "OUT"
                tvItemAlarmWay.text = if (item.isIn) context.getString(R.string.str_way_0) else context.getString(R.string.str_way_1)
                tvItemAlarmLocationDays.text = item.day.map { it.toDayOfKor() }.joinToString(",")
                tvItemAlarmLocation.text = "${item.placeTitle.parseAsHtml()}"
                lsItemAlarm.isChecked = item.isOn
            }
        }
        fun timeFormat(value: Int): String {
            return String.format("%02d", value)
        }
    }
}
