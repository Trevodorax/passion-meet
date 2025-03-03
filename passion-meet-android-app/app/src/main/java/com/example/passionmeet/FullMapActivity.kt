package com.example.passionmeet

import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.passionmeet.models.ActivityModel
import com.mapbox.api.geocoding.v6.MapboxV6Geocoding
import com.mapbox.api.geocoding.v6.V6ForwardGeocodingRequestOptions
import com.mapbox.api.geocoding.v6.models.V6Response
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FullMapActivity: AppCompatActivity() {

    private lateinit var mapView: MapView
    private var counter = 0
    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get the group id and group name from the intent
        val groupId = intent.getStringExtra("group_id")
        val activities = intent.getSerializableExtra("activities") as List<ActivityModel>

        Log.d("FullMapActivity", "Group ID: $groupId")
        Log.d("FullMapActivity", "Activities: $activities")

        // Create a map and set the initial camera
        mapView = MapView(this)
        setContentView(mapView)

        // Initialize PermissionManager
        permissionManager = PermissionManager(this)

        // Check if location permission is granted
        if (permissionManager.isLocationPermissionGranted()) {
            // Permission is already granted, proceed with location setup
            Log.d("FullMapActivity", "Location permission granted")

            val locationComponent = mapView.location
            // Enable location component to show the user's location
            locationComponent.locationPuck = createDefault2DPuck(withBearing = true)
            locationComponent.enabled = true
            locationComponent.puckBearing = PuckBearing.COURSE
            locationComponent.puckBearingEnabled = true


            // Set up listener to update map when the user's location changes
            locationComponent.addOnIndicatorPositionChangedListener { position ->
                // Update map camera to center on the user's location
                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(position.longitude(), position.latitude()))  // Center map on user's location
                        .zoom(12.0)  // Set zoom level
                        .bearing(0.0)  // Set bearing
                        .pitch(0.0)  // Set pitch
                        .build()
                )
                // Set gestures focal point to the user's current location
                mapView.gestures.focalPoint = mapView.mapboxMap.pixelForCoordinate(position)
            }

        }

             // Add the activities to the map
            activities.forEach { activity ->
                Log.d("FullMapActivity", "Activity: $activity")
                getGeolocationFromPostalLocation(activity.location)

            }
        }


    private fun getGeolocationFromPostalLocation(activityLocation: String) {

        val requestOptions = V6ForwardGeocodingRequestOptions.builder(activityLocation)
            .autocomplete(false)
            .build()

        val mapboxGeocoding = MapboxV6Geocoding.builder(
            getString(R.string.mapbox_access_token),
            requestOptions
        ).build()

        mapboxGeocoding.enqueueCall(object : Callback<V6Response> {
            override fun onResponse(call: Call<V6Response>, response: Response<V6Response>) {
                val results = response.body()!!.features()
                if (results.size > 0) {

                    // Log the geometry of the first result.
                    val firstResultPoint = results[0].geometry()

                    if (firstResultPoint is Point) {
                        val coordinates = firstResultPoint.coordinates()
                        Log.d("Geocoding", "First result geometry: $coordinates")
                        val longitude = coordinates[0]
                        val latitude = coordinates[1]

                        // Call method to set up the map with these coordinates
                        val activityPoint = Point.fromLngLat(longitude, latitude)
                        // Add a marker for the activity
                        addMarker(activityPoint)
                    }
                } else {
                    Log.e("Geocoding", "No results found or geocoding failed")
                }
            }
            override fun onFailure(call: Call<V6Response>, t: Throwable) {
                Log.e("Geocoding", "Geocoding failed: ${t.message}")
            }
        })
    }

    private fun addMarker(activityPoint: Point) {

        //PINPOINT THE PLACE
        val drawable = ContextCompat.getDrawable(
            this,
            R.drawable.red_marker
        )  // Replace with your drawable resource
        val bitmap = (drawable as BitmapDrawable).bitmap

        val annotationApi = this.mapView.annotations
        val pointAnnotationManager =
            annotationApi.createPointAnnotationManager()

        val pointAnnotationOptions: PointAnnotationOptions =
            PointAnnotationOptions()
                .withPoint(activityPoint)
                .withIconImage(bitmap)

        pointAnnotationManager.create(pointAnnotationOptions)

    }

}