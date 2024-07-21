package com.example.womenssafetyapplication

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast

class LoginActivity : Activity() {
    private lateinit var name: EditText
    private lateinit var number: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        name = findViewById(R.id.editTextName)
        number = findViewById(R.id.editTextNumber)
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        Log.d("LoginActivity", "onCreate: Activity started")
    }

    fun saveUserDetails(v: View) {
        val userName = name.text.toString()
        val userNumber = number.text.toString()

        if (userName.isNotBlank() && userNumber.isNotBlank()) {
            val editor = sharedPreferences.edit()
            editor.putString("UserName", userName)
            editor.putString("UserNumber", userNumber)
            editor.apply()

            Toast.makeText(this, "Details Saved", Toast.LENGTH_SHORT).show()

            Log.d("LoginActivity", "saveUserDetails: User details saved. Navigating to MainActivity")

            // Navigate to MainActivity or any other appropriate activity
            startActivity(Intent(this, MainActivity::class.java))

            finish()
        } else {
            Toast.makeText(this, "Please enter both name and number", Toast.LENGTH_SHORT).show()
            Log.d("LoginActivity", "saveUserDetails: User details not complete")
        }
    }
}
