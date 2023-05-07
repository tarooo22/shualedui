package com.vereshchagin.nikolay.pepegafood.data


class UserData (
    val token: String,
    val username: String
) {
    val bearer get() = "bearer $token"

    fun isEmpty() = token.isEmpty() || username.isEmpty()
}