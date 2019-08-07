package com.example.odometer_kotlin

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.*

class OdometerService : Service() {

    private val odometerBinder = OdometerBinder();
    private val random = Random();

    inner class OdometerBinder : Binder(){
        fun getOdometerService() : OdometerService {
            return this@OdometerService
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return odometerBinder
    }

    fun getDistance() : Double {
        return random.nextDouble()
    }
}
