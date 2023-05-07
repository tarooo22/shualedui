package com.vereshchagin.nikolay.pepegafood.ui.profile.viewer

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vereshchagin.nikolay.pepegafood.R
import com.vereshchagin.nikolay.pepegafood.databinding.FragmentProfileBinding
import com.vereshchagin.nikolay.pepegafood.settings.ApplicationPreference
import com.vereshchagin.nikolay.pepegafood.ui.BaseStateFragment
import com.vereshchagin.nikolay.pepegafood.ui.profile.login.LoginActivity
import com.vereshchagin.nikolay.pepegafood.utils.LoadState
import com.vereshchagin.nikolay.pepegafood.utils.StatefulLayout
import com.vereshchagin.nikolay.pepegafood.utils.StatefulLayout.Companion.ERROR_STATE
import com.vereshchagin.nikolay.pepegafood.utils.StatefulLayout.Companion.LOADING_STATE

/**
 * Фрагмент профиля.
 */
class ProfileFragment : BaseStateFragment<FragmentProfileBinding>() {

    companion object {
        private const val PROFILE_STATE = 1
        private const val LOGIN_STATE = 2
    }

    /**
     * ViewModel фрагмента профиля.
     */
    private val viewModel by viewModels<ProfileViewModel> {
        ProfileViewModel.Factory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onInflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun onStatefulLayout() : StatefulLayout {
        val stateful = StatefulLayout(binding.profileContainer,
            LOADING_STATE, binding.profileLoading)

        stateful.addView(PROFILE_STATE, binding.profileInfo)
        stateful.addView(LOGIN_STATE, binding.profileLoginContainer)
        stateful.addView(ERROR_STATE, binding.profileErrorContainer)

        return stateful
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_profile, menu)

        // val showLogout = viewModel.profileData() != null
        // menu.findItem(R.id.log_out).isVisible = showLogout
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.log_out) {
            viewModel.logout()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Вход в аккаунт
        binding.profileLogin.setOnClickListener {
            val loginIntent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.profileErrorRetry.setOnClickListener {
            viewModel.loadProfile()
        }

        viewModel.loadState.observe(viewLifecycleOwner, Observer {
            val loadState = it ?: return@Observer

            when (loadState.state) {
                LoadState.State.RUNNING -> {
                    stateful.setState(LOADING_STATE)
                }
                LoadState.State.SUCCESS -> {
                    val user = viewModel.profileData()

                    if (user == null) {
                        stateful.setState(LOGIN_STATE)
                        return@Observer
                    }

                    binding.profileName.text = user.fullName()
                    binding.profilePhone.text = user.phone
                    binding.profileEmail.text = user.email
                    binding.profileAddress.text = ApplicationPreference.userAddress()

                    stateful.setState(PROFILE_STATE)
                }
                LoadState.State.FAILED -> {
                    binding.profileError.text = loadState.msg

                    stateful.setState(ERROR_STATE)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProfile()
    }
}