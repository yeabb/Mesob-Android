package com.example.mesob

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class Map : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private val db = FirebaseFirestore.getInstance()



    private val customMapStyle = """
        [
          {
            "featureType": "poi",
            "elementType": "labels",
            "stylers": [
              { "visibility": "off" }
            ]
          },
          {
            "featureType": "transit",
            "elementType": "labels.icon",
            "stylers": [
              { "visibility": "off" }
            ]
          },
          {
            "featureType": "road",
            "elementType": "labels.icon",
            "stylers": [
              { "visibility": "off" }
            ]
          }
        ]
    """.trimIndent()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)



        updateMapMarkers()


        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.setMapStyle(MapStyleOptions(customMapStyle))



        // Initially load markers based on the default choice (gas stations)
        updateMapMarkers()
    }

    private fun updateMapMarkers() {


        // Query the Firestore collection based on the user's choice
        val collectionName = "food_menus"
        val fieldName = "restaurantName"


        db.collection(collectionName)
            .get()
            .addOnSuccessListener { documents ->

                googleMap?.clear()

                for (document in documents) {
                    val location = document["location"] as? com.google.firebase.firestore.GeoPoint
                    val name = document[fieldName] as? String ?: "Unknown Name"

                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)

                        // Customize the marker icon based on the choice
                        val restaurantIcon = createRestaurantIcon()

                        googleMap?.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(name)
                                .icon(restaurantIcon)
                        )
                    }

                    Log.d("MapFragment", "Processed document: $name, $location")
                }


                // Add a marker for the user's location
                val userLocation = LatLng(40.758896, -73.985130)
                val userIcon = createUserIcon()



                googleMap?.addMarker(
                    MarkerOptions()
                        .position(userLocation)
                        .title("Your Location")
                        .icon(userIcon)
                )

                // Move the camera to the user's location
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13.0f))


            }
            .addOnFailureListener { exception ->
                // Handle errors here
                Log.e("MapFragment", "Firestore query failed: ${exception.message}")
            }
    }

    // Create restaurant icon
    private fun createRestaurantIcon(): BitmapDescriptor {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_foodmenu2)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 90, 90, false)
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }



    // Create user pin icon
    private fun createUserIcon(): BitmapDescriptor {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_user2)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false)
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
