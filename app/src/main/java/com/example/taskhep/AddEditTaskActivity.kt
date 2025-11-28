package com.example.taskhep

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

class AddEditTaskActivity : AppCompatActivity() {

    private var taskId: Int? = null

    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_ACCENT_COLOR = "accent_color"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isDark = prefs.getBoolean(KEY_DARK_MODE, false)
        val targetMode = if (isDark) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode)
        }

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
                    dao.insert(Task(title = title, description = description))
                } else {
                    dao.update(Task(id = taskId!!, title = title, description = description))
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

        applyAccentColor()
    }

    private fun applyAccentColor() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val defaultColor = ContextCompat.getColor(this, R.color.accent_purple)
        val accent = prefs.getInt(KEY_ACCENT_COLOR, defaultColor)

        val tvScreenTitle: TextView = findViewById(R.id.tvScreenTitle)
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnCancel: Button = findViewById(R.id.btnCancel)
        val btnShare: Button = findViewById(R.id.btnShare)

        tvScreenTitle.setTextColor(accent)

        val tint = ColorStateList.valueOf(accent)
        btnSave.backgroundTintList = tint
        btnCancel.backgroundTintList = tint
        btnShare.backgroundTintList = tint
        val textColor = if (isColorDark(accent)) Color.WHITE else Color.BLACK
        btnSave.setTextColor(textColor)
        btnCancel.setTextColor(textColor)
        btnShare.setTextColor(textColor)
    }

    private fun isColorDark(color: Int): Boolean {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }
}
