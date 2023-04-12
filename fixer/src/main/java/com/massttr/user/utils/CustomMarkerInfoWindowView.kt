package com.massttr.user.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.common.data.network.model.NearByJobsResponse
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.massttr.provider.R
import com.massttr.provider.databinding.ListItemMapBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@DelicateCoroutinesApi
@ObsoleteCoroutinesApi
class CustomMarkerInfoWindowView(val context: Context, private val onCall: (String) -> Unit) :
    GoogleMap.InfoWindowAdapter {

    private val binding: ListItemMapBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.list_item_map, null, false)

    override fun getInfoWindow(marker: Marker): View {
        try {
            if (marker.tag != null) {
                val data = marker.tag as NearByJobsResponse
                binding.run {
                    ivCall.setOnClickListener {
                        onCall.invoke("123465678")
                    }

                    tvPrice.text = data.price.plus(" QD")
                    tvDistance.text = String.format("%.2f", data.distance).plus(context.getString(R.string.km_))
                    Glide.with(context)
                        .load(data.banner_image)
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.civWorkimage)
                    tvTitle.text = data.title

                    tvJobDateTime.text = changeDateFormat(
                        data.appointment_date,
                        "dd MMMM yyyy",
                        "EEE, d MMM"
                    ).plus(
                        " " + changeDateFormat(
                            data.appointment_time,
                            "hh:mm a",
                            "hh:mm"
                        )
                    )
                }
                return binding.root
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }
}
