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
import com.vereshchagin.nikolay.pepegafood.api.responses.CatalogItemResponse
import com.vereshchagin.nikolay.pepegafood.model.CatalogItem
import com.vereshchagin.nikolay.pepegafood.repository.boundary.CatalogBoundaryCallback
import com.vereshchagin.nikolay.pepegafood.repository.listing.CatalogListing
import com.vereshchagin.nikolay.pepegafood.utils.LoadState
import com.vereshchagin.nikolay.pepegafood.utils.NetworkUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CatalogRepository(context: Context) {

    private val retrofit: Retrofit
    private val api: PepegaFoodApi

    private val db = MainApplicationDatabase.database(context)
    private val dao = db.catalog()

    private val ioExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val builder = Retrofit.Builder()
            .baseUrl(NetworkUtils.API_URL)
            .addConverterFactory(GsonConverterFactory.create())

        // включение лога
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

    private fun addCatalogIntoDb(response: CatalogItemResponse?) {

    }

    @MainThread
    private fun refresh() : LiveData<LoadState> {
        val loadState = MutableLiveData(LoadState.LOADED)
        return loadState
    }

    fun catalogItems(size: Int = 20) : CatalogListing<CatalogItem> {
        val boundaryCallback = CatalogBoundaryCallback(
            api, dao, ioExecutor, this::addCatalogIntoDb
        )

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }

        val pagedList = LivePagedListBuilder(dao.all(), size)
            .setBoundaryCallback(boundaryCallback)
            .build()

        // Test
        ioExecutor.execute {
            db.runInTransaction {
                val list = ArrayList<CatalogItem>()
                for (i in 0..20) {
                    list.add(CatalogItem(i, "Title $i", "", i))
                }
                dao.insert(list)
            }
        }

        return CatalogListing(
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