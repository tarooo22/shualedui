package com.vereshchagin.nikolay.pepegafood.ui.profile.viewer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vereshchagin.nikolay.pepegafood.repository.ProfileRepository
import com.vereshchagin.nikolay.pepegafood.utils.LoadState

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    val loadState = MutableLiveData(LoadState.LOADING)


    fun profileData() = repository.profile

    fun loadProfile() = repository.loadProfile(loadState)

    fun logout() = repository.logout(loadState)

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProfileViewModel(
                ProfileRepository()
            ) as T
        }
    }
}