package com.example.passionmeet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView

class FullMapActivity: AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create a map programmatically and set the initial camera
        mapView = MapView(this)
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(2.2, 46.0))
                .pitch(0.0)
                .zoom(5.0)
                .bearing(0.0)
                .build()
        )
        setContentView(R.layout.activity_group_activity_focussed)
    }
}