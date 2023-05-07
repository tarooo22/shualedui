package com.vereshchagin.nikolay.pepegafood.utils

class LoadState private constructor(val state: State, val msg: String = "") {

    enum class State {
        RUNNING,
        SUCCESS,
        FAILED
    }

    companion object {
        val LOADING = LoadState(State.RUNNING)
        val LOADED = LoadState(State.SUCCESS)
        val EMPTY_ERROR = LoadState(State.FAILED)

        fun error(msg: String) = LoadState(State.FAILED, msg)
    }
}