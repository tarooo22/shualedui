package com.vereshchagin.nikolay.pepegafood.repository

import androidx.lifecycle.MutableLiveData
import com.tarashvili.nikolay.pepegafood.BuildConfig
import com.vereshchagin.nikolay.pepegafood.api.PepegaFoodApi
import com.vereshchagin.nikolay.pepegafood.api.responses.UserInfoResponse
import com.vereshchagin.nikolay.pepegafood.data.UserData
import com.vereshchagin.nikolay.pepegafood.settings.ApplicationPreference
import com.vereshchagin.nikolay.pepegafood.utils.LoadState
import com.vereshchagin.nikolay.pepegafood.utils.NetworkUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ProfileRepository {

    var profile: UserInfoResponse? = null
        private set

    private val ioExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private val retrofit: Retrofit
    private val api: PepegaFoodApi

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

    fun loadProfile(loadState: MutableLiveData<LoadState>) {
        loadState.value = LoadState.LOADING
        ioExecutor.execute {
            val userDate = ApplicationPreference.userData()
            if (userDate.isEmpty()) {
                profile = null
                loadState.postValue(LoadState.LOADED)
                return@execute
            }

            api.userInfo(userDate.bearer, userDate.username).enqueue(
                object : Callback<UserInfoResponse> {
                    override fun onResponse(
                        call: Call<UserInfoResponse>, response: Response<UserInfoResponse>
                    ) {
                        userInfoResponse(response, loadState)
                    }
                    override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                        loadState.value = LoadState.error(t.message ?: "Unknown error")
                    }
                }
            )
        }
    }

    fun logout(loadState: MutableLiveData<LoadState>) {
        loadState.value = LoadState.LOADING
        ioExecutor.execute {
            ApplicationPreference.setUserData(UserData("", ""))
            profile = null
            loadState.postValue(LoadState.LOADED)
        }
    }

    private fun userInfoResponse(
        response: Response<UserInfoResponse>,
        loadState: MutableLiveData<LoadState>
    ) {
        if (!response.isSuccessful) {
            val msg = when (response.code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> "401 Unauthorized. Invalid bearer token. Please sign in again."
                else -> response.errorBody()?.string() ?: "Unknown error"
            }
            loadState.value = LoadState.error(msg)
            return
        }

        profile = response.body()!!
        loadState.value = LoadState.LOADED
    }
}