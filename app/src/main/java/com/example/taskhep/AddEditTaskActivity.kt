package com.example.taskhep

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AddEditTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        val btnSave: Button = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {

            finish()
        }
    }
}

