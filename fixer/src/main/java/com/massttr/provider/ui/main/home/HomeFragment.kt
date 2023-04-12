package com.massttr.provider.ui.main.home

import android.Manifest
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.SeekBar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.common.base.BaseFragment
import com.common.data.network.model.NearByJobsResponse
import com.common.data.network.model.TaskList
import com.common.data.network.model.request.AvailableTask
import com.common.data.network.model.request.FixerLocation
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.massttr.provider.R
import com.massttr.provider.databinding.FragmentHomeBinding
import com.massttr.provider.ui.main.availableTasks.AvailableTaskViewModel
import com.massttr.provider.ui.main.availableTasks.viewTask.ViewTaskAcceptActivity
import com.massttr.user.utils.AppGlobal
import com.massttr.user.utils.CustomMarkerInfoWindowView
import com.massttr.user.utils.GOOGLETAG_FIXDETAIL
import com.massttr.user.utils.MapRipple
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.jetbrains.anko.startActivity
import permissions.dispatcher.*
import timber.log.Timber
import java.util.*

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
@RuntimePermissions
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home),
    OnMapReadyCallback,
    View.OnClickListener,
    GoogleMap.OnCameraIdleListener,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener {
    private val viewModel: AvailableTaskViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var mMap: GoogleMap? = null
    private var isMapReady: Boolean = false
    private var latitude : String? = null
    private var longitude = ""
    private var currantLatitude = ""
    private var currantLongitude = ""
    private var distance: Int = pref.kmProgress ?: 50
    private val pulseCount = 1
    private val animationDuration = (pulseCount + 1) * 400
    private var circles = Array<Circle?>(pulseCount) { null }

    private var lastUserCircle: Circle? = null
    private val pulseDuration: Long = 1000
    private var lastPulseAnimator: ValueAnimator? = null

    private var mRedPoint: GroundOverlay? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initMap(savedInstanceState)
        setUpObserver()
        onMarkerClick()
        setCurrantLocation()
        setNearBYJobs()
        clickListener()
        onLocationPermissionGrantedWithPermissionCheck()
    }

    override fun onStart() {
        super.onStart()
        binding.run {
            tvKm.text = pref.kmProgress.toString().plus(requireActivity().getString(R.string.km_))
            if (pref.kmProgress == 0) {
                seekBar.progress = 5
                pref.kmProgress = 5
                distance = pref.kmProgress ?: 5
                viewModel.nearByJobs(AvailableTask(pref.kmProgress ?: 0 * 1))
                // setNearBYJobs()
            } else {
                seekBar.progress = pref.kmProgress ?: 5
            }
        }
    }

    fun initView() {
        Timber.d("fcmToken: ${pref.fcmToken}")
        binding.run {
            // seekBar.progress = pref.kmProgress ?: 50
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    distance = seekBar.progress
                    pref.kmProgress = progress
                    tvKm.text = distance.toString().plus(requireActivity().getString(R.string.km_))
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    distance = seekBar.progress
                    tvKm.text = distance.toString().plus(requireActivity().getString(R.string.km_))
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    distance = seekBar.progress
                    tvKm.text = distance.toString().plus(requireActivity().getString(R.string.km_))
                    setNearBYJobs()
                }
            })
        }
            viewModel.getProfile()
        // getLocation()
    }

    @NeedsPermission(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    fun onLocationPermissionGranted() {
        getLocation()
    }

    @OnPermissionDenied(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    fun onLocationPermissionDenied() {
        AppGlobal.showDeniedPermissionDialog(
            requireContext(),
            "Permission Denied!, Please Allow Permission."
        )
    }

    @OnShowRationale(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    fun onLocationRationale(request: PermissionRequest) {
        AppGlobal.showRationalPermissionDialog(
            requireContext(),
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
        AppGlobal.showDeniedPermissionDialog(
            requireContext(),
            "Please Allow Location Permission From Settings."
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                mcontext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mcontext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()
                    currantLatitude = latitude
                    currantLongitude = longitude
                    Timber.e("lat $latitude log $longitude")
                    viewModel.fixerLocation(FixerLocation(latitude, longitude))
                    if (isMapReady) {
                        context?.let { mContext ->
                            if (isLocationEnabled(mContext))
                                setMap()
                            else
                                AppGlobal.alertDialog(
                                    context,
                                    "Please enabled Location First",
                                    "Location Enabled"
                                )
                        }
                    }
                }
            }
    }

    private fun initMap(savedInstanceState: Bundle?) {
        try {
            binding.run {
                isMapReady = false
                mapView.onCreate(savedInstanceState)
                mapView.onResume()
                MapsInitializer.initialize(mcontext)
                mapView.getMapAsync(this@HomeFragment)
            }
        } catch (e: Exception) {
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            val lm: LocationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            lm.isLocationEnabled
        } else {
            // This is Deprecated in API 28
            val mode: Int = Settings.Secure.getInt(
                context.contentResolver, Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            mode != Settings.Secure.LOCATION_MODE_OFF
        }
    }


    private fun setUpObserver() {
        viewModel.run {
            apiErrors.observe(requireActivity()) { handleError(it) }
            appLoader.observe(requireActivity()) { updateLoaderUI(it) }
            availableTask.observe(requireActivity()) { it ->
                it.data?.data?.forEach {
                    //  setMarker(it.location_latitude.toDouble(), it.location_longitude.toDouble(), it)
                }
            }
            fixerLocation.observe(requireActivity()) {
//                showMessage("Location set successfully")
                Timber.e("latitude::: ${it.location_latitude}")
                Timber.e("longitude::: ${it.location_longitude}")
            }

            getProfile.observe(requireActivity()){
               // viewModel.fixerLocation(FixerLocation(it.location_latitude.toString(), it.location_longitude.toString()))


            }
            nearByJob.observe(requireActivity()) {
                //  if(it.status){
                mMap?.clear()
                it.data.forEach { latlong ->
                    setMarker(
                        latlong.location_latitude.toDouble(),
                        latlong.location_longitude.toDouble(),
                        latlong
                    )
                }
//                }else{
//                    mMap?.clear()
//                }
            }
        }
    }

    private fun clickListener() {
        binding.run {
            ivCurrentLocation.setOnClickListener(this@HomeFragment)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        Timber.e("mapSet")
        mMap = map
        isMapReady = true

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
        val markerWindowView = context?.let {
            CustomMarkerInfoWindowView(it) { number ->
                val number: Uri = Uri.parse("tel:$number")
                val callIntent = Intent(Intent.ACTION_DIAL, number)
                context?.startActivity(callIntent)
            }
        }
        if (markerWindowView != null) {
            mMap?.setInfoWindowAdapter(markerWindowView)
        }
        //setNearBYJobs()
        mMap?.setOnInfoWindowClickListener(this)
//        mMap.setOnCameraIdleListener(this)
//        latitude = mMap.cameraPosition?.target?.latitude.toString()
//        longitude = mMap.cameraPosition?.target?.longitude.toString()
//        setMap()
    }

    override fun onCameraIdle() {
        // mMap?.clear()
//        latitude = mMap.cameraPosition?.target?.latitude.toString()
//        longitude = mMap.cameraPosition?.target?.longitude.toString()
//        setMarker(latitude.toDouble(), longitude.toDouble())
    }

    private fun setMap() {
        Timber.e("latitude: ${latitude.toDouble()}")
        mMap?.clear()
        val latLng = LatLng(currantLatitude.toDouble(), currantLongitude.toDouble())
//            setMarker(latLng.latitude, latLng.longitude, null)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
        mMap?.animateCamera(cameraUpdate)
    }

    private fun resizeMapIcons(): Bitmap {
        return Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources,
                resources.getIdentifier(
                    R.drawable.ic_location.toString(),
                    "drawable",
                    context?.packageName
                )
            ), 50, 50, false
        )
    }

    private fun setCurrantLocation() {
        mMap?.clear()
        /*val latLng = LatLng(currantLatitude.toDouble(), currantLongitude.toDouble())
        setMarker(latLng.latitude, latLng.longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
        mMap.animateCamera(cameraUpdate)*/
        if (currantLatitude.isNotEmpty() && currantLongitude.isNotEmpty()) {
            mMap?.clear()
            val latLng = LatLng(currantLatitude.toDouble(), currantLongitude.toDouble())
//            setMarker(latLng.latitude, latLng.longitude, null)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
            mMap?.animateCamera(cameraUpdate)
            Timber.e("currantLatitude ${currantLatitude.toDouble()} currantLongitude ${currantLongitude.toDouble()}")
            viewModel.fixerLocation(FixerLocation(currantLatitude, currantLongitude))
            setNearBYJobs()
            getLocation()
        } else {
            getLocation()
        }
    }

    private fun setNearBYJobs() {
        if (distance == 0) {
            val km = 5 * 1    // 5 * 1000 = 5000
            //viewModel.availableTask(AvailableTask(km), currentPage = 1)
            getLocation()
            viewModel.nearByJobs(AvailableTask(km))
        } else {
            val km = distance * 1    // 5 * 1000 = 5000
            //viewModel.availableTask(AvailableTask(km), currentPage = 1)
            viewModel.nearByJobs(AvailableTask(km))
        }
    }

    private fun setMarker(lat: Double?, lng: Double?, mainData: NearByJobsResponse?) {
        if (mainData != null) {
            mMap?.let {
                lat?.let { it1 ->
                    lng?.let { it2 ->
                        val latlong = LatLng(it1, it2)

                        if (mainData.job_status_id == 1) {
                            mMap?.addMarker(
                                MarkerOptions()
                                    .position(latlong)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fixing_user))
                            )!!.tag = mainData
                        } else {
                            mMap?.addMarker(
                                MarkerOptions()
                                    .position(latlong)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_user))
                            )!!.tag = mainData
                        }


                        // mMap?.addMarker(MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons())))
                        //    mMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))

//                        if (mainData.job_status_id == 1) {
//                            rippleEffect(
//                                latlong,
//                                mainData,
//                                ContextCompat.getColor(
//                                    MyApp.getInstance().applicationContext,
//                                    R.color.colorGreen
//                                ),
//                                BitmapDescriptorFactory.fromResource(R.drawable.ic_current_user)
//                            )
//                            //  pulseCircleTest(sydney)
//                        } else if ((mainData.job_status_id == 2)) {
//                            rippleEffect(
//                                latlong,
//                                mainData,
//                                ContextCompat.getColor(
//                                    MyApp.getInstance().applicationContext,
//                                    R.color.colorRed
//                                ),
//                                BitmapDescriptorFactory.fromResource(R.drawable.ic_fixing_user)
//                            )
//                        }
                    }
                }
            }
        } else {
            //   val mDotMarkerBitmap1: Bitmap = generateBitmapFromDrawable(R.drawable.circle_drawable)
            mMap?.let { map ->
                lat?.let { it1 ->
                    lng?.let { it2 ->
//                        val sydney = LatLng(it1, it2)
//                        map.addMarker(
//                            MarkerOptions().position(sydney)
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_user))
//                        )
                    }
                }
            }
        }
    }

    private fun onMarkerClick() {
        mMap?.setOnInfoWindowClickListener { marker ->
            //Timber.e(marker.title)
            val data = marker.tag as NearByJobsResponse
            Timber.e("infowindow data", data.toString())
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return true
    }

    override fun onInfoWindowClick(marker: Marker) {
        val data = marker.tag as NearByJobsResponse
        // if (data.job_status_id == 1) {
        requireContext().startActivity<ViewTaskAcceptActivity>(GOOGLETAG_FIXDETAIL to data.id.toInt())
        // }
    }

    private fun rippleEffect(
        location: LatLng,
        mainData: TaskList?,
        color: Int,
        drawable: BitmapDescriptor,
    ) {
        mMap?.addMarker(MarkerOptions().position(location).icon(drawable))?.tag = mainData

        val mapRipple = MapRipple(mMap, location, requireContext())
        mapRipple.stopRippleMapAnimation()
        mapRipple.withNumberOfRipples(1)
        mapRipple.withFillColor(color)
        mapRipple.withStrokeColor(color)
        mapRipple.withStrokewidth(0)
        mapRipple.withDistance(0.0)
        mapRipple.withRippleDuration(8000)
        mapRipple.withDurationBetweenTwoRipples(800)
        mapRipple.withTransparency(0.5f)
        mapRipple.startRippleMapAnimation();

        if (currantLatitude.isNotEmpty() && currantLongitude.isNotEmpty()) {
            mMap?.clear()
            val latLng = LatLng(currantLatitude.toDouble(), currantLongitude.toDouble())
//            setMarker(latLng.latitude, latLng.longitude, null)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
            mMap?.animateCamera(cameraUpdate)
        }
    }

    override fun onResume() {
        super.onResume()
        getLocation()
        if (currantLatitude.isNotEmpty() && currantLongitude.isNotEmpty()) {
            // mMap?.clear()
            val latLng = LatLng(currantLatitude.toDouble(), currantLongitude.toDouble())
//            setMarker(latLng.latitude, latLng.longitude, null)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
            mMap?.animateCamera(cameraUpdate)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivCurrentLocation -> setCurrantLocation()
        }
    }
}




















