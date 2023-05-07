package com.vereshchagin.nikolay.pepegafood.utils

import android.view.View
import android.view.ViewGroup
import androidx.transition.Fade
import androidx.transition.TransitionManager


class StatefulLayout (
    private val root: ViewGroup, initKey: Int, initView: View
) {

    private val states = HashMap<Int, View>()
    private var currentState: Int

    init {
        states[initKey] = initView
        currentState = initKey
    }

    fun addView(key: Int, view: View) {
        states[key] = view
        view.visibility = View.GONE
    }

    fun setState(key: Int) {
        if (key == currentState) {
            return
        }

        val fade = Fade()
        fade.duration = CommonUtils.FADE_DURATION
        TransitionManager.beginDelayedTransition(root, fade)

        states[currentState]?.visibility = View.GONE
        states[key]?.visibility = View.VISIBLE
        currentState = key
    }

    companion object {
        const val LOADING_STATE = -1
        const val ERROR_STATE = -2
        const val CONTENT_STATE = -3

    }
}