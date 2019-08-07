package com.example.odometer_kotlin

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.ContextCompat
import java.util.*

class OdometerService : Service() {

    private val odometerBinder = OdometerBinder();
    private val random = Random();

    private lateinit var locationListener : LocationListener
    private lateinit var locationManager: LocationManager

    private var lastLocation : Location? = null
    private var distanceInMeters : Float? = 0.0f

    inner class OdometerBinder : Binder(){
        fun getOdometerService() : OdometerService {
            return this@OdometerService
        }
    }

    override fun onCreate() {
        super.onCreate()

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                if (lastLocation == null) {
                    lastLocation = location
                }
                distanceInMeters = distanceInMeters?.plus(location?.distanceTo(lastLocation) ?: 0.0f)
                lastLocation = location
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            val provider = locationManager?.getBestProvider(Criteria(), true)
            if (provider != null) {
                locationManager?.requestLocationUpdates(provider, 1000, 1.0f, locationListener);
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationListener != null && locationManager != null) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                locationManager?.removeUpdates(locationListener)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return odometerBinder
    }

    fun getDistance() : Float? {
        return distanceInMeters//random.nextDouble()
    }
}
