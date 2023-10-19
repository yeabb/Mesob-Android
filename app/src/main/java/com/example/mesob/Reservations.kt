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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Reservations : Fragment() {
    private lateinit var adapter: ReservationsAdapter
    private lateinit var reservationRecyclerView: RecyclerView
    private lateinit var foodMenuArrayList: ArrayList<FoodMenuData>
    private lateinit var foodPickUpDateTimesArr: ArrayList<Date>
    private lateinit var foodMenusWithIds: MutableList<Pair<String, FoodMenuData>>
    private lateinit var foodPickUpDateTimesWithIds: MutableList<Pair<Date, String>>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Get the userId from the main activity as an argument
        val userId = arguments?.getString("userId")

        dataInitialize()

        val layoutManager = LinearLayoutManager(context)
        reservationRecyclerView = view.findViewById(R.id.rvReservations)
        reservationRecyclerView.layoutManager = layoutManager
        reservationRecyclerView.setHasFixedSize(true)
        adapter = ReservationsAdapter(foodMenuArrayList, foodPickUpDateTimesArr)
        reservationRecyclerView.adapter = adapter



        adapter.onItemClickListener = { foodMenu, foodPickUpDateTime ->
            val intent = Intent(requireContext(), ReservationDetailsExpand::class.java)

            // Retrieve the document ID associated with the food menu data
            val foodMenuId = foodMenusWithIds.find { it.second == foodMenu }?.first


            // Pass data to the next activity
            intent.putExtra("foodMenuId", foodMenuId) // Pass the document ID
            intent.putExtra("userId", userId)
            intent.putExtra("foodName", foodMenu.foodName)
            intent.putExtra("restaurantName", foodMenu.restaurantName)
            intent.putExtra("restaurantAdress", foodMenu.restaurantAdress)
            intent.putExtra("restaurantPhone", foodMenu.restaurantPhone)
            foodMenu.location?.let { intent.putExtra("restaurantLatitude", it.latitude) }
            foodMenu.location?.let { intent.putExtra("restaurantLongitude", it.longitude) }
            intent.putExtra("foodCreditNumber", foodMenu.foodCreditNumber)
            intent.putExtra("rating", foodMenu.rating)
            intent.putExtra("numberOfReviews", foodMenu.numberOfReviews)
            intent.putExtra("foodPickUpDateTime", foodPickUpDateTime)




            startActivity(intent)


        }
    }


    private fun dataInitialize() {
        foodMenuArrayList = arrayListOf()
        foodPickUpDateTimesArr = arrayListOf()
        foodMenusWithIds = mutableListOf()
        foodPickUpDateTimesWithIds = mutableListOf()

        // Reference to the "users" collection in Firestore
        val usersCollection = firestore.collection("users")

        // Get the userId from the main activity as an argument
        val userId = arguments?.getString("userId")

        // Fetch data for the specific user
        if (userId != null) {
            usersCollection
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val pastReservations = documentSnapshot.get("pastReservations") as? MutableList<HashMap<String, Date>>

                        // For each foodMenuId in pastReservations, access the "food_menus" collection
                        if (pastReservations != null) {
                            for (item in pastReservations) {
                                val foodMenuId = item["foodMenuId"] as String
                                val foodPickUpDateTimeStr = item["foodPickUpDateTime"] as String

                                // Parse the date string to a Date object
                                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                val foodPickUpDateTime = sdf.parse(foodPickUpDateTimeStr)

                                val foodMenuReference = firestore.collection("food_menus").document(foodMenuId)


                                // Fetch data for each foodMenuId
                                foodMenuReference.get().addOnSuccessListener { foodMenuDocumentSnapshot ->
                                    val foodMenu = foodMenuDocumentSnapshot.toObject(FoodMenuData::class.java)

                                    if (foodMenu != null) {
                                        val documentId = foodMenuDocumentSnapshot.id
                                        foodMenusWithIds.add(Pair(documentId, foodMenu))
                                        foodPickUpDateTimesWithIds.add(Pair(foodPickUpDateTime, documentId))
                                        foodMenuArrayList.add(foodMenu)
                                        foodPickUpDateTimesArr.add(foodPickUpDateTime)
                                        adapter.notifyDataSetChanged()


                                    }



                                }.addOnFailureListener { exception ->
                                    // Handle any errors here
                                    Log.e("Firestore", "Error fetching data: ${exception.message}")
                                }

                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors here
                    Log.e("Firestore", "Error fetching user data: ${exception.message}")
                }
        }
    }
    
}

