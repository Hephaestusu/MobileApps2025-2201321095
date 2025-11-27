package com.example.taskhep

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddEditTaskActivity : AppCompatActivity() {

    private var taskId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_task)

        val etTitle: EditText = findViewById(R.id.etTitle)
        val etDescription: EditText = findViewById(R.id.etDescription)
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnCancel: Button = findViewById(R.id.btnCancel)
        val btnShare: Button = findViewById(R.id.btnShare)
        val idFromIntent = intent.getIntExtra("task_id", -1)
        if (idFromIntent != -1) {
            taskId = idFromIntent
            val titleFromIntent = intent.getStringExtra("task_title") ?: ""
            val descFromIntent = intent.getStringExtra("task_description") ?: ""

            etTitle.setText(titleFromIntent)
            etDescription.setText(descFromIntent)
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (title.isEmpty()) {
                etTitle.error = "Title is required"
                return@setOnClickListener
            }

            Thread {
                val db = TaskDatabase.getDatabase(this)
                val dao = db.taskDao()

                if (taskId == null) {
                    dao.insert(
                        Task(
                            title = title,
                            description = description
                        )
                    )
                } else {
                    dao.update(
                        Task(
                            id = taskId!!,
                            title = title,
                            description = description
                        )
                    )
                }

                runOnUiThread {
                    finish()
                }
            }.start()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnShare.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (title.isEmpty() && description.isEmpty()) {
                etTitle.error = "Nothing to share"
                return@setOnClickListener
            }

            val shareText = buildString {
                if (title.isNotEmpty()) append(title)
                if (description.isNotEmpty()) {
                    if (isNotEmpty()) append("\n\n")
                    append(description)
                }
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Task: $title")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            startActivity(Intent.createChooser(shareIntent, "Share task via"))
        }
    }
}
