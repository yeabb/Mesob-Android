package com.example.mesob

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

class FoodMenuDetailsExpandPreReservation : Fragment() {

    private lateinit var btToGoogleMap: Button
    private lateinit var btReserveFood: Button
    private lateinit var tvFoodName: TextView
    private lateinit var tvFoodCreditNumber: TextView
    private lateinit var tvFoodIngredients: TextView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_food_menu_details_expand_pre_reservation,
            container,
            false
        )

        btToGoogleMap = view.findViewById(R.id.btToGoogleMap)
        btReserveFood = view.findViewById(R.id.btReserveFood)
        tvFoodName = view.findViewById(R.id.tvFoodName)
        tvFoodCreditNumber = view.findViewById(R.id.tvFoodCreditNumber)
        tvFoodIngredients = view.findViewById(R.id.tvFoodIngredients)

        // Get data from arguments and set the text of UI elements
        val foodMenuId = arguments?.getString("foodMenuId")
        val foodName = arguments?.getString("foodName")
        val foodCreditNumber = arguments?.getInt("foodCreditNumber", 0)
        val foodIngredients = arguments?.getString("foodIngredients")

        tvFoodName.text = foodName
        tvFoodCreditNumber.text = foodCreditNumber.toString()
        tvFoodIngredients.text = foodIngredients

        btToGoogleMap.setOnClickListener {
            val restaurantLatitude = arguments?.getDouble("restaurantLatitude", 0.0) ?: 0.0
            val restaurantLongitude = arguments?.getDouble("restaurantLongitude", 0.0) ?: 0.0
            openGoogleMapsDirections(restaurantLatitude, restaurantLongitude)
        }

        btReserveFood.setOnClickListener {
            showBottomSheetDialog()
        }

        // Set up click listeners for star ImageView elements
        val starIds = listOf(R.id.star0, R.id.star1, R.id.star2, R.id.star3, R.id.star4)
        starIds.forEach { starId ->
            view.findViewById<View>(starId).setOnClickListener { view ->
                onStarClick(view, arguments?.getString("foodMenuId") ?: "")
            }
        }

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

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
                        Toast.makeText(requireContext(), "Rating updated successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to update the rating: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                // Update the star ratings in the UI
                for (i in 0..4) {
                    val starId = requireView().resources.getIdentifier("star$i", "id", requireContext().packageName)
                    val star = requireView().findViewById<ImageView>(starId)

                    if (i <= rating) {
                        star.setImageResource(R.drawable.ic_filled_star)
                    } else {
                        star.setImageResource(R.drawable.ic_empty_star)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to retrieve rating data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateTimeSlots(): List<TimeWindow> {
        val timeWindows = mutableListOf<TimeWindow>()
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 11)
        calendar.set(Calendar.MINUTE, 30)

        val endCalendar = Calendar.getInstance()
        endCalendar.set(Calendar.HOUR_OF_DAY, 14)
        endCalendar.set(Calendar.MINUTE, 30)

        while (calendar.before(endCalendar)) {
            val startTime = format.format(calendar.time)
            calendar.add(Calendar.MINUTE, 15)
            val endTime = format.format(calendar.time)

            timeWindows.add(TimeWindow(startTime, endTime))
        }

        return timeWindows
    }

    fun showBottomSheetDialog() {
        val view = layoutInflater.inflate(R.layout.select_timewindow_bottom_sheet, null)

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        dialog.show()

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvSelectTimeWindow)
        val btSelectTimeWindow = view.findViewById<Button>(R.id.btSelectTimeWindow)

        val timewindows = generateTimeSlots()
        val adapter = TimeSlotAdapter(timewindows)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btSelectTimeWindow.setOnClickListener {
            val selectedTimeWindow = adapter.getSelectedTimeWindow()
            if (selectedTimeWindow != null) {
                val timeWindowToDisplay = "${selectedTimeWindow.startTime} - ${selectedTimeWindow.endTime}"

                Toast.makeText(requireContext(), "Selected Time Window: $timeWindowToDisplay", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please select a time window.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
