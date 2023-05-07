package com.vereshchagin.nikolay.pepegafood.settings

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.android.gms.maps.model.LatLng
import com.vereshchagin.nikolay.pepegafood.MainApplication
import com.vereshchagin.nikolay.pepegafood.data.UserData

class ApplicationPreference {

    companion object {

        private const val USER_ADDRESS = "user_address"
        private const val USER_ADDRESS_COORDINATES = "user_address_coordinates"

        private const val USER_DATA = "user_data"
        private const val USER_TOKEN = "user_token"
        private const val USER_USERNAME = "user_username"

        fun userAddress(): String? {
            return PreferenceManager.getDefaultSharedPreferences(MainApplication.instance)
                .getString(USER_ADDRESS, null)
        }

        fun setUserAddress(address: String) {
            PreferenceManager.getDefaultSharedPreferences(MainApplication.instance)
                .edit()
                .putString(USER_ADDRESS, address)
                .apply()
        }

        fun setUserAddressCoordinates(latitude: Double, longitude: Double) {
            PreferenceManager.getDefaultSharedPreferences(MainApplication.instance)
                .edit()
                .putString(USER_ADDRESS_COORDINATES, "$latitude;$longitude")
                .apply()
        }

        fun userAddressCoordinates(): LatLng {
            val list = PreferenceManager.getDefaultSharedPreferences(MainApplication.instance)
                .getString(USER_ADDRESS_COORDINATES, "0;0")!!.split(";")

            return LatLng(list[0].toDouble(), list[1].toDouble())
        }

        fun setUserData(data: UserData) {
            encryptedPreference().edit()
                .putString(USER_TOKEN, data.token)
                .putString(USER_USERNAME, data.username)
                .apply()
        }

        fun userData(): UserData {
            val preference = encryptedPreference()
            return UserData (
                token = preference.getString(USER_TOKEN, "")!!,
                username = preference.getString(USER_USERNAME, "")!!
            )
        }

        private fun encryptedPreference(): SharedPreferences {
            val masterKey = MasterKey.Builder(MainApplication.instance)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(
                MainApplication.instance,
                USER_DATA,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }
}