package com.example.mesob

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class FoodMenuDetailsExpand : AppCompatActivity() {

    private lateinit var btToGoogleMap: Button
    private lateinit var tvFoodName: TextView
    private lateinit var btReserveFood: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var tvFoodCreditNumber : TextView
    private lateinit var tvFoodIngredients : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_menu_details_expand)


        firestore = FirebaseFirestore.getInstance()

        btToGoogleMap = findViewById(R.id.btToGoogleMap)
        tvFoodName = findViewById(R.id.tvFoodName)
        btReserveFood = findViewById(R.id.btReserveFood)
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
        val foodIngredients =  intent.getStringExtra("foodIngredients")
        val rating = intent.getIntExtra("rating", 0)

        tvFoodName.text = foodName
        tvFoodCreditNumber.text = foodCreditNumber.toString()
        tvFoodIngredients.text = foodIngredients




        btToGoogleMap.setOnClickListener {
            Log.d("GooglenMap Button Click", "Google map Button clicked!")
            openGoogleMapsDirections(restaurantLatitude, restaurantLongitude)
        }

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
}