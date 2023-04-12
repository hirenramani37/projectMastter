package com.massttr.user.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment

class PermissionUtils {

    private var activity: Activity? = null
    private var fragment: Fragment? = null

    private var onPermissionResponse: OnPermissionResponse? = null


    constructor(activity: Activity) {
        this.activity = activity
        onPermissionResponse = activity as OnPermissionResponse
    }

    constructor(fragment: Fragment) {
        this.activity = fragment.activity
        this.fragment = fragment
        onPermissionResponse = fragment as OnPermissionResponse
    }

    fun requestPermissions(permissions: Array<String>, requestCode: Int) {

        if (checkPermission(permissions)) {
            if (onPermissionResponse != null) {
                onPermissionResponse!!.onPermissionGranted(requestCode)
            }
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (fragment != null)
                    (fragment as Fragment).requestPermissions(permissions, requestCode)
                else
                    (activity as Activity).requestPermissions(permissions, requestCode)
            }
        }

    }

    fun checkPermission(permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            return true
        } else {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(activity!!, permission) != 0) {
                    return false
                }
            }
            return true
        }
    }


    interface OnPermissionResponse {
        fun onPermissionGranted(requestCode: Int)

        fun onPermissionDenied(requestCode: Int)
    }


    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (verifyPermissionResults(grantResults)) {

            if (onPermissionResponse != null) {
                onPermissionResponse!!.onPermissionGranted(requestCode)
            }
        } else {
            if (onPermissionResponse != null) {
                onPermissionResponse!!.onPermissionDenied(requestCode)
            }
        }
    }

    fun verifyPermissionResults(grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) {
            return false
        }
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (verifyPermissionResults(grantResults)) {

            if (onPermissionResponse != null) {
                onPermissionResponse!!.onPermissionGranted(requestCode)
            }
        } else {
            if (onPermissionResponse != null) {
                onPermissionResponse!!.onPermissionDenied(requestCode)
            }
        }
    }
    companion object {

        val REQUEST_CODE_CAMERA_GALLERY_PERMISSION = 104
        val REQUEST_CAMERA_GALLERY_PERMISSION = arrayOf( Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}