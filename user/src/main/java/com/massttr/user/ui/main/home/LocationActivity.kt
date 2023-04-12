package com.massttr.user.ui.main.home

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import com.common.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.massttr.user.R
import com.massttr.user.databinding.ActivityLocationBinding
import com.massttr.user.utils.AppGlobal.Companion.showDeniedPermissionDialog
import com.massttr.user.utils.AppGlobal.Companion.showRationalPermissionDialog
import com.massttr.user.utils.EventBus
import com.massttr.user.utils.WorkaroundMapFragment
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.toast
import permissions.dispatcher.*
import timber.log.Timber
import java.io.IOException
import java.util.*

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
@RuntimePermissions
class LocationActivity : BaseActivity<ActivityLocationBinding>(R.layout.activity_location),
    OnMapReadyCallback,
    View.OnClickListener {

    private var mMap: GoogleMap? = null
    private var onCameraIdleListener: GoogleMap.OnCameraIdleListener? = null
    private var latitude = ""
    private var longitude = ""
    private var provider: LocationGooglePlayServicesProvider? = null
    private var vAddress = ""
    private var isEdit = false
    private var placeAutoCompleteRequestCode = 12
    private var address: String? = ""
    private var country: String? = ""
    private var city: String? = ""
    private var state: String? = ""
    private var postalCode: String? = ""
    private var streetName: String? = ""
    private var locationLatitude = ""
    private var locationLongitude = ""


    companion object {
        const val LAT = "LAT"
        const val LONG = "LONG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        configureCameraIdle()
        clickListener()
    }

    private fun initView() {
        binding.run {
            tvSelectLocation.isEnabled = false
            tvSelectLocation.alpha = 0.5f

            provider = LocationGooglePlayServicesProvider()
            provider!!.setCheckLocationSettings(true)
            isEdit = false

            Places.initialize(applicationContext, resources.getString(R.string.maps_api_key))
            onLocationPermissionGrantedWithPermissionCheck()
        }

        if (intent.hasExtra(LAT)) locationLatitude = intent.getStringExtra(LAT) ?: ""
        if (intent.hasExtra(LONG)) locationLongitude = intent.getStringExtra(LONG) ?: ""

    }

    private fun clickListener() {
        binding.run {
            tvSelectLocation.setOnClickListener(this@LocationActivity)
            //llCurrentLocation.setOnClickListener(this)
            ivBack.setOnClickListener(this@LocationActivity)
            tvBack.setOnClickListener(this@LocationActivity)
            ivSearch.setOnClickListener(this@LocationActivity)

        }
    }

    private fun configureCameraIdle() {
        onCameraIdleListener = GoogleMap.OnCameraIdleListener {
            val latLng = mMap!!.cameraPosition.target
            val geocoder = Geocoder(
                this@LocationActivity, Locale("en")
            )

            latitude = latLng.latitude.toString()
            longitude = latLng.longitude.toString()

            try {
                val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addressList != null && addressList.size > 0) {
                    address = addressList[0].getAddressLine(0) ?: ""
                    country = addressList[0].countryName ?: ""
                    city = addressList[0].locality ?: ""
                    state = addressList[0].adminArea ?: ""
                    postalCode = addressList[0].postalCode ?: ""
                    streetName = addressList[0].thoroughfare ?: ""

                    if (address!!.isNotEmpty() && country!!.isNotEmpty()) {
                        binding.run {
                            tvLocation!!.text = (address)
                            edAddressDesc.setText(address)
                        }


                        Timber.e("MAPaddress $address")
                        Timber.e("MAPCountry $country")
                        Timber.e("MAPCity $city")
                        Timber.e("MAPState $state")
                        Timber.e("MAPPostalCode $postalCode")
                        Timber.e("MAPstreetName $streetName")
                        Timber.e("ADDRESS $addressList")
                        Timber.e("Lat: $latitude")
                        Timber.e("Long: $longitude")

                        binding.tvSelectLocation.isEnabled = true
                        binding.tvSelectLocation.alpha = 1.0f
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (!isEdit) {
            val smartLocation = SmartLocation.Builder(this).logging(true).build()
            smartLocation.location(provider).oneFix().start { location ->
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()

                setMap()
            }

        } else {
            setMap()
        }
    }

    private fun setMap() {
        onCameraIdleListener?.let {
            mMap!!.setOnCameraIdleListener(it)
        }
        val latLng = LatLng(locationLatitude.toDouble(), locationLongitude.toDouble())
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
        mMap?.animateCamera(cameraUpdate)
        mMap?.addMarker(
            MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons()))
        )
    }

    private fun resizeMapIcons(): Bitmap {
        return Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources,
                resources.getIdentifier(
                    R.drawable.ic_location_fixer.toString(),
                    "drawable",
                    this.packageName
                )
            ), 50, 50, false
        )
    }

    @NeedsPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    fun onLocationPermissionGranted() {
        if (!isEdit) {
            val smartLocation = SmartLocation.Builder(this).logging(true).build()
            smartLocation.location(provider).oneFix().start { location ->
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
                try {
                    // Loading map
                    initializeMap()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            try {
                // Loading map
                initializeMap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @OnPermissionDenied(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    fun onLocationPermissionDenied() {
        showDeniedPermissionDialog(this, "Permission Denied!, Please Allow Permission.")
    }

    @OnShowRationale(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    fun onLocationRationale(request: PermissionRequest) {
        showRationalPermissionDialog(
            this,
            "Allow Location Permission To Get Nearest Matches.",
            positiveClick = {
                request.proceed()
            },
            negativeClick = {
                request.cancel()
            })
    }

    @OnNeverAskAgain(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    fun onLocationNeverAskAgain() {
        showDeniedPermissionDialog(
            this,
            "Please Allow Location Permission From Settings."
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun callPlaceAutocompleteActivityIntent() = try {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        )
            .build(this)
        startActivityForResult(intent, placeAutoCompleteRequestCode)
    } catch (e: Exception) {
        Timber.e("Exception ${e.printStackTrace()}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == placeAutoCompleteRequestCode) {
            if (resultCode == RESULT_OK) {
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                Timber.e("SearchId: ${place?.id}")
                Timber.e("SearchName: ${place?.name}")
                Timber.e("SearchLatLng: ${place?.latLng}")

                if (place?.latLng != null) {
                    onCameraIdleListener?.let {
                        mMap!!.setOnCameraIdleListener(it)
                    }
                    val latLng = LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
                    mMap!!.animateCamera(cameraUpdate)
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = data?.let { Autocomplete.getStatusFromIntent(it) }
                Timber.e("SearchMessage: ${status?.statusMessage}")
            }
        }
    }

    private fun initializeMap() {

        handler.postDelayed({
            try {
                supportFragmentManager.findFragmentById(R.id.fragment_map)?.let {
                    (it as WorkaroundMapFragment)
                        .getMapAsync(this)
                    (it).setListener(object : WorkaroundMapFragment.OnTouchListener {
                        override fun onTouch() {
                            //scroll?.requestDisallowInterceptTouchEvent(true)
                        }
                    })
                }
            } catch (e: Exception) {
            }
        }, 1000)
    }

    override fun onBackPressed() {
        binding.ivSearch.visibility = View.GONE
        super.onBackPressed()
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.tvBack -> {
                onBackPressed()
            }

            R.id.ivSearch -> {
                callPlaceAutocompleteActivityIntent()
            }
//            R.id.llCurrentLocation -> {
//
//                val smartLocation = SmartLocation.Builder(this).logging(true).build()
//                smartLocation.location(provider).oneFix().start { location ->
//                    latitude = location.latitude.toString()
//                    longitude = location.longitude.toString()
//                    setMap()
//                }
//            }
            R.id.tvSelectLocation -> {
                binding.run {
                    if (llAddressDesc.visibility == View.GONE) {
                        llAddressDesc.visibility = View.VISIBLE
                        edAddressDesc.requestFocus()
                        edAddressDesc.setText(address)
                        tvSelectLocation.text = resources.getString(R.string.done)
                    } else {
                        if (tvLocation.text.toString().isNotEmpty()) {
                            vAddress = tvLocation.text.toString()

                            Timber.e("City: $city\nCountry Name: $country\nAddress: $address\nPostal Code: $postalCode\nStreet Name: $streetName")
                            Timber.e("Address Description: ${edAddressDesc.text}")

                            //SharedPref.latitude = latitude
                            // SharedPref.longitude = longitude

                            val bundle = Bundle()
                            bundle.putBoolean("ADDRESS_CHANGE", true)
                            bundle.putString("ADDRESS", edAddressDesc.text.toString())
                            EventBus.publish(bundle)
                            onBackPressed()

                            /*val data = Intent()
                                data.putExtra(CITY_NAME, city)
                                data.putExtra(COUNTRY_NAME, country)
                                data.putExtra(SHIPPING_ADDRESS, address)
                                data.putExtra(POSTAL_CODE, postalCode)
                                data.putExtra(STREET_NAME, streetName)
                                data.putExtra(ADDRESS_DESC, edAddressDesc.text.toString())
                                setResult(Activity.RESULT_OK, data)
                                finish()*/
                        } else {
                            toast(resources.getString(R.string.places_search_error))
//                    sneakerError
                        }
                    }
                }
            }
            R.id.ivBack -> onBackPressed()
        }
    }
}
