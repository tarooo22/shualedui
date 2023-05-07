package com.vereshchagin.nikolay.pepegafood.api.responses

import com.google.gson.annotations.SerializedName


class UserInfoResponse (
    val firstName: String,
    val lastName: String,
    val email: String,
    @SerializedName("phoneNumber") val phone: String,
    val address: String,
    @SerializedName("userName") val username: String
) {
    fun fullName() = "$firstName $lastName"
}