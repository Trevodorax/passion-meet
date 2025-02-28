package com.example.passionmeet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.passionmeet.models.ActivityModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView

class FullMapActivity: AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var activities: List<ActivityModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get the group id and group name from the intent
        val groupId = intent.getStringExtra("group_id")

        // Create a map and set the initial camera
        mapView = MapView(this)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(2.2, 46.0))
                .pitch(0.0)
                .zoom(12.0)
                .bearing(0.0)
                .build()
        )
        setContentView(mapView)
    }
}