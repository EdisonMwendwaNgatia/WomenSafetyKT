package com.example.womenssafetyapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View

class Instructions : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions)
    }

    fun back(v: View?) {
        val i_back = Intent(
            this@Instructions,
            MainActivity::class.java
        )
        startActivity(i_back)
    }
}