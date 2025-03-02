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
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FullMapActivity: AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var activities: List<ActivityModel>
    private var counter = 0

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

                        if(counter == 0){
                            mapView.mapboxMap.setCamera(
                                CameraOptions.Builder()
                                    .center(Point.fromLngLat(longitude, latitude))
                                    .pitch(0.0)
                                    .zoom(12.0)
                                    .bearing(0.0)
                                    .build()
                            )
                            counter++
                        }
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