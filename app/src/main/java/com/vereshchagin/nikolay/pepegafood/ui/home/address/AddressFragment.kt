package com.vereshchagin.nikolay.pepegafood.ui.home.address

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.vereshchagin.nikolay.pepegafood.R
import com.vereshchagin.nikolay.pepegafood.databinding.FragmentAddressBinding
import com.vereshchagin.nikolay.pepegafood.settings.ApplicationPreference
import com.vereshchagin.nikolay.pepegafood.utils.CommonUtils
import com.vereshchagin.nikolay.pepegafood.utils.LoadState
import com.vereshchagin.nikolay.pepegafood.utils.StatefulLayout


class AddressFragment : Fragment() {

    companion object {
        private const val TAG = "AddressFragmentLog"

        private const val MAP_SCALE = 15f

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!

    private var _stateful: StatefulLayout? = null
    private val stateful get() = _stateful!!

    private lateinit var client: PlacesClient
    private lateinit var viewModel: AddressViewModel


    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext().applicationContext, getString(R.string.google_maps_key))
        }
        client = Places.createClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        _stateful = StatefulLayout(binding.root, StatefulLayout.LOADING_STATE, binding.loadingLayout.loading)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateful.addView(StatefulLayout.CONTENT_STATE, binding.addressContent)

        // Callback для карты
        val mapFragment = childFragmentManager.findFragmentById(R.id.address_map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            this.map = map
            map.setOnCameraIdleListener {
                viewModel.updateUserAddress(map.cameraPosition.target)
            }

            checkLocationPermission()
            updateMapUI()

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(viewModel.coordinates, MAP_SCALE))

            stateful.setState(StatefulLayout.CONTENT_STATE)
        }

        // Autocomplete для адреса
        val completeFragment = childFragmentManager.findFragmentById(R.id.address_complete)
                as AutocompleteSupportFragment
        completeFragment.setPlaceFields(listOf(Place.Field.LAT_LNG))
        completeFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                viewModel.updateUserAddress(place.latLng)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, MAP_SCALE))
            }

            override fun onError(status: Status) {
                Log.e(TAG, status.toString())
            }
        })

        // нажата кнопка "установить позицию"
        binding.selectAddress.setOnClickListener {
            // если местомоложение есть
            if (viewModel.state.value == LoadState.LOADED) {
                val coordinates = viewModel.coordinates
                val address = viewModel.currentAddress

                if (address != null) {
                    activity?.let { activity ->
                        ApplicationPreference.setUserAddressCoordinates(
                            coordinates.latitude, coordinates.longitude
                        )
                        ApplicationPreference.setUserAddress(
                            address
                        )
                        activity.onBackPressed()
                    }
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this,
            AddressViewModel.Factory(activity?.application!!))
            .get(AddressViewModel::class.java)

        // загрузка адреса
        viewModel.state.observe(viewLifecycleOwner, Observer { loadState ->
            when (loadState.state) {
                LoadState.State.RUNNING -> {
                    binding.currentAddress.setText(R.string.address_loading)
                }
                LoadState.State.SUCCESS -> {
                    val address = viewModel.currentAddress
                    if (address != null) {
                        binding.currentAddress.text = address
                    } else {
                        binding.currentAddress.setText(R.string.address_error)
                    }
                }
                LoadState.State.FAILED -> {
                    binding.currentAddress.setText(R.string.address_error)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _stateful = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // разрешение к местоположению
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateMapUI()
                return
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Обновляет элементы на карте.
     */
    private fun updateMapUI() {
        map?.let { map ->
            if (checkLocationPermission()) {
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true
                map.setPadding(0, CommonUtils.dpToPx(requireContext(), 60), 0, 0)
            }
            map.uiSettings.isZoomControlsEnabled = true
        }
    }


    private fun checkLocationPermission(): Boolean {
        context?.let { context ->
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        }


        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
        return false
    }
}