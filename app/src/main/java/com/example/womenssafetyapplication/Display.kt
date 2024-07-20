package com.example.womenssafetyapplication

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View

class Display : Activity() {
    private var c: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        val db = openOrCreateDatabase("NumDB", MODE_PRIVATE, null)
        c = db.rawQuery("SELECT * FROM details", null)

        if (c?.count == 0) {
            showMessage("Error", "No records found.")
            return
        }

        val buffer = StringBuffer()
        c?.apply {
            while (moveToNext()) {
                buffer.append("Name: ${getString(0)}\n")
                buffer.append("Number: ${getString(1)}\n")
            }
        }

        showMessage("Details", buffer.toString())

        val i_startservice = Intent(this@Display, BgService::class.java)
        startService(i_startservice)
    }

    private fun showMessage(title: String?, message: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.show()
    }

    fun back(v: View?) {
        val i_back = Intent(this@Display, MainActivity::class.java)
        startActivity(i_back)
    }

    override fun onDestroy() {
        super.onDestroy()
        c?.close() // Close the cursor when the activity is destroyed
    }
}
