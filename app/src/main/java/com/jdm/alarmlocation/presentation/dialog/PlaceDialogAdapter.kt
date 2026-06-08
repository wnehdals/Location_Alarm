package com.jdm.alarmlocation.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import com.jdm.alarmlocation.databinding.ItemPlaceBinding
import com.jdm.alarmlocation.domain.model.ListDialogItem
import com.jdm.alarmlocation.domain.model.Place

class PlaceDialogAdapter(
    private val context: Context,
    private val onClickItem: (Place) -> Unit
) : RecyclerView.Adapter<PlaceDialogAdapter.ViewHolder>() {
    private val itemList = mutableListOf<Place>()
    private val onItemClick = { item: Place -> onClickItem.invoke(item) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false), onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    fun submitList(data: List<Place>) {
        itemList.clear()
        itemList.addAll(data)
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: ItemPlaceBinding, onClickItem: (Place) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.clItemListDialog.setOnClickListener {
                onClickItem(itemList[bindingAdapterPosition])
            }
        }
        fun bindView(item: Place) {
            with(binding) {
                tvItemPlaceDialog.text = item.title.parseAsHtml()
                tvItemPlaceAddress.text = item.roadAddress
                tvItemPlaceCategory.text = item.category
            }
        }
    }
}
