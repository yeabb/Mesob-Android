package com.example.mesob

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class FoodMenu : Fragment() {
    private lateinit var adapter: FoodMenuAdapter
    private lateinit var foodMenuRecyclerView: RecyclerView
    private lateinit var foodMenuArrayList: ArrayList<FoodMenuData>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userLocation: GeoPoint


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_menu, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize user's location (replace with actual coordinates)
        userLocation = GeoPoint(9.020478527484224, 38.759949051401776)

        dataInitialize(userLocation)

        val layoutManager = LinearLayoutManager(context)
        foodMenuRecyclerView = view.findViewById(R.id.rvFoodMenu)
        foodMenuRecyclerView.layoutManager = layoutManager
        foodMenuRecyclerView.setHasFixedSize(true)
        adapter = FoodMenuAdapter(foodMenuArrayList)
        foodMenuRecyclerView.adapter = adapter



        adapter.onItemClickListener = { foodMenu ->
            val intent = Intent(requireContext(), FoodMenuDetailsExpand::class.java)

            // Retrieve the document ID associated with the gas station data
            val foodMenuId = foodMenusWithIds.find { it.second == foodMenu }?.first

            intent.putExtra("foodMenuId", foodMenuId) // Pass the document ID
            intent.putExtra("foodName", foodMenu.foodName)
            intent.putExtra("restaurantName", foodMenu.restaurantName)
            intent.putExtra("restaurantAdress", foodMenu.restaurantAdress)
            intent.putExtra("restaurantPhone", foodMenu.restaurantPhone)
            foodMenu.location?.let { intent.putExtra("restaurantLatitude", it.latitude) }
            foodMenu.location?.let { intent.putExtra("restaurantLongitude", it.longitude) }
            intent.putExtra("foodCreditNumber", foodMenu.foodCreditNumber)
            startActivity(intent)
        }
    }




    private lateinit var foodMenusWithIds: MutableList<Pair<String, FoodMenuData>>

    private fun dataInitialize(userLocation: GeoPoint) {
        foodMenuArrayList = arrayListOf<FoodMenuData>()

        // Reference to the "gas_stations" collection in Firestore
        val collectionReference = firestore.collection("food_menus")

        // Fetch data from Firestore without sorting
        collectionReference
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot? ->
                foodMenusWithIds = mutableListOf()

                querySnapshot?.documents?.forEach { documentSnapshot ->
                    val foodMenu = documentSnapshot.toObject(FoodMenuData::class.java)

                    if (foodMenu != null) {
                        val documentId = documentSnapshot.id
                        foodMenusWithIds.add(Pair(documentId, foodMenu))
                        foodMenuArrayList.add(foodMenu)
                    }
                }

                // Sort the gasArrayList by distance
                foodMenuArrayList.sortBy { it.location?.let { it1 -> calculateHaversineDistance(it1, userLocation) } }
                adapter.notifyDataSetChanged() // Notify the adapter that data has changed
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
                Log.e("Firestore", "Error fetching data: ${exception.message}")
            }
    }

    // Using haversine formula to calculate the distance between user's location and gas station location
    private fun calculateHaversineDistance(
        location1: GeoPoint,
        location2: GeoPoint
    ): Double {
        val radiusOfEarth = 6371.0 // Earth's radius in kilometers

        // Convert latitude and longitude from degrees to radians
        val lat1Rad = Math.toRadians(location1.latitude)
        val lon1Rad = Math.toRadians(location1.longitude)
        val lat2Rad = Math.toRadians(location2.latitude)
        val lon2Rad = Math.toRadians(location2.longitude)

        // Haversine formula
        val dLat = lat2Rad - lat1Rad
        val dLon = lon2Rad - lon1Rad
        val a = sin(dLat / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radiusOfEarth * c
    }
}