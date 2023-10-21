package com.example.mesob

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.roundToInt


class Refer : Fragment() {
    private lateinit var adapter: ReferAdapter
    private lateinit var referRecyclerView: RecyclerView
    private lateinit var rewardArrayList: ArrayList<RewardData>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var tvPercentageAmount: TextView
    private lateinit var tvConfirmedReferralAmount: TextView
    private lateinit var tvPendingReferralAmount: TextView
    private lateinit var percentageProgressBar: ProgressBar
    private var confirmedReferral: Int = 0
    private var pendingReferral: Int = 0

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

        tvPercentageAmount = view.findViewById(R.id.tvPercentageAmount)
        tvConfirmedReferralAmount = view.findViewById(R.id.tvConfirmedReferralAmount)
        tvPendingReferralAmount = view.findViewById(R.id.tvPendingReferralAmount)
        percentageProgressBar = view.findViewById(R.id.percentageProgressBar)

        // Initially hide UI components
        tvConfirmedReferralAmount.visibility = View.INVISIBLE
        tvPendingReferralAmount.visibility = View.INVISIBLE
        percentageProgressBar.visibility = View.INVISIBLE

        dataInitialize()

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        referRecyclerView = view.findViewById(R.id.rvRewards)
        referRecyclerView.layoutManager = layoutManager
        referRecyclerView.setHasFixedSize(true)
        adapter = ReferAdapter(rewardArrayList)
        referRecyclerView.adapter = adapter

        // Get the userId from the main activity as an arg
        val userId = arguments?.getString("userId")

        // Get confirmedReferral and pendingReferral from Firestore "users" collections
        dataReferralNumberInitialize(userId)
    }

    private fun dataReferralNumberInitialize(userId: String?) {
        val userDocRef = userId?.let { firestore.collection("users").document(it) }

        userDocRef?.get()
            ?.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Data exists for the specified user
                    confirmedReferral = documentSnapshot.getLong("confirmedReferral")?.toInt() ?: 0
                    pendingReferral = documentSnapshot.getLong("pendingReferral")?.toInt() ?: 0
                    // Update the UI components and make them visible
                    updateUI()
                } else {
                    // No data found for the specified user
                    // Handle this case accordingly
                }
            }
            ?.addOnFailureListener { e ->
                // Handle any errors that occurred during the fetch
                Toast.makeText(
                    requireContext(),
                    "Error fetching user data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateUI() {
        tvConfirmedReferralAmount.text = confirmedReferral.toString()
        tvPendingReferralAmount.text = pendingReferral.toString()

        val totalRequiredReferral = 30
        val rewardPercentage = calculateRewardPercentage(confirmedReferral, totalRequiredReferral).roundToInt()

        val rewardPercentageStr = rewardPercentage.toString()
        tvPercentageAmount.text = "$rewardPercentageStr%"
        percentageProgressBar.progress = rewardPercentage

        // Make the UI components visible
        tvConfirmedReferralAmount.visibility = View.VISIBLE
        tvPendingReferralAmount.visibility = View.VISIBLE
        percentageProgressBar.visibility = View.VISIBLE
    }

    private fun dataInitialize() {
        rewardArrayList = arrayListOf()

        val collectionReference = firestore.collection("rewards")

        collectionReference
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { documentSnapshot ->
                    val reward = documentSnapshot.toObject(RewardData::class.java)
                    reward?.let { rewardArrayList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching data: ${exception.message}")
            }
    }

    private fun calculateRewardPercentage(confirmedReferral: Int, totalRequiredReferral: Int): Double {
        return (confirmedReferral.toDouble() / totalRequiredReferral) * 100.0
    }
}
