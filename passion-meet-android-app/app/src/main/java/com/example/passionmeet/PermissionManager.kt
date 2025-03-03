package com.example.passionmeet

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mapbox.android.core.permissions.PermissionsManager

class PermissionManager(private val activity: Activity) {

        // Function to check if location permission is granted
        fun isLocationPermissionGranted(): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

        // Function to request location permission
        fun requestLocationPermission(requestCode: Int) {
            if (!isLocationPermissionGranted()) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    requestCode
                )
            }
        }

        // Function to handle permission request result
        fun onPermissionResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    onLocationPermissionGranted()
                } else {
                    // Permission denied
                    onLocationPermissionDenied()
                }
            }
        }

        // Callback for permission granted (you can customize this)
        private fun onLocationPermissionGranted() {
            // Handle the case when the location permission is granted
        }

        // Callback for permission denied (you can customize this)
        private fun onLocationPermissionDenied() {
            // Handle the case when the location permission is denied
        }

        companion object {
            const val LOCATION_PERMISSION_REQUEST_CODE = 1
        }
    }