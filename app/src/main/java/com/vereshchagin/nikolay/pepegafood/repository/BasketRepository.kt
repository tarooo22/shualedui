package com.vereshchagin.nikolay.pepegafood.repository

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import com.tarashvili.nikolay.pepegafood.BuildConfig
import com.vereshchagin.nikolay.pepegafood.MainApplicationDatabase
import com.vereshchagin.nikolay.pepegafood.api.PepegaFoodApi
import com.vereshchagin.nikolay.pepegafood.api.responses.BasketItemResponse
import com.vereshchagin.nikolay.pepegafood.model.BasketItem
import com.vereshchagin.nikolay.pepegafood.repository.boundary.BasketBoundaryCallback
import com.vereshchagin.nikolay.pepegafood.repository.listing.BasketListing
import com.vereshchagin.nikolay.pepegafood.utils.LoadState
import com.vereshchagin.nikolay.pepegafood.utils.NetworkUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BasketRepository(context: Context) {

    private val retrofit: Retrofit
    private val api: PepegaFoodApi


    private val db = MainApplicationDatabase.database(context)
    private val dao = db.basket()

    private val ioExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val builder = Retrofit.Builder()
            .baseUrl(NetworkUtils.API_URL)
            .addConverterFactory(GsonConverterFactory.create())


        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            builder.client(client)
        }

        retrofit = builder.build()
        api = retrofit.create(PepegaFoodApi::class.java)
    }

    private fun addBasketIntoDb(response: BasketItemResponse?) {

    }

    @MainThread
    private fun refresh() : LiveData<LoadState> {
        val loadState = MutableLiveData(LoadState.LOADED)
        return loadState
    }

    fun basketItems(size: Int = 20) : BasketListing<BasketItem> {
        val boundaryCallback = BasketBoundaryCallback(
            api, dao, ioExecutor, this::addBasketIntoDb
        )

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }

        val pagedList = LivePagedListBuilder(dao.all(), size)
            .setBoundaryCallback(boundaryCallback)
            .build()

        return BasketListing(
            pagedList,
            boundaryCallback.loadState,
            refreshState,
            refresh = {
                refreshTrigger.value = null
            },
            retry = {
                boundaryCallback.helper.retryAllFailed()
            }
        )
    }
}