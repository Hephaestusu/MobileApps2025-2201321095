package com.example.taskhep

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_settings)

        val btnRed: Button = findViewById(R.id.btnColorRed)
        val btnGreen: Button = findViewById(R.id.btnColorGreen)
        val btnBlue: Button = findViewById(R.id.btnColorBlue)
        val btnPurple: Button = findViewById(R.id.btnColorPurple)
        val btnYellow: Button = findViewById(R.id.btnColorYellow)

        fun saveColor(colorRes: Int) {
            val color = ContextCompat.getColor(this, colorRes)
            prefs.edit().putInt(KEY_ACCENT_COLOR, color).apply()
            finish()
        }

        btnRed.setOnClickListener { saveColor(R.color.accent_red) }
        btnGreen.setOnClickListener { saveColor(R.color.accent_green) }
        btnBlue.setOnClickListener { saveColor(R.color.accent_blue) }
        btnPurple.setOnClickListener { saveColor(R.color.accent_purple) }
        btnYellow.setOnClickListener { saveColor(R.color.accent_yellow) }
    }
}
