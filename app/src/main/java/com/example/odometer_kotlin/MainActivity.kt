package com.example.odometer_kotlin

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.jar.Manifest

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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 999)
        } else {
            val intent = Intent(this, OdometerService::class.java)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            999 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(this, OdometerService::class.java)
                    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    val pendingIntent = PendingIntent.getActivities(this, 111, arrayOf(intent), PendingIntent.FLAG_UPDATE_CURRENT)

                    val notificationBuilder = Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setVibrate(longArrayOf(0,1000))

                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(88, notificationBuilder.build())

                }
            }
        }
    }

    private lateinit var mRunnable : Runnable
    fun disPlayDistance() {
        val handler = Handler()
        mRunnable = Runnable {
            var distance : Float = 0.0f
            if (bound && odometerService != null){
                distance = odometerService?.getDistance() ?: 0.0f
            }
            val strDistance = String.format(Locale.getDefault(), "%1$,.2f miles", distance)
            txtDistance.text = strDistance
            handler.postDelayed(mRunnable, 1000)
        }
        handler.post(mRunnable)
    }
}
