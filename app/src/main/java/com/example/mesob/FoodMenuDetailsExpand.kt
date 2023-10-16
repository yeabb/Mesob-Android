package com.example.mesob

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.mesob.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

class FoodMenuDetailsExpand : AppCompatActivity() {


    private lateinit var firestore: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_menu_details_expand)


        firestore = FirebaseFirestore.getInstance()


        // Retrieve all the data we need from the intent or other sources
        val foodMenuId = intent.getStringExtra("foodMenuId").toString()
        val userId = intent.getStringExtra("userId").toString()
        val foodName = intent.getStringExtra("foodName")
        val restaurantName = intent.getStringExtra("restaurantName")
        val restaurantAdress = intent.getStringExtra("restaurantAdress")
        val restaurantPhone = intent.getStringExtra("restaurantPhone")
        val restaurantLatitude = intent.getDoubleExtra("restaurantLatitude", 0.0)
        val restaurantLongitude = intent.getDoubleExtra("restaurantLongitude", 0.0)
        val foodCreditNumber = intent.getIntExtra("foodCreditNumber", 0)
        val foodIngredients = intent.getStringExtra("foodIngredients")
        val rating = intent.getIntExtra("rating", 0)

        // Create a Bundle and add all the data to it
        val args = Bundle()
        args.putString("foodMenuId", foodMenuId)
        args.putString("userId", userId)
        args.putString("foodName", foodName)
        args.putString("restaurantName", restaurantName)
        args.putString("restaurantAdress", restaurantAdress)
        args.putString("restaurantPhone", restaurantPhone)
        args.putDouble("restaurantLatitude", restaurantLatitude)
        args.putDouble("restaurantLongitude", restaurantLongitude)
        args.putInt("foodCreditNumber", foodCreditNumber)
        args.putString("foodIngredients", foodIngredients)
        args.putInt("rating", rating)


        // Create the the 2 fragments and set arguments for both
        val preReservationFragment = FoodMenuDetailsExpandPreReservation()
        preReservationFragment.arguments = args
        val postReservationFragment = FoodMenuDetailsExpandPostReservation()
        postReservationFragment.arguments = args



        val userDocRef = userId?.let { firestore.collection("users").document(it) }


        userDocRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Data exists for the specified user
                    val liveReservations = documentSnapshot.get("liveReservations") as List<String>

                    // Check if foodMenuId is in liveReservations here
                    if (foodMenuId in liveReservations) {
                        Log.d("MyApp", "You already have reserved this menu")
                        replaceFragment(postReservationFragment)
                    } else {
                        replaceFragment(preReservationFragment)
                    }

                } else {
                    // No data found for the specified user
                    // Handle this case accordingly
                }
            }
            ?.addOnFailureListener { e ->
                // Handle any errors that occurred during the fetch
                Toast.makeText(
                    this,
                    "Error fetching user data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }



    }


    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flFragment, fragment)
        fragmentTransaction.commit()
    }
}


