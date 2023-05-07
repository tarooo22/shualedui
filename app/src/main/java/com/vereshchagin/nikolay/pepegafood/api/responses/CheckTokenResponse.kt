package com.vereshchagin.nikolay.pepegafood.api.responses

import com.google.gson.annotations.SerializedName


class CheckTokenResponse (
    val tokenActive: Boolean,
    @SerializedName("username") val username: String
)