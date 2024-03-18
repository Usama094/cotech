package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HeartRateActivity : AppCompatActivity() {

    private lateinit var heartRateInput: EditText
    private lateinit var bloodSugarInput: EditText
    private lateinit var waterIntakeInput: EditText
    private lateinit var checkHealthStatusButton: Button
    private lateinit var icon1: ImageView
    private lateinit var icon2: ImageView
    private lateinit var icon3: ImageView
    private lateinit var icon4: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate)

        // Initialize views
        heartRateInput = findViewById(R.id.heartRateInput)
        bloodSugarInput = findViewById(R.id.bloodSugarInput)
        waterIntakeInput = findViewById(R.id.waterIntakeInput)
        checkHealthStatusButton = findViewById(R.id.checkHealthStatusButton)
        icon1 = findViewById(R.id.icon1)
        icon2 = findViewById(R.id.icon2)
        icon3 = findViewById(R.id.icon3)
        icon4 = findViewById(R.id.icon4)

        // Set click listener for the check health status button
        checkHealthStatusButton.setOnClickListener {
            // Retrieve input values
            val heartRate = heartRateInput.text.toString().toIntOrNull()
            val bloodSugarLevel = bloodSugarInput.text.toString().toDoubleOrNull()
            val waterIntake = waterIntakeInput.text.toString().toIntOrNull()

            // Check if any input is empty or invalid
            if (heartRate == null || bloodSugarLevel == null || waterIntake == null) {
                showToast("Please fill in all fields with valid values.")
                return@setOnClickListener
            }

            // Analyze health status
            analyzeHealthStatus(heartRate, bloodSugarLevel, waterIntake)
        }

        // Set click listeners for bottom navigation icons
        icon1.setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }

        icon2.setOnClickListener {
            // You're already in the HeartRateActivity, so nothing needs to be done here
        }

        icon3.setOnClickListener {
            startActivity(Intent(this, WeatherActivity::class.java))
        }

        icon4.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun analyzeHealthStatus(heartRate: Int, bloodSugarLevel: Double, waterIntake: Int) {
        // Analyze heart rate
        val heartRateStatus = when {
            heartRate < 60 -> "Low heart rate. Please consult a doctor."
            heartRate in 60..100 -> "Normal heart rate."
            else -> "High heart rate. Please consult a doctor."
        }

        // Analyze blood sugar level
        val bloodSugarStatus = when {
            bloodSugarLevel < 70 -> "Low blood sugar level. Please take some sugar."
            bloodSugarLevel in 70.0..140.0 -> "Normal blood sugar level."
            else -> "High blood sugar level. Please consult a doctor."
        }

        // Analyze water intake
        val waterIntakeStatus = when {
            waterIntake < 8 -> "You need to drink more water."
            waterIntake == 8 -> "You have consumed sufficient water for the day."
            else -> "You have consumed more water than required."
        }

        // Display status messages using Toast
        showToast(heartRateStatus)
        showToast(bloodSugarStatus)
        showToast(waterIntakeStatus)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
