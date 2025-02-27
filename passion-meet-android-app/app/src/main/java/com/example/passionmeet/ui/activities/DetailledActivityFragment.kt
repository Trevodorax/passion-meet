package com.example.passionmeet.ui.activities

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.passionmeet.R
import com.mapbox.maps.MapView
import androidx.core.content.ContextCompat
import com.mapbox.api.geocoding.v6.MapboxV6Geocoding
import com.mapbox.api.geocoding.v6.V6ForwardGeocodingRequestOptions
import com.mapbox.api.geocoding.v6.models.V6Response
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

/**
 * A fragment representing the activity focused on.
 */
class DetailledActivityFragment : AppCompatActivity() {

    private lateinit var activitySentenceTV: TextView;
    private lateinit var activityDescriptionTV: TextView;
    private lateinit var activityParticipantsTV: TextView;
    private lateinit var activityMaxParticipantsTV: TextView;
    private lateinit var activityMap: MapView;
    private lateinit var activitySignUpBtn: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_activity_focussed)

        val bundle = intent.extras
        if (bundle != null) {
            val activitySentence = bundle.getString("activity_sentence")
            val activityId = bundle.getString("activity_id")
            val activityDescription = bundle.getString("activity_description")
            val activityLocation = bundle.getString("activity_location")
            val maxParticipants = bundle.getString("max_participants")
            val participants = bundle.getStringArrayList("participants")

            activitySentenceTV = findViewById(R.id.texts_activity)
            activityDescriptionTV = findViewById(R.id.activity_description_content_tv)
            activityParticipantsTV = findViewById(R.id.activity_list_participants)
            activityMaxParticipantsTV= findViewById(R.id.activity_participants_max_tv)
            activitySentenceTV.text = activitySentence
            activityDescriptionTV.text = activityDescription
            activityParticipantsTV.text = participants?.joinToString(", ")
            activityMaxParticipantsTV.text = maxParticipants
            activityMap = findViewById(R.id.map_for_activity)
            updateMapSettings(activityLocation)
        }

    }

    private fun updateMapSettings(activityLocation: String?) {

        if (activityLocation != null) {
            getGeolocationFromPostalLocation(activityLocation)
        }
    }

    private fun getGeolocationFromPostalLocation(activityLocation: String) {

        val requestOptions = V6ForwardGeocodingRequestOptions.builder(activityLocation)
            .autocomplete(false)
            .build();

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
                        setMapPinpoint(latitude, longitude)
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

    private fun setMapPinpoint(latiude: Double, longitude: Double) {
        //CENTER THE CARD AROUND THE PLACE
        this.activityMap.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(longitude, latiude))
                .pitch(0.0)
                .zoom(15.0)
                .bearing(0.0)
                .build()
        )

        //PINPOINT THE PLACE
        val drawable = ContextCompat.getDrawable(
            this,
            R.drawable.red_marker
        )  // Replace with your drawable resource
        val bitmap = (drawable as BitmapDrawable).bitmap

        val annotationApi = this.activityMap.annotations
        val pointAnnotationManager =
            annotationApi.createPointAnnotationManager()

        val pointAnnotationOptions: PointAnnotationOptions =
            PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latiude))
                .withIconImage(bitmap)

        pointAnnotationManager.create(pointAnnotationOptions)
    }
}