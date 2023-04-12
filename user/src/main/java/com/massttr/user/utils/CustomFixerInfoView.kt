package com.massttr.user.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.common.data.network.model.HomeMapGetFixerResponse
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.makeramen.roundedimageview.RoundedImageView
import com.massttr.user.R
import timber.log.Timber

class CustomFixerInfoView(val context: Context, private val onCall: (String) -> Unit) :
    GoogleMap.InfoWindowAdapter {
    private val markerItemView: View =
        LayoutInflater.from(context).inflate(R.layout.list_item_fixer_map, null, false)

    override fun getInfoWindow(marker: Marker): View? {
        if (marker.tag != null) {
            val data = marker.tag as HomeMapGetFixerResponse
            markerItemView.run {
                val tvDistance: TextView = findViewById(R.id.tvDistance)
                val tvFixerName: TextView = findViewById(R.id.tvFixerName)
                val tvCompletedJobs: TextView = findViewById(R.id.tvCompletedJobs)
                val tvFixerRating: TextView = findViewById(R.id.tvFixerRating)
                val ivFixerProfile: RoundedImageView = findViewById(R.id.ivFixerProfile)

                val callButton: ImageView = findViewById(R.id.imgCall)

                callButton.setOnClickListener {
                    onCall.invoke("123465678")
                }

                tvDistance.text = String.format("%.2f", data.distance).plus(" km")
                // ivFixerProfile.load(data.profile_picture)
                context.loadImages(data.profile_picture, ivFixerProfile, R.drawable.ic_placeholder)
                tvFixerName.text = data.full_name
                tvCompletedJobs.text = data.total_completed_jobs.toString()
                tvFixerRating.text = data.avg_rating.toString()
            }
            return markerItemView
        }
        return null
    }

    override fun getInfoContents(p0: Marker): View? {
        return null
    }
}