package com.example.mesob

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class PostQRCodeScanner : Fragment() {

    private lateinit var btConfirmPickUp : Button
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_post_q_r_code_scanner, container, false)


        firestore = FirebaseFirestore.getInstance()

        val userId = arguments?.getString("userId").toString()
        val foodMenuId = arguments?.getString("foodMenuId").toString()

        btConfirmPickUp = view.findViewById(R.id.btConfirmPickUp)

        btConfirmPickUp.setOnClickListener {

            val currentDateAndTime = getCurrentDateTime()

            // Create a reference to the user's document
            val userDocRef = firestore.collection("users").document(userId)

            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val liveReservations = documentSnapshot.get("liveReservations") as? MutableList<String>
                        val pastReservations = documentSnapshot.get("pastReservations") as? MutableList<HashMap<String, Any>>

                        if (liveReservations != null && pastReservations != null) {
                            if (liveReservations.contains(foodMenuId)) {
                                // Remove the foodMenuId from liveReservations
                                liveReservations.remove(foodMenuId)

                                // Create a map representing the reservation and add it to pastReservations
                                val reservation = Pair(foodMenuId, currentDateAndTime)

                                pastReservations.add(hashMapOf(
                                    "foodMenuId" to reservation.first,
                                    "foodPickUpDateTime" to reservation.second
                                ))


                                // Update the user's document with the modified liveReservations and pastReservations
                                userDocRef.update(
                                    mapOf(
                                        "liveReservations" to liveReservations,
                                        "pastReservations" to pastReservations
                                    )
                                )
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "Removed $foodMenuId from liveReservations and added to pastReservations")
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("Firestore", "Error updating document: ${exception.message}")
                                    }
                            } else {
                                Log.d("Firestore", "$foodMenuId not found in liveReservations")
                            }
                        } else {
                            Log.d("Firestore", "liveReservations or pastReservations is null")
                        }
                    } else {
                        Log.d("Firestore", "User document not found for userId: $userId")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching user data: ${e.message}")
                }
        }


        return view

    }

    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


}