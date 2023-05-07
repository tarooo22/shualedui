package com.vereshchagin.nikolay.pepegafood.ui.home.paging.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.vereshchagin.nikolay.pepegafood.R
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.FavoriteBasket

/**
 * Адаптер для списка избранных корзин.
 */
class FavoriteBasketAdapter
    : PagedListAdapter<FavoriteBasket, FavoriteBasketItemHolder>(
    BASKET_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteBasketItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_basket, parent, false)
        return FavoriteBasketItemHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: FavoriteBasketItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {

        /**
         * Компаратор для сравнения избранных корзин.
         */
        val BASKET_COMPARATOR = object : DiffUtil.ItemCallback<FavoriteBasket>() {

            override fun areItemsTheSame(oldItem: FavoriteBasket, newItem: FavoriteBasket) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FavoriteBasket, newItem: FavoriteBasket) =
                oldItem.title == newItem.title
        }
    }
}