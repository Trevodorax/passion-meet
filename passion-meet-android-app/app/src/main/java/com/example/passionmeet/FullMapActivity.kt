package com.example.passionmeet

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mapbox.api.geocoding.v6.MapboxV6Geocoding
import com.mapbox.api.geocoding.v6.V6ForwardGeocodingRequestOptions
import com.mapbox.api.geocoding.v6.models.V6Response
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FullMapActivity: AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create a map programmatically and set the initial camera

        var requestOptions = V6ForwardGeocodingRequestOptions.builder("242 rue du Faubourg Saint-Antoine, 75012 Paris")
            .autocomplete(false)
            .build();

        var mapboxGeocoding = MapboxV6Geocoding.builder(
            getString(R.string.mapbox_access_token),
            requestOptions
        ).build()

        createMapWithPoints(this, mapboxGeocoding)

    }

    fun createMapWithPoints(context: Context, mapbox: MapboxV6Geocoding){

        mapbox.enqueueCall(object : Callback<V6Response> {
            override fun onResponse(call: Call<V6Response>, response: Response<V6Response>) {

                val results = response.body()!!.features()
                if (results.size > 0) {
                    // Log the geometry of the first result.
                    val firstResultPoint = results[0].geometry()
                    if (firstResultPoint is Point) {
                        val coordinates = firstResultPoint.coordinates()

                        //CREATE MARKER
                        mapView = MapView(context)
                        mapView.mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(2.2, 46.0))
                                .pitch(0.0)
                                .zoom(5.0)
                                .bearing(0.0)
                                .build()
                        )
                        setContentView(mapView)

                        val drawable = ContextCompat.getDrawable(
                            context,
                            R.drawable.red_marker
                        )  // Replace with your drawable resource
                        val bitmap = (drawable as BitmapDrawable).bitmap

                        val annotationApi = mapView.annotations
                        val pointAnnotationManager =
                            annotationApi.createPointAnnotationManager()
                        // Set options for the resulting symbol layer.
                        val pointAnnotationOptions: PointAnnotationOptions =
                            PointAnnotationOptions()
                                .withPoint(Point.fromLngLat(coordinates[0], coordinates[1]))
                                .withIconImage(bitmap)

                        pointAnnotationManager?.create(pointAnnotationOptions)
                    }
                } else {
                    // No result for your request were found.
                    Log.d("Geocoder", "onResponse: No result found")
                }
            }

            override fun onFailure(call: Call<V6Response>, throwable: Throwable) {
                throwable.printStackTrace()
            }
        })
    }
}