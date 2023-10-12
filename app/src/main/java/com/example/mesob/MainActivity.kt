package com.example.mesob

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.example.mesob.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.Map


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var tvNavUserName: TextView
    private lateinit var tvNavEmail : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav )

        val navigationView = findViewById<NavigationView>(R.id.navigationDrawer)
        val headerView = navigationView.getHeaderView(0)
        tvNavUserName = headerView.findViewById<TextView>(R.id.tvNavUserName)
        tvNavEmail = headerView.findViewById<TextView>(R.id.tvNavEmail)


        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

//        firebaseAuth.signOut()

        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid
        val email = currentUser?.email.toString()


        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)


        if (currentUser == null) {
            hideToolbarAndNavigationView()
            hideBottomNavigation()
            // User is not authenticated, redirect to the login screen (Login fragment)
            replaceFragment(Login())
        } else {

            val userDocRef = userId?.let { firestore.collection("users").document(it) }


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
                    Toast.makeText(
                        this, // Use 'this' or 'applicationContext' here
                        "Error fetching user data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            showToolbarAndNavigationView()
            showBottomNavigation()
            replaceFragment(FoodMenu())
        }



        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.miFoodMenu -> replaceFragment(FoodMenu())
                R.id.miReservations -> replaceFragment(Reservations())
                R.id.miRefer -> replaceFragment(Refer())
//                R.id.miMap -> replaceFragment(kotlin.collections.Map())
            }
            true
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.miSettings -> replaceFragment(Settings())
            R.id.miAbout -> replaceFragment(About())
            R.id.miHelp -> replaceFragment(Help())
            R.id.miShare -> {
                val appLink = "https://play.google.com/store/apps/details?id=com.example.yourapp"

                // Create an intent to share your app's link
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this cool app: $appLink")

                // Start the share activity
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }

            R.id.miLogout -> {

                hideToolbarAndNavigationView()
                hideBottomNavigation()
                firebaseAuth.signOut()
                replaceFragment(Login())
            }


        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer((GravityCompat.START))
        }else{
            super.getOnBackPressedDispatcher().onBackPressed()
        }
    }



    fun showBottomNavigation() {
        binding.bottomNavigationView.visibility = View.VISIBLE

    }

    private fun hideBottomNavigation() {
        binding.bottomNavigationView.visibility = View.GONE

    }

    fun showToolbarAndNavigationView() {
        binding.toolbar.visibility = View.VISIBLE
        binding.navigationDrawer.visibility = View.VISIBLE
    }

    fun hideToolbarAndNavigationView() {
        binding.toolbar.visibility = View.GONE
        binding.navigationDrawer.visibility = View.GONE
    }


    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flFragment, fragment)
        fragmentTransaction.commit()
    }
}