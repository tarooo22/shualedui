package com.vereshchagin.nikolay.pepegafood.ui.catalog.review.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.vereshchagin.nikolay.pepegafood.R
import com.vereshchagin.nikolay.pepegafood.model.CatalogItem

class CatalogItemAdapter : PagedListAdapter<CatalogItem, CatalogItemHolder>(CATALOG_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_catalog, parent, false)
        return CatalogItemHolder(view)
    }

    override fun onBindViewHolder(holder: CatalogItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {

        val CATALOG_COMPARATOR = object : DiffUtil.ItemCallback<CatalogItem>() {

            override fun areItemsTheSame(oldItem: CatalogItem, newItem: CatalogItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CatalogItem, newItem: CatalogItem) =
                oldItem == newItem
        }
    }
}