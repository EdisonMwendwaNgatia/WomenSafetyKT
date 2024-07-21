package com.example.womenssafetyapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.content.ContextCompat

@SuppressLint("HandlerLeak")
class BgService : Service(), AccelerometerListener {
    private var mServiceLooper: Looper? = null
    private var mServiceHandler: ServiceHandler? = null
    private lateinit var sharedPreferences: SharedPreferences

    // Handler that receives messages from the thread.
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            // Handle messages here if needed.
        }
    }

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        if (AccelerometerManager.isSupported(this)) {
            AccelerometerManager.startListening(this)
        }
        val thread = HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()
        mServiceLooper = thread.looper
        mServiceHandler = ServiceHandler(mServiceLooper!!)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val msg = mServiceHandler!!.obtainMessage() // Asserting non-null here since mServiceHandler should be initialized in onCreate()
        msg.arg1 = startId
        mServiceHandler!!.sendMessage(msg) // Asserting non-null here since mServiceHandler should be initialized in onCreate()
        return START_STICKY
    }

    inner class GeocoderHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            // Handle geocoder messages here if needed.
            if (msg.what == 1) {
                val bundle = msg.data
                val address = bundle.getString("address")
                // Do something with the address
                Toast.makeText(applicationContext, address, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (AccelerometerManager.isListening) {
            AccelerometerManager.stopListening()
        }
        val context = applicationContext
        Toast.makeText(context, "Women Safety App Service Stopped", Toast.LENGTH_SHORT).show()
    }

    // Implementing AccelerometerListener interface methods
    override fun onAccelerationChanged(x: Float, y: Float, z: Float) {
        // Handle accelerometer data change if needed
    }

    override fun onEmergencyDetected() {
        val gps = GPSTracker(this@BgService)
        if (gps.canGetLocation()) {
            val latitude: Double = gps.getLatitude()
            val longitude: Double = gps.getLongitude()
            val rGeocoder = RGeocoder()
            rGeocoder.getAddressFromLocation(latitude, longitude, applicationContext, GeocoderHandler(mServiceLooper!!))

            // Check for SMS permission before sending message
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                sendEmergencyMessage(latitude, longitude)
            } else {
                // Permission not granted, handle this scenario (notify user, log, etc.)
                Toast.makeText(applicationContext, "SMS permission not granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            gps.showSettingsAlert()
        }
    }

    private fun sendEmergencyMessage(latitude: Double, longitude: Double) {
        val userName = sharedPreferences.getString("UserName", "Your Kin")
        val emergencyNumber = sharedPreferences.getString("UserNumber", "+254742254329") // Default number if not set
        val message = "$userName at Latitude: $latitude, Longitude: $longitude is in danger."

        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(emergencyNumber, null, message, null, null)
            Toast.makeText(applicationContext, "Emergency Message Sent", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Failed to send emergency message", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onShake(force: Float) {
        // Handle shake event
        val gps = GPSTracker(this@BgService)
        if (gps.canGetLocation()) {
            val latitude: Double = gps.getLatitude()
            val longitude: Double = gps.getLongitude()
            val rGeocoder = RGeocoder()
            rGeocoder.getAddressFromLocation(latitude, longitude, applicationContext, GeocoderHandler(mServiceLooper!!))
            Toast.makeText(applicationContext, "onShake", Toast.LENGTH_SHORT).show()

            // Check for SMS permission before sending message
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                sendEmergencyMessage(latitude, longitude)
            } else {
                // Permission not granted, handle this scenario (notify user, log, etc.)
                Toast.makeText(applicationContext, "SMS permission not granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            gps.showSettingsAlert()
        }
    }
}
