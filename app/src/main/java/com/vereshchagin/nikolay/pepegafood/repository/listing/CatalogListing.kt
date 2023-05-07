package com.vereshchagin.nikolay.pepegafood.repository.listing

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.vereshchagin.nikolay.pepegafood.utils.LoadState


class CatalogListing<T>(
    val pagedList: LiveData<PagedList<T>>,
    val loadState: LiveData<LoadState>,
    val refreshState: LiveData<LoadState>,
    val refresh: () -> Unit,
    val retry: () -> Unit
)