package com.vereshchagin.nikolay.pepegafood.ui.basket.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.vereshchagin.nikolay.pepegafood.R
import com.vereshchagin.nikolay.pepegafood.model.BasketItem

class BasketItemAdapter : PagedListAdapter<BasketItem, BasketItemHolder>(BASKET_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_basket, parent, false)
        return BasketItemHolder(view)
    }

    override fun onBindViewHolder(holder: BasketItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {

        val BASKET_COMPARATOR = object : DiffUtil.ItemCallback<BasketItem>() {

            override fun areItemsTheSame(oldItem: BasketItem, newItem: BasketItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: BasketItem, newItem: BasketItem) =
                oldItem == newItem
        }
    }
}