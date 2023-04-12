package com.massttr.user.ui.main.home

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.common.base.BaseFragment
import com.common.data.network.model.HomeMapGetFixerResponse
import com.common.data.network.model.request.HomeMapGetFixer
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.massttr.user.R
import com.massttr.user.databinding.FragmentHomeBinding
import com.massttr.user.ui.language.login.register.create_profile.location.LocationActivity
import com.massttr.user.ui.main.home.taskrequest.TaskRequestActivity
import com.massttr.user.utils.*
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import permissions.dispatcher.*
import timber.log.Timber
import java.io.IOException
import java.util.*

//@RuntimePermissions
@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home),
    View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnCameraIdleListener, GoogleMap.OnInfoWindowClickListener {

    private val viewModel: HomeFragmentViewModel by viewModels()
    private var mMap: GoogleMap? = null
    private var onCameraIdleListener: GoogleMap.OnCameraIdleListener? = null
    private var latitude = ""
    private var longitude = ""
    private var provider: LocationGooglePlayServicesProvider? = null
    private var placeAutoCompleteRequestCode = 12
    private var address: String? = ""
    private var country: String? = ""
    private var city: String? = ""
    private var state: String? = ""
    private var postalCode: String? = ""
    private var streetName: String? = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    var mapFragment: SupportMapFragment? = null
    private var lat: Double = 0.0
    private var long: Double = 0.0

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setUpObserver()
        configureCameraIdle()
        createLocationRequest()
        clickListener()
        getBusEvent()
    }

    private fun initView() {
        Timber.d("fcmToken: ${pref.fcmToken}")
        viewModel.editProfile()
        mapFragment = childFragmentManager.findFragmentById(R.id.gMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        // onLocationPermissionGrantedWithPermissionCheck()
        provider = LocationGooglePlayServicesProvider()
        provider!!.setCheckLocationSettings(true)
        // isEdit = false

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                //placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }

        Places.initialize(requireActivity(), resources.getString(R.string.maps_api_key))
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    private fun configureCameraIdle() {
        onCameraIdleListener = GoogleMap.OnCameraIdleListener {
            val latLng = mMap?.cameraPosition?.target
            val geocoder = Geocoder(requireActivity(), Locale.getDefault())

            latitude = latLng?.latitude.toString()
            longitude = latLng?.longitude.toString()

            try {
                val addressList =
                    latLng?.let { geocoder.getFromLocation(it.latitude, latLng.longitude, 1) }
                if (addressList != null && addressList.size > 0) {
                    address = addressList[0].getAddressLine(0) ?: ""
                    country = addressList[0].countryName ?: ""
                    city = addressList[0].locality ?: ""
                    state = addressList[0].adminArea ?: ""
                    postalCode = addressList[0].postalCode ?: ""
                    streetName = addressList[0].thoroughfare ?: ""

                    if (address!!.isNotEmpty() && country!!.isNotEmpty()) {
                        // tvLocation!!.text = (address)
                        // edAddressDesc.setText(address)

                        binding.EtSearch.setText(pref.userInfo?.address)

                        Timber.e("MAPaddress $address")
                        Timber.e("MAPCountry $country")
                        Timber.e("MAPCity $city")
                        Timber.e("MAPState $state")
                        Timber.e("MAPPostalCode $postalCode")
                        Timber.e("MAPstreetName $streetName")
                        Timber.e("ADDRESS $addressList")
                        Timber.e("Lat: $latitude")
                        Timber.e("Long: $longitude")

                        //  tvSelectLocation.isEnabled = true
                        //  tvSelectLocation.alpha = 1.0f
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun setUpObserver() {
        viewModel.run {
            appLoader.observe(requireActivity()) { updateLoaderUI(it) }
            apiErrors.observe(requireActivity()) { handleError(it) }
            mapGetFixer.observe(requireActivity()) {
                it.data?.forEach { homeMapGetFixer ->
                    setMarker(
                        homeMapGetFixer.location_latitude,
                        homeMapGetFixer.location_longitude,
                        homeMapGetFixer
                    )
                }
            }
            editProfile.observe(requireActivity()) {
                long = it.data.long
                lat = it.data.lat
                setMap()
            }
        }
    }

    private fun setMarker(
        lat: Double,
        lng: Double,
        fixerData: HomeMapGetFixerResponse,
    ) {
        if (fixerData != null) {
            mMap?.let {
                lat.let { it1 ->
                    lng.let { it2 ->
                        val sydney = LatLng(it1, it2)
                        mMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                        /*mMap?.addMarker(MarkerOptions().position(sydney)
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons())))*/
                        mMap?.addMarker(
                            MarkerOptions()
                                .position(sydney)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_fixer))
                        )!!.tag = fixerData

                        /*rippleEffect(
                            sydney,
                            mainData,
                            ContextCompat.getColor(MyApplication.getInstance().mContext, R.color.greenLight),
                            BitmapDescriptorFactory.fromResource(R.drawable.green_dot)
                        )*/
                    }
                }
            }
        }
    }


    private fun resizeMapIcons(): Bitmap {
        return Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources,
                resources.getIdentifier(
                    R.drawable.ic_location_fixer.toString(),
                    "drawable",
                    context?.packageName
                )
            ), 50, 50, false
        )
    }

    private fun clickListener() {
        binding.run {
            ivTaskStart.setOnClickListener(this@HomeFragment)
            EtSearch.setOnClickListener(this@HomeFragment)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        val markerWindowView = context?.let {
            CustomFixerInfoView(it) { number ->
                val numbers: Uri = Uri.parse("tel:$number")
                val callIntent = Intent(Intent.ACTION_DIAL, numbers)
                context?.startActivity(callIntent)
            }
        }
        if (markerWindowView != null) {
            mMap?.setInfoWindowAdapter(markerWindowView)
        }
        val smartLocation = SmartLocation.Builder(requireActivity()).logging(true).build()
        smartLocation.location(provider).oneFix().start { location ->
            latitude = location.latitude.toString()
            longitude = location.longitude.toString()

            val mapStyleOptions =
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle)
            mMap?.setMapStyle(mapStyleOptions)

            // mMap?.uiSettings?.isZoomControlsEnabled = true
            mMap?.setOnMarkerClickListener(this)
            mMap?.setOnInfoWindowClickListener(this)
            setUpMap()

        }
    }

    override fun onMarkerClick(p0: Marker) = false

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        mMap?.isMyLocationEnabled = true

        onCameraIdleListener?.let {
            mMap!!.setOnCameraIdleListener(it)
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                //placeMarkerOnMap(currentLatLng)
                //  mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                Timber.d("Location-----------> $currentLatLng")
                Timber.d("Address-----------> ${getAddress(currentLatLng)}")
                viewModel.getFixerMap(HomeMapGetFixer(latitude, longitude))
            }
        }
    }

    private fun getAddress(latLng: LatLng): String {
        // 1
        val geocoder = Geocoder(requireActivity())
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && addresses.isNotEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(
                        i
                    )
                }
            }
        } catch (e: IOException) {
            Timber.e(e.localizedMessage)
        }

        return addressText
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(
                        requireActivity(),
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

//    private fun loadPlacePicker() {
//        val builder = PlacePicker.IntentBuilder()
//
//        try {
//            startActivityForResult(builder.build(this@MapsActivity), PLACE_PICKER_REQUEST)
//        } catch (e: GooglePlayServicesRepairableException) {
//            e.printStackTrace()
//        } catch (e: GooglePlayServicesNotAvailableException) {
//            e.printStackTrace()
//        }
//    }

    private fun setMap() {
        onCameraIdleListener?.let {
            mMap!!.setOnCameraIdleListener(it)
        }
        val latLng = LatLng(lat, long)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
        mMap?.animateCamera(cameraUpdate)
    }

    //    @NeedsPermission(
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION
//    )
    fun onLocationPermissionGranted() {
//        if (!isEdit) {
//            val smartLocation = SmartLocation.Builder(requireActivity()).logging(true).build()
//            smartLocation.location(provider).oneFix().start { location ->
//                latitude = location.latitude.toString()
//                longitude = location.longitude.toString()
//                try {
//                    // Loading map
//                    initializeMap()
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//            }
//        } else {
        try {
            // Loading map
            initializeMap()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        //  }
    }

//    @OnPermissionDenied(
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION
//    )
//    fun onLocationPermissionDenied() {
//        showDeniedPermissionDialog(requireActivity(),
//            "Permission Denied!, Please Allow Permission.")
//    }

//    @OnShowRationale(
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION
//    )
//    fun onLocationRationale(request: PermissionRequest) {
//        showRationalPermissionDialog(
//            requireActivity(),
//            "Allow Location Permission To Get Nearest Matches.",
//            positiveClick = {
//                request.proceed()
//            },
//            negativeClick = {
//                request.cancel()
//            })
//    }

//    @OnNeverAskAgain(
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION
//    )
//    fun onLocationNeverAskAgain() {
//        showDeniedPermissionDialog(
//            requireActivity(),
//            "Please Allow Location Permission From Settings."
//        )
//    }


    private fun callPlaceAutocompleteActivityIntent() = try {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).build(requireActivity())
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
                binding.EtSearch.setText(place?.name)

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
        try {
            requireActivity().supportFragmentManager.findFragmentById(R.id.gMap)?.let {
                (it as WorkaroundMapFragment)
                    .getMapAsync(this@HomeFragment)
                (it).setListener(object : WorkaroundMapFragment.OnTouchListener {
                    override fun onTouch() {
                        //scroll?.requestDisallowInterceptTouchEvent(true)
                    }
                })
                it.getMapAsync {


                    //  setUpMap()
                    // mMap = it
                }
            }
        } catch (e: Exception) {
            Timber.e("exception $e")
            e.message
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivTaskStart ->
                requireActivity().startActivity<TaskRequestActivity>(
                    TaskRequestActivity.LATITUDE to lat.toString(),
                    TaskRequestActivity.LONGITUDE to long.toString(),
                    TaskRequestActivity.ADDRESS to pref.userInfo?.address
                )
            R.id.EtSearch -> {
                v.closeSoftKeyboard()
                requireActivity().startActivity<LocationActivity>("lat" to lat, "log" to long)
            }

        }
    }

    @ObsoleteCoroutinesApi
    private fun getBusEvent() {
        EventBus.subscribe<Bundle>(listSubscription) {
            if (it.containsKey("ADDRESS_CHANGE")) {
                Timber.e("ADDRESS_CHANGE")
                val address = it.getBoolean("ADDRESS_CHANGE", false)
                val aDDRESSEdit = it.getString("ADDRESS")

                if (address) binding.EtSearch.setText(aDDRESSEdit)
            }
        }
    }

    override fun onCameraIdle() {
        mMap?.clear()
    }

    override fun onInfoWindowClick(marker: Marker) {
        Timber.e("click")
        val data = marker.tag as HomeMapGetFixerResponse
        requireActivity().call(data.phone_no)
        /*val data = marker?.tag as TaskList
        if (data.job_status_id == 1) {
            // requireContext().startActivity<AvailableFixDetailActivity>(GOOGLETAG_FIXDETAIL to data.id)
        }*/
    }
}



