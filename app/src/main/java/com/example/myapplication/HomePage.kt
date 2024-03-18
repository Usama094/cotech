package com.example.myapplication

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // Find the heart rate card view, weather card view, and map card view by their IDs
        val heartRateCardView = findViewById<CardView>(R.id.heartRateCardView)
        val weatherCardView = findViewById<CardView>(R.id.weatherCardView)
        val mapCardView = findViewById<CardView>(R.id.mapCardView)

        // Set click listeners for the heart rate card view, weather card view, and map card view
        heartRateCardView.setOnClickListener {
            navigateToHeartRatePage()
        }

        weatherCardView.setOnClickListener {
            navigateToWeatherPage()
        }

        mapCardView.setOnClickListener {
            navigateToMapPage()
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to exit?")
            .setPositiveButton("Exit") { dialog, id ->
                // Call super.onBackPressed() to allow exiting after confirmation
                super.onBackPressed()
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()  // Dismiss the dialog if user chooses not to exit
            }
        builder.create().show()
    }

    private fun navigateToHeartRatePage() {
        val intent = Intent(this, HeartRateActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToWeatherPage() {
        val intent = Intent(this, WeatherActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMapPage() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}
