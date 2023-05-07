package com.vereshchagin.nikolay.pepegafood.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.vereshchagin.nikolay.pepegafood.utils.StatefulLayout

abstract class BaseStateFragment<T : ViewBinding> : BaseFragment<T>() {

    private var _stateful: StatefulLayout? = null
    protected val stateful get() = _stateful!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        _stateful = onStatefulLayout()
        return view
    }

    abstract fun onStatefulLayout(): StatefulLayout

    override fun onDestroyView() {
        super.onDestroyView()
        _stateful = null
    }
}