package com.vereshchagin.nikolay.pepegafood.ui.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vereshchagin.nikolay.pepegafood.ui.home.repository.HomeRepository


class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    private val repositoryListing = MutableLiveData(repository.content())
    val shoppingBaskets = Transformations.switchMap(repositoryListing) { it.shoppingBaskets }
    val favoriteBaskets = Transformations.switchMap(repositoryListing) { it.favoriteBaskets }
    val catalogItems = Transformations.switchMap(repositoryListing) { it.catalogItems }


    class Factory (
        private val application: Application
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(
                    HomeRepository(application)
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}