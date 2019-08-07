package com.example.odometer_kotlin

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var bound : Boolean = false
    private var odometerService : OdometerService? = null;

    val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder : OdometerService.OdometerBinder = service as OdometerService.OdometerBinder
            odometerService = binder.getOdometerService()
            bound = true
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, OdometerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
        bound = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        disPlayDistance()
    }

    private lateinit var mRunnable : Runnable
    fun disPlayDistance() {
        val handler = Handler()
        mRunnable = Runnable {
            var distance : Double = 0.0
            if (bound && odometerService != null){
                distance = odometerService?.getDistance() ?: 0.0
            }
            val strDistance = String.format(Locale.getDefault(), "%1$,.2f miles", distance)
            txtDistance.text = strDistance
            handler.postDelayed(mRunnable, 1000)
        }
        handler.post(mRunnable)
    }
}
