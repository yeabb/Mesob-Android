package com.example.mesob

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.mesob.Login
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Signup : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var btSignup: Button
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etSignupEmail: EditText
    private lateinit var etSignupPassword: EditText
    private lateinit var tvSignupToLogin : TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)


        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        btSignup = view.findViewById(R.id.btSignup)
        etFirstName = view.findViewById(R.id.etFirstName)
        etLastName = view.findViewById(R.id.etLastName)
        etSignupEmail = view.findViewById(R.id.etSignupEmail)
        etSignupPassword = view.findViewById(R.id.etSignupPassword)
        tvSignupToLogin = view.findViewById(R.id.tvSignupToLogin)

        btSignup.setOnClickListener{
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val email = etSignupEmail.text.toString()
            val password = etSignupPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()){
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            val userId = user?.uid
                            saveUserDetailsToFirestore(userId, firstName, lastName)
                            navigateToLoginFragment()

                        } else {
                            Toast.makeText(requireContext(), "Something went wrong please try again", Toast.LENGTH_SHORT).show()
                        }
                    }
            }else {
                Toast.makeText(requireContext(), "None of the fields can be empty", Toast.LENGTH_SHORT).show()
            }
        }

        tvSignupToLogin.setOnClickListener {
            navigateToLoginFragment()
        }


        return view
    }



    private fun saveUserDetailsToFirestore(userId: String?, firstName: String, lastName: String) {
        userId?.let {
            val userDocRef = firestore.collection("users").document(it)
            val userData = hashMapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "liveReservations" to arrayListOf<String>(), // Initialize with an empty array
                "pastReservations" to arrayListOf<String>(), // Initialize with an empty array
                "pendingReferral" to 0,
                "confirmedReferral" to 0,
            )

            userDocRef.set(userData)
                .addOnSuccessListener {
                    // Data saved successfully
                }
                .addOnFailureListener { e ->
                    // Handle the error
                    Toast.makeText(
                        requireContext(),
                        "Error saving user data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }


    private fun navigateToLoginFragment() {
        // Create an instance of the login fragment
        val loginFragment = Login()

        // Get the FragmentManager
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager

        // Start a new FragmentTransaction
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Replace the current fragment with the login fragment
        transaction.replace(R.id.flFragment, loginFragment)

        // Add the transaction to the back stack (optional)
        // transaction.addToBackStack(null)

        transaction.commit()
    }
}