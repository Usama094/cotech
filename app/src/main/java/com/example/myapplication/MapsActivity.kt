package com.example.myapplication// com.example.myapplication.MapsActivity.kt
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MapsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Initialize osmdroid configuration
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        // Find the MapView from the layout
        val mapView = findViewById<MapView>(R.id.mapView)
        val searchView = findViewById<EditText>(R.id.searchView)



        // Set the tile source (e.g., MAPNIK)
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        // Additional configuration for the MapView can be done here
        // For example, enabling multi-touch controls:
        mapView.setMultiTouchControls(true)

        // Enable zoom controls
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)


// Zoom In
        mapView.controller.zoomIn()

// Zoom Out
        mapView.controller.zoomOut()

// Fetch current location
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // Update map view with current location
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                mapView.controller.animateTo(geoPoint)
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String) {
                if (provider == LocationManager.GPS_PROVIDER) {
                    // GPS is now enabled
                    Log.d("Location", "GPS is now enabled")
                    // You can perform actions here, like requesting location updates
                    // or updating the UI
                }
            }
            override fun onProviderDisabled(provider: String) {}
        }
// Request location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )

        // Show GPS coordinates
        class MyLocationOverlay(context: Context) : Overlay() {
            private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
            private var location: GeoPoint? = null

            init {
                paint.color = context.resources.getColor(android.R.color.holo_blue_light)
                paint.style = Paint.Style.FILL
                paint.textSize = 30f
            }
            override fun draw(canvas: Canvas?, mapView: MapView?, shadow: Boolean) {
                super.draw(canvas, mapView, shadow)
                location?.let { geoPoint ->
                    val point = mapView?.projection?.toPixels(geoPoint, null)
                    point?.let { pixelPoint ->
                        val latitude = geoPoint.latitude
                        val longitude = geoPoint.longitude
                        canvas?.drawCircle(
                            pixelPoint.x.toFloat(),
                            pixelPoint.y.toFloat(),
                            10f,
                            paint
                        )
                        canvas?.drawText(
                            "Lat: $latitude, Long: $longitude",
                            pixelPoint.x.toFloat(),
                            pixelPoint.y.toFloat() - 20f,
                            paint
                        )
                    }
                }
            }

            fun setLocation(geoPoint: GeoPoint) {
                location = geoPoint
            }
        }

        val myLocationOverlay = MyLocationOverlay(applicationContext)
        mapView.overlays.add(myLocationOverlay)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f) { location ->
            val geoPoint = GeoPoint(location.latitude, location.longitude)
            myLocationOverlay.setLocation(geoPoint)
            mapView.invalidate()
        }

        searchView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchQuery = searchView.text.toString()  // Get the entire user input
                searchLocation(searchQuery)  // Pass the input to the search function
                true  // Indicate that the event has been handled
            } else {
                false  // Let the system handle other actions
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun searchLocation(locationName: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val result = getGeoPointFromNominatim(locationName)
            withContext(Dispatchers.Main) {
                if (result != null) {
                    val mapView = findViewById<MapView>(R.id.mapView)
                    // Set a specific zoom level (e.g., 16 for street level)
                    mapView.controller?.setZoom(16.0)
                    mapView.controller?.setCenter(result)
                } else {
                    Toast.makeText(this@MapsActivity, "Location not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getGeoPointFromNominatim(locationName: String): GeoPoint? {
        var geoPoint: GeoPoint? = null
        try {
            val url = URL("https://nominatim.openstreetmap.org/search?q=$locationName&format=json")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val inputStream = connection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            bufferedReader.close()

            val json = stringBuilder.toString()
            val jsonArray = JSONArray(json)
            if (jsonArray.length() > 0) {
                val jsonObject = jsonArray.getJSONObject(0)
                val lat = jsonObject.getDouble("lat")
                val lon = jsonObject.getDouble("lon")
                geoPoint = GeoPoint(lat, lon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return geoPoint
    }

}



