package com.example.mesob

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore



class FoodMenuDetailsExpandPostReservation : Fragment() {

    private lateinit var btToGoogleMap: Button
    private lateinit var btPickUpFood: Button
    private lateinit var tvFoodName: TextView
    private lateinit var tvFoodCreditNumber: TextView
    private lateinit var tvFoodIngredients: TextView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_food_menu_details_expand_post_reservation,
            container,
            false
        )

        btToGoogleMap = view.findViewById(R.id.btToGoogleMap)
        btPickUpFood = view.findViewById(R.id.btPickUpFood)
        tvFoodName = view.findViewById(R.id.tvFoodName)
        tvFoodIngredients = view.findViewById(R.id.tvFoodIngredients)


        // Get data from arguments and set the text of UI elements
        val userId = arguments?.getString("userId").toString()
        val foodMenuId = arguments?.getString("foodMenuId").toString()
        val foodName = arguments?.getString("foodName")
        val foodCreditNumber = arguments?.getInt("foodCreditNumber", 0)
        val foodIngredients = arguments?.getString("foodIngredients")

        tvFoodName.text = foodName
        tvFoodIngredients.text = foodIngredients

        btToGoogleMap.setOnClickListener {
            val restaurantLatitude = arguments?.getDouble("restaurantLatitude", 0.0) ?: 0.0
            val restaurantLongitude = arguments?.getDouble("restaurantLongitude", 0.0) ?: 0.0
            openGoogleMapsDirections(restaurantLatitude, restaurantLongitude)
        }


        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()


        btPickUpFood.setOnClickListener {
            replaceFragment(QrCodeScanner())
        }

        return view
    }





    fun openGoogleMapsDirections(latitude: Double, longitude: Double) {
        val uri = "google.navigation:q=$latitude,$longitude"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Google Maps app is not installed", Toast.LENGTH_SHORT).show()
        }
    }





    private fun replaceFragment(fragment: Fragment) {
        // Assuming that MainActivity has a function to replace fragments
        if (activity is FoodMenuDetailsExpand) {
            (activity as FoodMenuDetailsExpand).replaceFragment(fragment)
        }
    }
}
