package com.example.taskhep

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddEditTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        val etTitle: EditText = findViewById(R.id.etTitle)
        val etDescription: EditText = findViewById(R.id.etDescription)
        val btnSave: Button = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (title.isEmpty()) {
                etTitle.error = "Title is required"
                return@setOnClickListener
            }

            Thread {
                val db = TaskDatabase.getDatabase(this)
                db.taskDao().insert(
                    Task(
                        title = title,
                        description = description
                    )
                )

                runOnUiThread {
                    finish()
                }
            }.start()
        }
    }
}
