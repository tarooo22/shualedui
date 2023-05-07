package com.vereshchagin.nikolay.pepegafood.repository.boundary

import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import com.vereshchagin.nikolay.pepegafood.utils.createStatusLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor


abstract class BaseBoundaryCalback<A, D, R, I> (
    protected val api: A,
    protected val dao: D,
    protected val ioExecutor: Executor,
    protected val handelResponse: (R?) -> Unit
) : PagedList.BoundaryCallback<I>() {

    val helper = PagingRequestHelper(ioExecutor)
    val loadState = helper.createStatusLiveData()

    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            onZeroLoaded()
        }
    }

   abstract fun onZeroLoaded()

    override fun onItemAtEndLoaded(itemAtEnd: I) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            onEndLoaded(itemAtEnd)
        }
    }

    abstract fun onEndLoaded(itemAtEnd: I)

    private fun addItemsIntoDb(response: Response<R>,
                               callback: PagingRequestHelper.Request.Callback) {
        ioExecutor.execute {
            handelResponse(response.body())
            callback.recordSuccess()
        }
    }

    private fun apiCallback(callback: PagingRequestHelper.Request.Callback): Callback<R> {
        return object : Callback<R> {
            override fun onFailure(call: Call<R>, t: Throwable) {
                callback.recordFailure(t)
            }

            override fun onResponse(call: Call<R>, response: Response<R>) {
                addItemsIntoDb(response, callback)
            }
        }
    }
}