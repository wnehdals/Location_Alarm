package com.jdm.alarmlocation.presentation.ui.location

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.databinding.ItemSearchLocationBinding
import com.jdm.alarmlocation.domain.model.NameLocation

class SearchLocationAdapter(
    private val context: Context,
    private val onClickNotice: (NameLocation) -> Unit
) : ListAdapter<NameLocation, SearchLocationAdapter.ViewHolder>(diffUtil) {
    private val onItemClick = { item: NameLocation ->
        onClickNotice.invoke(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = ItemSearchLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemView, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = getItem(position)
        holder.bindView(item)
    }

    inner class ViewHolder(val binding: ItemSearchLocationBinding, onItemClick: (NameLocation) -> Unit) : RecyclerView.ViewHolder(
        binding.root
    ) {
        init {
            binding.llSearchLocationItem.setOnClickListener { onItemClick(currentList[bindingAdapterPosition]) }
        }
        fun bindView(item: NameLocation) {
            with(binding) {
                tvSearchLocationItemTitle.text = item.name
                tvSearchLocationItemSubtitle.text = "${context.getString(R.string.str_latitude)}:${item.latitude} / ${context.getString(R.string.str_longitude)}:${item.longitude}"
            }
        }
    }
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<NameLocation>() {
            override fun areItemsTheSame(oldItem: NameLocation, newItem: NameLocation): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NameLocation, newItem: NameLocation): Boolean {
                return oldItem == newItem
            }
        }
    }
}
