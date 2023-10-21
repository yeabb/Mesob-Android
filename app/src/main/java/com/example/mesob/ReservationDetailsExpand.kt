package com.example.mesob

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt

class ReservationDetailsExpand : AppCompatActivity() {

    private lateinit var btToGoogleMap: Button
    private lateinit var tvFoodName: TextView
    private lateinit var tvPleaseRateInstruction : TextView
    private lateinit var tvFoodPickUpDateAndTime: TextView
    private lateinit var tvFoodCreditNumber: TextView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_details_expand)



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
        val rating = intent.getIntExtra("rating", 0)
        val foodPickUpDateTime = intent.getStringExtra("foodPickUpDateTime")


        btToGoogleMap = findViewById(R.id.btToGoogleMap)
        tvFoodName = findViewById(R.id.tvFoodName)
        tvFoodCreditNumber = findViewById(R.id.tvFoodCreditNumber)
        tvFoodPickUpDateAndTime = findViewById(R.id.tvFoodPickUpDateTime)
        tvPleaseRateInstruction = findViewById(R.id.tvPleaseRateInstruction)

        val star0: ImageView = findViewById(R.id.star0)
        val star1: ImageView = findViewById(R.id.star1)
        val star2: ImageView = findViewById(R.id.star2)
        val star3: ImageView = findViewById(R.id.star3)
        val star4: ImageView = findViewById(R.id.star4)

        val starImageViews = listOf(star0, star1, star2, star3, star4)


        tvFoodName.text = foodName
        tvFoodCreditNumber.text = foodCreditNumber.toString()
        tvFoodPickUpDateAndTime.text = foodPickUpDateTime

        btToGoogleMap.setOnClickListener {
            openGoogleMapsDirections(restaurantLatitude, restaurantLongitude)
        }




        val x = true

        if (!x){   //check if user already rated the menu
            // Set up click listeners for star ImageView elements
            val starIds = listOf(R.id.star0, R.id.star1, R.id.star2, R.id.star3, R.id.star4)
            starIds.forEach { starId ->
                findViewById<View>(starId).setOnClickListener { view ->
                    onStarClick(view, foodMenuId)
                }
            }

        }else{    //Check if user has not rated the menu in the past

            for (i in 0..4) {


                if (i < rating) {
                    starImageViews[i].setImageResource(R.drawable.ic_filled_star)
                } else {
                    starImageViews[i].setImageResource(R.drawable.ic_empty_star)
                }
                tvPleaseRateInstruction.text = "You have already rated this menu"
                starImageViews[i].setOnClickListener(null)     //Make the star images not clickable
            }
        }




        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

    }


    fun openGoogleMapsDirections(latitude: Double, longitude: Double) {
        val uri = "google.navigation:q=$latitude,$longitude"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.google.android.apps.maps") // Specify the package to ensure it opens in Google Maps

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Google Maps app is not installed, handle this case
            // We can open the web version of Google Maps or prompt the user to install the app.
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
                    val starId = this.resources.getIdentifier("star$i", "id", this.packageName)
                    val star = this.findViewById<ImageView>(starId)

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



    fun showCustomMessageDialog(message: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_message_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage: TextView = dialog.findViewById(R.id.tvMessageDialog)

        tvMessage.text = message
        dialog.show()
    }

}