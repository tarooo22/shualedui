package com.vereshchagin.nikolay.pepegafood.api

import com.vereshchagin.nikolay.pepegafood.api.payloads.*
import com.vereshchagin.nikolay.pepegafood.api.responses.CheckTokenResponse
import com.vereshchagin.nikolay.pepegafood.api.responses.LoginResponse
import com.vereshchagin.nikolay.pepegafood.api.responses.RegistrationResponse
import com.vereshchagin.nikolay.pepegafood.api.responses.UserInfoResponse
import retrofit2.Call
import retrofit2.http.*


interface PepegaFoodApi {

    @POST("/api/identity/login")
    fun login(@Body payload: LoginPayload) : Call<LoginResponse>


    @POST("/api/identity/regist")
    fun registration(@Body payload: RegistrationPayload) : Call<RegistrationResponse>


    @GET("/api/identity/check_jwt")
    fun checkToken(@Header("Authorization") bearerToken: String) : Call<CheckTokenResponse>


    @GET("/api/identity/get_user_info/{username}")
    fun userInfo(@Header("Authorization") bearerToken: String,
                 @Path("username") username: String) : Call<UserInfoResponse>


    @PUT("/api/ClientData/change_password/{username}")
    fun changePassword(@Header("Authorization") bearerToken: String,
                       @Path("username") username: String,
                       @Body payload: ChangePasswordPayload) // : ??????????


    @PUT("/api/ClientData/change_email/{username}")
    fun changeEmail(@Header("Authorization") bearerToken: String,
                    @Path("username") username: String,
                    @Body payload: ChangeEmailPayload) // : ??????????


    @PUT("/api/ClientData/change_first_name/{username}")
    fun changeFirstName(@Header("Authorization") bearerToken: String,
                        @Path("username") username: String,
                        @Body payload: ChangeFirstNamePayload) // : ??????????


    @PUT("/api/ClientData/change_last_name/{username}")
    fun changeLastName(@Header("Authorization") bearerToken: String,
                       @Path("username") username: String,
                       @Body payload: ChangeLastNamePayload) // : ??????????

}