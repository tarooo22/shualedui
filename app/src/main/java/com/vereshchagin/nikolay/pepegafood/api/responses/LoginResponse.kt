package com.vereshchagin.nikolay.pepegafood.api.responses

import com.google.gson.annotations.SerializedName


class LoginResponse (
    val token: String,
    val success: Boolean,
    @SerializedName("userName") val username: String
    // val errorsMessages
)