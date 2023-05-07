package com.vereshchagin.nikolay.pepegafood.ui.home.paging.shopping

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.pepegafood.databinding.ItemShoppingBasketBinding
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.ShoppingBasket


class ShoppingBasketItemHolder(
    itemView: View
): RecyclerView.ViewHolder(itemView) {

    private val binding = ItemShoppingBasketBinding.bind(itemView)


    fun bind(basket: ShoppingBasket?) {
        binding.foodBasketCost.text = basket?.text
    }
}