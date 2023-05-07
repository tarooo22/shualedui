package com.vereshchagin.nikolay.pepegafood.utils;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingRequestHelper

private fun getErrorMessage(report: PagingRequestHelper.StatusReport): String {
    return PagingRequestHelper.RequestType.values().mapNotNull {
        report.getErrorFor(it)?.message
    }.first()
}

fun PagingRequestHelper.createStatusLiveData(): LiveData<LoadState> {
    val liveData = MutableLiveData<LoadState>()
    addListener { report ->
        when {
            report.hasRunning() -> liveData.postValue(LoadState.LOADING)
            report.hasError() -> liveData.postValue(
                LoadState.error(
                    getErrorMessage(
                        report
                    )
                ))
            else -> liveData.postValue(LoadState.LOADED)
        }
    }
    return liveData
}