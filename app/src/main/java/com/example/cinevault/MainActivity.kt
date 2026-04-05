package com.example.cinevault

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        if (savedInstanceState == null) {
            openFragment(HomeFragment())
            bottomNavigationView.selectedItemId = R.id.menu_explore
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_explore -> {
                    openFragment(HomeFragment())
                    true
                }

                R.id.menu_search -> {
                    openFragment(SearchFragment())
                    true
                }

                R.id.menu_watchlist -> {
                    openFragment(WatchlistFragment())
                    true
                }

                R.id.menu_favorites -> {
                    openFragment(FavoritesFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}