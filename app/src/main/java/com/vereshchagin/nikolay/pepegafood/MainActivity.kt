package com.vereshchagin.nikolay.pepegafood

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.vereshchagin.nikolay.pepegafood.databinding.ActivityMainBinding
import com.vereshchagin.nikolay.pepegafood.settings.ApplicationPreference
import com.vereshchagin.nikolay.pepegafood.utils.CommonUtils
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
       const val TAG = "MainActivityLog"
       const val PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)


        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_catalog, R.id.nav_basket, R.id.nav_delivery, R.id.nav_profile)
        )

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            setAddressActionBar(destination.id == R.id.nav_home)
        }

        binding.addressToolbar.setOnClickListener {
            navController.navigate(R.id.to_address_fragment)
        }

        // TODO("Test badge)
        val badge = binding.navView.getOrCreateBadge(R.id.nav_basket)
        badge.verticalOffset = 5
        badge.maxCharacterCount = 3
        badge.number = 100
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateUserLocation()
                return
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setAddressActionBar(enable: Boolean) {
        if (enable) {
            binding.toolbar.title = ""
            updateUserLocation()
        }
        binding.addressToolbar.visibility = CommonUtils.toVisibly(enable)
    }

    private fun updateUserLocation() {

        val userAddress = ApplicationPreference.userAddress()
        if (userAddress != null) {
            binding.addressLocation.text = userAddress
            return
        }


        if (checkLocationPermission()) {

            val request = LocationRequest()
            request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(request, object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult?) {
                        super.onLocationResult(result)

                        LocationServices.getFusedLocationProviderClient(this@MainActivity)
                            .removeLocationUpdates(this)

                        if (result != null && result.locations.isNotEmpty()) {
                            val locationIndex = result.locations.size - 1
                            val latitude = result.locations[locationIndex].latitude
                            val longitude = result.locations[locationIndex].longitude

                            val coder = Geocoder(this@MainActivity, Locale.getDefault())

                            // получение адреса
                            try {
                                val location = coder.getFromLocation(latitude, longitude, 1)
                                if (location == null) {
                                    binding.addressLocation.setText(R.string.address_not_set)
                                    return
                                }

                                val address = location[0]
                                val formatAddress = CommonUtils.addressToString(address)
                                binding.addressLocation.text = formatAddress
                                ApplicationPreference.setUserAddress(formatAddress)
                                ApplicationPreference.setUserAddressCoordinates(latitude, longitude)

                            } catch (ignored: Exception) {
                                binding.addressLocation.setText(R.string.address_not_set)
                            }
                        } else {
                            binding.addressLocation.setText(R.string.address_not_set)
                        }
                    }
                }, Looper.getMainLooper())

        } else {
            binding.addressLocation.setText(R.string.address_not_set)
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        ), PERMISSION_REQUEST_CODE)

        return false
    }
}