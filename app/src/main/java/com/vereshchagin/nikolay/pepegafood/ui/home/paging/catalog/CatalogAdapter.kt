package com.vereshchagin.nikolay.pepegafood.ui.home.paging.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.vereshchagin.nikolay.pepegafood.R
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.model.CatalogHomeItem


class CatalogAdapter : PagedListAdapter<CatalogHomeItem, CatalogItemHolder>(
    CATALOG_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catalog, parent, false)
        return CatalogItemHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: CatalogItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {

        /**
         * Компаратор для сравнения авто-корзин.
         */
        val CATALOG_COMPARATOR = object : DiffUtil.ItemCallback<CatalogHomeItem>() {

            override fun areItemsTheSame(oldItem: CatalogHomeItem, newItem: CatalogHomeItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CatalogHomeItem, newItem: CatalogHomeItem) =
                oldItem.title == newItem.title
        }
    }
}