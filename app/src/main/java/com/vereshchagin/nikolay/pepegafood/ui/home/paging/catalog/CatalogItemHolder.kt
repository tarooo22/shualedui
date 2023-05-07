package com.vereshchagin.nikolay.pepegafood.ui.home.paging.catalog

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vereshchagin.nikolay.pepegafood.databinding.ItemCatalogBinding
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.CatalogHomeItem


class CatalogItemHolder (
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemCatalogBinding.bind(itemView)

    /**
     * Присоединяет данные к holder'у.
     */
    fun bind(catalog: CatalogHomeItem?) {
        binding.categoryName.text = catalog?.title
    }
}