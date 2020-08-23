package com.jmeek.graphcreator

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startUrlActivity(view: View) {
        val intent = Intent(this, GraphCreatorURLActivity::class.java)
        startActivity(intent)
    }
}