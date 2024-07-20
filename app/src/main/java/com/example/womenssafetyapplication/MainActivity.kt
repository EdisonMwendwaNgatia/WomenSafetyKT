package com.example.womenssafetyapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : Activity() {

    private val REQUEST_SMS_PERMISSION = 1
    private val REQUEST_LOCATION_PERMISSION = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check and request permissions
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        // Check for SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), REQUEST_SMS_PERMISSION)
        }

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_SMS_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // SMS permission granted
                } else {
                    // SMS permission denied
                }
            }
            REQUEST_LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Location permission granted
                } else {
                    // Location permission denied
                }
            }
        }
    }

    fun register(v: View?) {
        val i_register = Intent(this@MainActivity, Register::class.java)
        startActivity(i_register)
    }

    fun display_no(v: View?) {
        val i_view = Intent(this@MainActivity, Display::class.java)
        startActivity(i_view)
    }

    fun instruct(v: View?) {
        val i_help = Intent(this@MainActivity, Instructions::class.java)
        startActivity(i_help)
    }

    fun verify(v: View?) {
        val i_verify = Intent(this@MainActivity, Verify::class.java)
        startActivity(i_verify)
    }

    fun startEmergencyService(v: View?) {
        val i_startservice = Intent(this@MainActivity, BgService::class.java)
        startService(i_startservice)
    }
}
