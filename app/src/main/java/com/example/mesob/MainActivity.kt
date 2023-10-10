package com.example.mesob

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.example.mesob.databinding.ActivityMainBinding
import kotlin.collections.Map


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
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




        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)


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


        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flFragment, fragment)
        fragmentTransaction.commit()
    }
}