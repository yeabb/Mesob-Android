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


class Refer : Fragment() {
    private lateinit var adapter: ReferAdapter
    private lateinit var referRecyclerView: RecyclerView
    private lateinit var rewardArrayList: ArrayList<RewardData>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_refer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()



        dataInitialize()

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        referRecyclerView = view.findViewById(R.id.rvRewards)
        referRecyclerView.layoutManager = layoutManager
        referRecyclerView.setHasFixedSize(true)
        adapter = ReferAdapter(rewardArrayList)
        referRecyclerView.adapter = adapter



        //Get the userId from the main activity as an arg
        val userId = arguments?.getString("userId")





//        adapter.onItemClickListener = { reward ->
//            val intent = Intent(requireContext(), RewardDetailsExpand::class.java)
//
//            // Retrieve the document ID associated with the gas station data
//            val foodMenuId = foodMenusWithIds.find { it.second == foodMenu }?.first
//
//            intent.putExtra("foodMenuId", foodMenuId) // Pass the document ID
//            intent.putExtra("userId", userId)
//            intent.putExtra("foodName", foodMenu.foodName)
//            intent.putExtra("restaurantName", foodMenu.restaurantName)
//            intent.putExtra("restaurantAdress", foodMenu.restaurantAdress)
//            intent.putExtra("restaurantPhone", foodMenu.restaurantPhone)
//            foodMenu.location?.let { intent.putExtra("restaurantLatitude", it.latitude) }
//            foodMenu.location?.let { intent.putExtra("restaurantLongitude", it.longitude) }
//            intent.putExtra("foodCreditNumber", foodMenu.foodCreditNumber)
//            intent.putExtra("foodIngredients", foodMenu.foodIngredients)
//            intent.putExtra("rating", foodMenu.rating)
//            intent.putExtra("numberOfReviews", foodMenu.numberOfReviews)
//            startActivity(intent)
//        }
    }



    private lateinit var rewardsWithIds: MutableList<Pair<String, RewardData>>

    private fun dataInitialize() {
        rewardArrayList = arrayListOf<RewardData>()

        // Reference to the "gas_stations" collection in Firestore
        val collectionReference = firestore.collection("rewards")

        // Fetch data from Firestore without sorting
        collectionReference
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot? ->
                rewardsWithIds = mutableListOf()

                querySnapshot?.documents?.forEach { documentSnapshot ->
                    val reward = documentSnapshot.toObject(RewardData::class.java)

                    if (reward != null) {
                        val documentId = documentSnapshot.id
                        rewardsWithIds.add(Pair(documentId, reward))
                        rewardArrayList.add(reward)
                    }
                }

                adapter.notifyDataSetChanged() // Notify the adapter that data has changed
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
                Log.e("Firestore", "Error fetching data: ${exception.message}")
            }
    }

}