package com.example.mesob

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.Map


class Login : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var btLogin: Button
    private lateinit var etLoginEmail: EditText
    private lateinit var etLoginPassword: EditText
    private lateinit var tvLoginToSignUp: TextView
    private lateinit var tvNavUserName: TextView
    private lateinit var tvNavEmail : TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("LoginFragment", "Login fragment created")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        btLogin = view.findViewById(R.id.btLogin)
        etLoginEmail = view.findViewById(R.id.etLoginEmail)
        etLoginPassword = view.findViewById(R.id.etLoginPassword)
        tvLoginToSignUp = view.findViewById(R.id.tvLoginToSignUp)



        val navigationView = requireActivity().findViewById<NavigationView>(R.id.navigationDrawer)
        val headerView = navigationView.getHeaderView(0)
        tvNavUserName = headerView.findViewById<TextView>(R.id.tvNavUserName)
        tvNavEmail = headerView.findViewById<TextView>(R.id.tvNavEmail)





        btLogin.setOnClickListener{
            val email = etLoginEmail.text.toString()
            val password = etLoginPassword.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){

                            val currentUser = firebaseAuth.currentUser
                            val userId = currentUser?.uid

                            val userDocRef = userId?.let { firestore.collection("users").document(it) }

                            // ...

                            userDocRef?.get()
                                ?.addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                        // Data exists for the specified user
                                        val firstName = documentSnapshot.getString("firstName")
                                        val lastName = documentSnapshot.getString("lastName")
                                        val fullName = "$firstName $lastName"
                                        tvNavUserName.text = fullName
                                        tvNavEmail.text = email


                                    } else {
                                        // No data found for the specified user
                                        // Handle this case accordingly
                                    }
                                }
                                ?.addOnFailureListener { e ->
                                    // Handle any errors that occurred during the fetch
                                    Toast.makeText(requireContext(), "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()

                                }

                            showToolbarAndNavigationView()
                            showBottomNavigation()
                            replaceFragment(FoodMenu())

                        }else{
                            val errorMessage = task.exception?.message
                            Toast.makeText(requireContext(), "Wrong email or password, please try again", Toast.LENGTH_SHORT).show()
                        }

                    }
            }else{
                Toast.makeText(requireContext(), "Email and password can not be empty", Toast.LENGTH_SHORT).show()
            }
        }

        tvLoginToSignUp.setOnClickListener {
            navigateToSignupFragment()
        }

        return view
    }

    private fun showToolbarAndNavigationView() {
        // Assuming that MainActivity has a function to show the bottom navigation menu
        if (activity is MainActivity) {
            (activity as MainActivity).showToolbarAndNavigationView()
        }
    }

    private fun showBottomNavigation() {
        // Assuming that MainActivity has a function to show the bottom navigation menu
        if (activity is MainActivity) {
            (activity as MainActivity).showBottomNavigation()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        // Assuming that MainActivity has a function to replace fragments
        if (activity is MainActivity) {
            (activity as MainActivity).replaceFragment(fragment)
        }
    }



    private fun navigateToSignupFragment() {
        // Create an instance of the login fragment
        val signupFragment = Signup()

        // Get the FragmentManager
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager

        // Start a new FragmentTransaction
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Replace the current fragment with the login fragment
        transaction.replace(R.id.flFragment, signupFragment)

        // Add the transaction to the back stack (optional)
        // transaction.addToBackStack(null)

        transaction.commit()
    }
}