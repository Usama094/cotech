package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class WeatherActivity : AppCompatActivity() {

    private lateinit var locationTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var feelsLikeTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var visibilityTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var icon1: ImageView
    private lateinit var icon2: ImageView
    private lateinit var icon3: ImageView
    private lateinit var icon4: ImageView

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        locationTextView = findViewById(R.id.tv_location)
        temperatureTextView = findViewById(R.id.tv_temperature)
        humidityTextView = findViewById(R.id.tv_humidity)
        feelsLikeTextView = findViewById(R.id.tv_feels_like)
        windSpeedTextView = findViewById(R.id.tv_wind)
        visibilityTextView = findViewById(R.id.tv_visibility)
        pressureTextView = findViewById(R.id.tv_pressure)
        icon1 = findViewById(R.id.icon1)
        icon2 = findViewById(R.id.icon2)
        icon3 = findViewById(R.id.icon3)
        icon4 = findViewById(R.id.icon4)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set click listeners for bottom navigation icons
        icon1.setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }

        icon2.setOnClickListener {
            startActivity(Intent(this, HeartRateActivity::class.java))
        }

        // WeatherActivity is already opened, so no action needed here

        icon4.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        // Fetch weather data based on current location
        fetchWeatherForCurrentLocation()
    }

    private fun fetchWeatherForCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // Permissions are already granted, proceed with fetching weather
            fetchWeather()
        }
    }

    private fun fetchWeather() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val response = getWeatherForLocation(location.latitude, location.longitude)
                            if (response.isSuccessful) {
                                val body = response.body?.string()
                                if (!body.isNullOrBlank()) {
                                    val weatherData = parseWeatherData(body)
                                    updateUi(weatherData)
                                } else {
                                    showErrorOnUi("Empty response from server")
                                }
                            } else {
                                showErrorOnUi("API request failed with code: ${response.code}")
                            }
                        } catch (e: IOException) {
                            showErrorOnUi("Error fetching weather data: ${e.message}")
                        } catch (e: Exception) {
                            showErrorOnUi("Error: ${e.message}")
                        }
                    }
                }
            }
    }

    private fun getWeatherForLocation(latitude: Double, longitude: Double): okhttp3.Response {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.weatherapi.com/v1/current.json?key=b18deade862d444b841203259241703&q=$latitude,$longitude&aqi=no")
            .get()
            .build()
        return client.newCall(request).execute()
    }

    private fun parseWeatherData(responseBody: String): WeatherData {
        val jsonObject = JSONObject(responseBody)
        val location = jsonObject.getJSONObject("location")
        val name = location.getString("name")
        val current = jsonObject.getJSONObject("current")
        val temperatureCelsius = current.getDouble("temp_c").toInt()
        val feelsLikeCelsius = current.getDouble("feelslike_c").toInt()
        val windSpeed = current.getDouble("wind_kph")
        val visibility = current.getDouble("vis_km")
        val humidity = current.getInt("humidity")
        val pressureMb = current.getDouble("pressure_mb")

        return WeatherData(name, temperatureCelsius, feelsLikeCelsius, humidity, windSpeed, visibility, pressureMb)
    }

    private fun updateUi(weatherData: WeatherData) {
        runOnUiThread {
            locationTextView.text = "Location: ${weatherData.name}"
            temperatureTextView.text = "Temperature: ${weatherData.temperature} °C"
            feelsLikeTextView.text = "Feels Like: ${weatherData.feelsLike} °C"
            humidityTextView.text = "Humidity: ${weatherData.humidity} %"
            windSpeedTextView.text = "Wind Speed: ${weatherData.windSpeed} km/h"
            visibilityTextView.text = "Visibility: ${weatherData.visibility} km"
            pressureTextView.text = "Pressure: ${weatherData.pressureMb} mb"
        }
    }

    private fun showErrorOnUi(message: String) {
        runOnUiThread {
            locationTextView.text = "Error"
            temperatureTextView.text = message
            feelsLikeTextView.text = ""
            humidityTextView.text = ""
            windSpeedTextView.text = ""
            visibilityTextView.text = ""
            pressureTextView.text = ""
        }
    }

    data class WeatherData(
        val name: String,
        val temperature: Int,
        val feelsLike: Int,
        val humidity: Int,
        val windSpeed: Double,
        val visibility: Double,
        val pressureMb: Double
    )

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch weather
                fetchWeather()
            } else {
                // Permission denied, handle accordingly
                showErrorOnUi("Location permission denied")
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}
