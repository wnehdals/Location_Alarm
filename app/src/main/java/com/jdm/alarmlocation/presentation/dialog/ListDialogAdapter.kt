package com.jdm.alarmlocation.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jdm.alarmlocation.databinding.ItemSpinnerBinding
import com.jdm.alarmlocation.domain.model.ListDialogItem

class ListDialogAdapter(
    private val context: Context,
    private val onClickItem: (ListDialogItem) -> Unit
) : RecyclerView.Adapter<ListDialogAdapter.ViewHolder>() {
    private val itemList = mutableListOf<ListDialogItem>()
    private val onItemClick = { item: ListDialogItem -> onClickItem.invoke(item) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSpinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false), onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    fun submitList(data: List<ListDialogItem>) {
        itemList.clear()
        itemList.addAll(data)
        notifyDataSetChanged()
    }
    private fun clearItem() {
        itemList.forEach { it.isSelected = false }
    }
    fun itemChange(item: ListDialogItem) {
        clearItem()
        if (itemList.isEmpty())
            return

        var index = 0
        for(i in itemList.indices) {
            if (item.id == itemList[i].id) {
                index = i
                break
            }
        }
        itemList[index].isSelected = true
        notifyItemChanged(index)
    }
    fun getSelectedItem(): ListDialogItem {
        return itemList.filter { it.isSelected == true }.first()
    }
    inner class ViewHolder(val binding: ItemSpinnerBinding, onClickItem: (ListDialogItem) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clItemListDialog.setOnClickListener {
                onClickItem(itemList[bindingAdapterPosition])
            }
        }
        fun bindView(item: ListDialogItem) {
            with(binding) {
                tvItemListDialog.text = item.text
                tvItemListDialog.isSelected = item.isSelected
            }
        }
    }
}
