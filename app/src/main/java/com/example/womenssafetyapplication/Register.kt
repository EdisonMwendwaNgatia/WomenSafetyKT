package com.example.womenssafetyapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class Register : Activity() {
    private var name: EditText? = null
    private var number: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        // Initialize EditText fields
        name = findViewById(R.id.editText1)
        number = findViewById(R.id.editText2)
    }

    fun display(v: View?) {
        val i_view = Intent(this@Register, Display::class.java)
        startActivity(i_view)
    }

    fun instructions(v: View?) {
        val i_help = Intent(this@Register, Instructions::class.java)
        startActivity(i_help)
    }

    fun storeInDB(v: View?) {
        Toast.makeText(applicationContext, "Save started", Toast.LENGTH_LONG).show()

        // Ensure name and number are not null before proceeding
        val str_name = name?.text.toString()
        val str_number = number?.text.toString()

        if (str_name.isNotBlank() && str_number.isNotBlank()) {
            val db = openOrCreateDatabase("NumDB", MODE_PRIVATE, null)
            db.execSQL("CREATE TABLE IF NOT EXISTS details(name VARCHAR, number VARCHAR);")

            // Check if there are already two entries in the table
            val c = db.rawQuery("SELECT COUNT(*) FROM details", null)
            c.moveToFirst()
            val count = c.getInt(0)
            c.close()

            if (count < 2) {
                db.execSQL("INSERT INTO details VALUES('$str_name', '$str_number');")
                Toast.makeText(applicationContext, "Successfully Saved", Toast.LENGTH_SHORT).show()
            } else {
                db.execSQL("INSERT INTO details VALUES('$str_name', '$str_number');")
                Toast.makeText(
                    applicationContext,
                    "Maximum numbers limit reached. Previous numbers are replaced.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            db.close()
        } else {
            Toast.makeText(applicationContext, "Please enter both name and number", Toast.LENGTH_SHORT).show()
        }
    }
}
