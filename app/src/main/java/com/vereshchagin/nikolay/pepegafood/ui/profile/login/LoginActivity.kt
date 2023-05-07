package com.vereshchagin.nikolay.pepegafood.ui.profile.login

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.vereshchagin.nikolay.pepegafood.R
import com.vereshchagin.nikolay.pepegafood.databinding.ActivityLoginBinding
import com.vereshchagin.nikolay.pepegafood.utils.CommonUtils
import com.vereshchagin.nikolay.pepegafood.utils.LoadState

/**
 * Активность для входа в приложение.
 */
class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginActivityLog"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // переключение вкладок
        binding.tabs.addOnButtonCheckedListener { group, _, _ ->
            showLoginTab(group.checkedButtonId == R.id.login_tab)
        }

        viewModel = ViewModelProvider(this, LoginViewModel.Factory())
            .get(LoginViewModel::class.java)


        initLoginTab()
        initRegistrationTab()

        // статус загрузки
        viewModel.loadingState.observe(this, Observer { loadState ->
            if (loadState == null) {
                enabledForms(true)
                showLoading(false)
                return@Observer
            }

            when (loadState.state) {
                LoadState.State.RUNNING -> {
                    enabledForms(false)
                    showLoading(true)
                }
                LoadState.State.SUCCESS -> {
                    if (viewModel.isLogin) {
                        onBackPressed()
                    } else {
                        registrationSuccess()
                    }
                }
                LoadState.State.FAILED -> {
                    enabledForms(true)
                    showLoading(false)

                    AlertDialog.Builder(this)
                        .setTitle(R.string.error)
                        .setMessage(loadState.msg)
                        .setPositiveButton(R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setCancelable(true)
                        .show()
                }
            }
        })
    }

    /**
     * Инициализация вкладки входа.
     */
    private fun initLoginTab() {
        // проверка формы для входа в систему
        viewModel.loginFormState.observe(this, Observer {
            val loginState = it ?: return@Observer

            binding.loginButton.isEnabled = loginState.isDataValid

            binding.loginEmailLayout.error = if (loginState.emailError != null)
                getString(loginState.emailError) else null

            binding.loginPasswordLayout.error = if (loginState.passwordError != null)
                getString(loginState.passwordError) else null
        })

        binding.loginEmail.doAfterTextChanged {
            updateLoginForm()
        }

        binding.loginPassword.doAfterTextChanged {
            updateLoginForm()
        }

        binding.loginButton.setOnClickListener {
            updateLoginForm()
            if (!viewModel.loginFormState.value?.isDataValid!!) {
                return@setOnClickListener
            }

            viewModel.login(
                binding.loginEmail.text.toString(),
                binding.loginPassword.text.toString()
            )
        }
    }

    /**
     * Инициализация вкладки регистрации.
     */
    private fun initRegistrationTab() {
        viewModel.registrationFormState.observe(this, Observer {
            val registrationState = it ?: return@Observer

            binding.registrationButton.isEnabled = registrationState.isDataValid

            binding.registrationFirstNameLayout.error = if (registrationState.firstNameError != null)
                getString(registrationState.firstNameError) else null

            binding.registrationLastNameLayout.error = if (registrationState.lastNameError != null)
                getString(registrationState.lastNameError) else null

            binding.registrationEmailLayout.error = if (registrationState.emailError != null)
                getString(registrationState.emailError) else null

            binding.registrationPhoneLayout.error = if (registrationState.phoneError != null)
                getString(registrationState.phoneError) else null

            binding.registrationPasswordLayout.error = if (registrationState.passwordError != null)
                getString(registrationState.passwordError) else null

            binding.registrationConfirmPasswordLayout.error = if (registrationState.conformPasswordError != null)
                getString(registrationState.conformPasswordError) else null
        })

        binding.registrationFirstName.doAfterTextChanged {
            updateRegistrationForm()
        }

        binding.registrationUserLastName.doAfterTextChanged {
            updateRegistrationForm()
        }

        binding.registrationEmail.doAfterTextChanged {
            updateRegistrationForm()
        }

        binding.registrationPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        binding.registrationPhone.doAfterTextChanged {
            updateRegistrationForm()
        }

        binding.registrationPassword.doAfterTextChanged {
            updateRegistrationForm()
        }

        binding.registrationConfirmPassword.doAfterTextChanged {
            updateRegistrationForm()
        }

        binding.registrationButton.setOnClickListener {
            updateRegistrationForm()
            if (!viewModel.registrationFormState.value?.isDataValid!!) {
                return@setOnClickListener
            }

            viewModel.registration(
                binding.registrationFirstName.text.toString(),
                binding.registrationUserLastName.text.toString(),
                binding.registrationEmail.text.toString(),
                binding.registrationPhone.text.toString(),
                binding.registrationPassword.text.toString()
            )
        }
    }

    /**
     * Обновляет форму входа.
     */
    private fun updateLoginForm() {
        viewModel.loginDataChanged(
            binding.loginEmail.text.toString(),
            binding.loginPassword.text.toString()
        )
    }

    /**
     * Обновляет форму регистрации.
     */
    private fun updateRegistrationForm() {
        viewModel.registrationDataChanged(
            binding.registrationFirstName.text.toString(),
            binding.registrationUserLastName.text.toString(),
            binding.registrationEmail.text.toString(),
            binding.registrationPhone.text.toString(),
            binding.registrationPassword.text.toString(),
            binding.registrationConfirmPassword.text.toString()
        )
    }

    /**
     * Вызывается, если регистрация прошла успешно.
     */
    private fun registrationSuccess() {
        AlertDialog.Builder(this)
            .setTitle(R.string.login_success_registration)
            .setMessage(R.string.login_success_registration_msg)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()

        enabledForms(true)
        showLoading(false)

        copyLoginData()
        showLoginTab(true)
        clearRegistrationForm()

        binding.loginButton.requestFocus()
        binding.tabs.check(R.id.login_tab)
    }

    /**
     * Копирует данные для входа из формы регистрации.
     */
    private fun copyLoginData() {
        binding.loginEmail.setText(binding.registrationEmail.text.toString())
        binding.loginPassword.setText(binding.registrationPassword.text.toString())
    }

    /**
     * Очищает форму регистрации.
     */
    private fun clearRegistrationForm() {
        binding.registrationFirstName.text?.clear()
        binding.registrationUserLastName.text?.clear()
        binding.registrationEmail.text?.clear()
        binding.registrationPhone.text?.clear()
        binding.registrationPassword.text?.clear()
        binding.registrationConfirmPassword.text?.clear()
    }

    /**
     * Переключает вкладки: вход / регистрация.
     */
    private fun showLoginTab(show: Boolean) {
        val fade = Fade()
        fade.duration = CommonUtils.FADE_DURATION
        TransitionManager.beginDelayedTransition(binding.loginLayout, fade)

        binding.loginContent.visibility = CommonUtils.toVisibly(show)
        binding.registrationContent.visibility = CommonUtils.toVisibly(!show)
    }

    /**
     * Показывает / скрывает View загрузки.
     */
    private fun showLoading(show: Boolean) {
        binding.loginLoading.visibility = CommonUtils.toVisibly(show)
    }

    /**
     * Блокирует формы ввода.
     */
    private fun enabledForms(enabled: Boolean) {
        binding.loginEmail.isEnabled = enabled
        binding.loginPassword.isEnabled = enabled
        binding.registrationFirstName.isEnabled = enabled
        binding.registrationUserLastName.isEnabled = enabled
        binding.registrationEmail.isEnabled = enabled
        binding.registrationPhone.isEnabled = enabled
        binding.registrationPassword.isEnabled = enabled
        binding.registrationConfirmPassword.isEnabled = enabled

        binding.loginButton.isEnabled = enabled
        binding.registrationButton.isEnabled = enabled

        binding.loginTab.isEnabled = enabled
        binding.registrationTab.isEnabled = enabled
    }
}
