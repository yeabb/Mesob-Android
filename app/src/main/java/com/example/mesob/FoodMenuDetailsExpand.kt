package com.example.mesob

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

class FoodMenuDetailsExpand : AppCompatActivity() {

    private lateinit var btToGoogleMap: Button
    private lateinit var tvFoodName: TextView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var tvFoodCreditNumber: TextView
    private lateinit var tvFoodIngredients: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_menu_details_expand)

        firestore = FirebaseFirestore.getInstance()

        btToGoogleMap = findViewById(R.id.btToGoogleMap)
        tvFoodName = findViewById(R.id.tvFoodName)
        tvFoodCreditNumber = findViewById(R.id.tvFoodCreditNumber)
        tvFoodIngredients = findViewById(R.id.tvFoodIngredients)

        val foodMenuId = intent.getStringExtra("foodMenuId").toString()
        val foodName = intent.getStringExtra("foodName")
        val restaurantName = intent.getStringExtra("restaurantName")
        val restaurantAdress = intent.getStringExtra("restaurantAdress")
        val restaurantPhone = intent.getStringExtra("restaurantPhone")
        val restaurantLatitude = intent.getDoubleExtra("restaurantLatitude", 0.0)
        val restaurantLongitude = intent.getDoubleExtra("restaurantLongitude", 0.0)
        val foodCreditNumber = intent.getIntExtra("foodCreditNumber", 0)
        val foodIngredients = intent.getStringExtra("foodIngredients")
        val rating = intent.getIntExtra("rating", 0)

        tvFoodName.text = foodName
        tvFoodCreditNumber.text = foodCreditNumber.toString()
        tvFoodIngredients.text = foodIngredients

        btToGoogleMap.setOnClickListener {
            Log.d("GoogleMap Button Click", "Google map Button clicked!")
            openGoogleMapsDirections(restaurantLatitude, restaurantLongitude)
        }

        // Set up click listeners for star ImageView elements
        val starIds = listOf(R.id.star0, R.id.star1, R.id.star2, R.id.star3, R.id.star4)
        starIds.forEach { starId ->
            findViewById<ImageView>(starId).setOnClickListener { view ->
                onStarClick(view, foodMenuId)
            }
        }
    }

    fun openGoogleMapsDirections(latitude: Double, longitude: Double) {
        val uri = "google.navigation:q=$latitude,$longitude"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Google Maps app is not installed", Toast.LENGTH_SHORT).show()
        }
    }



    fun onStarClick(view: View, foodMenuId: String) {
        val rating = view.tag?.toString()?.toIntOrNull() ?: 0

        firestore.collection("food_menus")
            .document(foodMenuId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val currentRating = documentSnapshot.getLong("rating") ?: 0
                val numberOfReviews = documentSnapshot.getLong("numberOfReviews") ?: 0
                val totalSumRating = documentSnapshot.getLong("totalSumRating") ?: 0

                // Calculate the new average rating
                var newRating = ((totalSumRating + rating) / (numberOfReviews + 1).toFloat()).roundToInt()

                if (newRating > 5) {
                    newRating = 5
                }


                val data = mapOf(
                    "rating" to newRating,
                    "numberOfReviews" to numberOfReviews + 1,
                    "totalSumRating" to totalSumRating + rating
                )

                // Update the Firestore document with the new rating and number of reviews
                firestore.collection("food_menus")
                    .document(foodMenuId)
                    .update(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Rating updated successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update the rating: ${e.message}", Toast.LENGTH_SHORT).show()

                    }

                // Update the star ratings in the UI
                for (i in 0..4) {
                    val starId = resources.getIdentifier("star$i", "id", packageName)
                    val star = findViewById<ImageView>(starId)

                    if (i <= rating) {
                        star.setImageResource(R.drawable.ic_filled_star)
                    } else {
                        star.setImageResource(R.drawable.ic_empty_star)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to retrieve rating data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}
