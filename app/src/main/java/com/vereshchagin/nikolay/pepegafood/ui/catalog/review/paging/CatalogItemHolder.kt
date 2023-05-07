package com.vereshchagin.nikolay.pepegafood.ui.catalog.review.paging

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.pepegafood.databinding.ItemCatalogBinding
import com.vereshchagin.nikolay.pepegafood.model.CatalogItem


class CatalogItemHolder (
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemCatalogBinding.bind(itemView)


    fun bind(catalog: CatalogItem?) {
        binding.categoryName.text = catalog?.title
    }
}