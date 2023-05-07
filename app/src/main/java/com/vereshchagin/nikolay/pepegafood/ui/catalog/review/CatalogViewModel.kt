package com.vereshchagin.nikolay.pepegafood.ui.catalog.review

import android.app.Application
import androidx.lifecycle.*
import com.vereshchagin.nikolay.pepegafood.repository.CatalogRepository


class CatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CatalogRepository(application)
    private val repositoryListing = MutableLiveData(repository.catalogItems())
    val catalogItems = Transformations.switchMap(repositoryListing) { it.pagedList }
    val loadState = Transformations.switchMap(repositoryListing) { it.loadState }
    val refreshState = Transformations.switchMap(repositoryListing) { it.refreshState }

    fun refresh() {
        repositoryListing.value?.refresh?.invoke()
    }

    fun retry() {
        repositoryListing.value?.retry?.invoke()
    }

    class Factory(val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CatalogViewModel(application) as T
        }
    }
}