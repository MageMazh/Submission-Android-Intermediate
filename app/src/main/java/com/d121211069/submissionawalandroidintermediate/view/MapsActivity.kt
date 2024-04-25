package com.d121211069.submissionawalandroidintermediate.view

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.d121211069.submissionawalandroidintermediate.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.d121211069.submissionawalandroidintermediate.databinding.ActivityMapsBinding
import com.d121211069.submissionawalandroidintermediate.factory.ViewModelFactory
import com.d121211069.submissionawalandroidintermediate.util.Result
import com.d121211069.submissionawalandroidintermediate.viewmodel.MapsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var token = ""
    private var lat = 0.0
    private var lon = 0.0
    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapsViewModel.getSession().observe(this) { user ->
            token = user.token
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        addManyMarker()
        setMapStyle()
    }

    private fun addManyMarker() {
        mapsViewModel.getAllStoriesWithLocation(token).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        val result = result.data.listStory

                        if (result.isEmpty()) {
                            Toast.makeText(
                                this, getString(R.string.tidak_ada_data), Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            result.forEach { data ->
                                lat = data.lat as Double
                                lon = data.lon as Double
                                if (lat < 0 && lon > 0) {
                                    val latLng = LatLng(lat, lon)
                                    mMap.addMarker(
                                        MarkerOptions().position(latLng).title(data.name)
                                            .snippet(data.description)
                                    )
                                    boundsBuilder.include(latLng)
                                }
                            }

                            val bounds: LatLngBounds = boundsBuilder.build()
                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    resources.displayMetrics.widthPixels,
                                    resources.displayMetrics.heightPixels,
                                    300
                                )
                            )
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            getString(R.string.terjadi_kesalahan) + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}