package com.vereshchagin.nikolay.pepegafood.utils

import android.content.Context
import android.location.Address
import android.util.DisplayMetrics
import android.view.View
import kotlin.math.roundToInt


/**
 * Вспомогательные функции.
 */
class CommonUtils {

    companion object {

        const val FADE_DURATION = 300L

        /**
         * В зависимости от visible возвращает значение видимости для View.
         */
        fun toVisibly(visible: Boolean) = if (visible) View.VISIBLE else View.GONE

        /**
         * Представляет объект адреса в виде строки.
         */
        fun addressToString(address: Address): String {
            val addressLines = ArrayList<String>()
            for (index in 0..address.maxAddressLineIndex) {
                address.getAddressLine(index)?.let {
                    addressLines.add(it)
                }
            }
            return addressLines.joinToString(", ")
        }

        /**
         * Пероводит Display point в Pixel point.
         */
        fun dpToPx(context: Context, dp: Int): Int {
            val displayMetrics: DisplayMetrics = context.resources.displayMetrics
            return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
        }
    }
}