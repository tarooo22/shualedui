package com.vereshchagin.nikolay.pepegafood.api.payloads



class RegistrationPayload (
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val address: String,
    val password: String
)