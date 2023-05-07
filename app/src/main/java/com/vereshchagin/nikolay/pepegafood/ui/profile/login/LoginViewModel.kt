package com.vereshchagin.nikolay.pepegafood.ui.profile.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vereshchagin.nikolay.pepegafood.R
import com.vereshchagin.nikolay.pepegafood.ui.profile.login.data.LoginFormState
import com.vereshchagin.nikolay.pepegafood.ui.profile.login.data.RegistrationFormState
import com.vereshchagin.nikolay.pepegafood.ui.profile.login.repository.LoginRepository
import com.vereshchagin.nikolay.pepegafood.utils.LoadState

/**
 * ViewModel для авторизации.
 */
class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _registrationForm = MutableLiveData<RegistrationFormState>()
    val registrationFormState: LiveData<RegistrationFormState> = _registrationForm

    val loadingState = MutableLiveData<LoadState>(null)
    var isLogin: Boolean = true
        private set

    /**
     * Выполняет вход в систему.
     */
    fun login(email: String, password: String) {
        loginRepository.login(email, password, loadingState)
        isLogin = true
    }

    /**
     * Регистрирцет пользователя
     */
    fun registration(firstName: String, lastName: String, email: String,
                     phone: String, password: String) {
        loginRepository.registration(firstName, lastName, email, phone, password, loadingState)
        isLogin = false
    }

    /**
     * Проверяет форму входа на правильность.
     */
    fun loginDataChanged(email: String, password: String) {
        when {
            !isEmailValid(email) -> {
                _loginForm.value =
                    LoginFormState(
                        emailError = R.string.login_email_invalid
                    )
            }
            !isPasswordValid(password) -> {
                _loginForm.value =
                    LoginFormState(
                        passwordError = R.string.login_password_invalid
                    )
            }
            else -> {
                _loginForm.value =
                    LoginFormState(
                        isDataValid = true
                    )
            }
        }
    }

    /**
     * Проверяет форму регистрации на правильность.
     */
    fun registrationDataChanged(firstName: String, lastName: String, email: String, phone: String,
                                password: String, confirmPassword: String) {
        when {
            !isNameValid(firstName) -> {
                _registrationForm.value =
                    RegistrationFormState(
                        firstNameError = R.string.login_first_name_invalid
                    )
            }
            !isNameValid(lastName) -> {
                _registrationForm.value =
                    RegistrationFormState(
                        lastNameError = R.string.login_last_name_invalid
                    )
            }
            !isEmailValid(email) -> {
                _registrationForm.value =
                    RegistrationFormState(
                        emailError = R.string.login_email_invalid
                    )
            }
            !isPhoneValid(phone) -> {
                _registrationForm.value =
                    RegistrationFormState(
                        phoneError = R.string.login_phone_invalid
                    )
            }
            !isPasswordValid(password) -> {
                _registrationForm.value =
                    RegistrationFormState(
                        passwordError = R.string.login_password_invalid
                    )
            }
            !isConfirmPasswordValid(password, confirmPassword) -> {
                _registrationForm.value =
                    RegistrationFormState(
                        conformPasswordError = R.string.login_confirm_password_invalid
                    )
            }
            else -> {
                _registrationForm.value =
                    RegistrationFormState(
                        isDataValid = true
                    )
            }
        }
    }

    /**
     * Проверяет на правильномть введенного имени пользователя.
     */
    private fun isNameValid(userName: String) = userName.isNotEmpty()

    /**
     * Проверяет на правильномть введенного email.
     */
    private fun isEmailValid(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    /**
     * Проверяет на правильность введенного телефона.
     */
    private fun isPhoneValid(phone: String) = Patterns.PHONE.matcher(phone).matches()

    /**
     * Проверяет на правильность введенного пароля.
     */
    private fun isPasswordValid(password: String) = password.length >= 6

    /**
     * Проверяет на правильность подтвержение введенного пароля.
     */
    private fun isConfirmPasswordValid(password: String, confirmPassword: String) =
        password == confirmPassword

    /**
     * Factory для создания LoginViewModel.
     */
    class Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(
                    LoginRepository()
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}