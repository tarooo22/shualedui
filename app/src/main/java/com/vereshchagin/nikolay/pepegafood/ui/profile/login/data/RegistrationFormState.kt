package com.vereshchagin.nikolay.pepegafood.ui.profile.login.data

class RegistrationFormState (
    val firstNameError: Int? = null,
    val lastNameError: Int? = null,
    val emailError: Int? = null,
    val phoneError: Int? = null,
    val passwordError: Int? = null,
    val conformPasswordError: Int? = null,
    val isDataValid: Boolean = false
)