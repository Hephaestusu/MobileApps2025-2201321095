package com.example.taskhep

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter
    private lateinit var viewModel: TaskViewModel

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
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        val root = findViewById<ConstraintLayout>(R.id.rootMain)
        val switchTheme: SwitchMaterial = findViewById(R.id.switchTheme)
        val rvTasks: RecyclerView = findViewById(R.id.rvTasks)
        val fabAddTask: FloatingActionButton = findViewById(R.id.fabAddTask)
        val btnSettings: ImageButton = findViewById(R.id.btnSettings)

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val density = resources.displayMetrics.density
            val extraTop = (8f * density).toInt()
            v.setPadding(
                v.paddingLeft,
                statusBars.top + extraTop,
                v.paddingRight,
                v.paddingBottom
            )
            insets
        }

        switchTheme.isChecked = isDark
        switchTheme.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean(KEY_DARK_MODE, checked).apply()
            val mode = if (checked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        adapter = TaskAdapter { task ->
            val intent = Intent(this, AddEditTaskActivity::class.java)
            intent.putExtra("task_id", task.id)
            intent.putExtra("task_title", task.title)
            intent.putExtra("task_description", task.description)
            startActivity(intent)
        }

        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = adapter

        viewModel.tasks.observe(this, Observer { tasks ->
            adapter.submitList(tasks)
        })

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            private val deletePaint = Paint().apply {
                color = Color.parseColor("#B00020")
                isAntiAlias = true
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val taskToDelete = adapter.getItemAt(position)

                val builder = AlertDialog.Builder(this@MainActivity)
                    .setTitle("Delete task?")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteTask(taskToDelete)
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        adapter.notifyItemChanged(position)
                    }
                    .setOnCancelListener {
                        adapter.notifyItemChanged(position)
                    }

                val dialog = builder.create()

                dialog.setOnShowListener {
                    val accent = getAccentColor()
                    val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    val negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

                    positive?.setTextColor(accent)
                    negative?.setTextColor(accent)
                }

                dialog.show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                if (dX < 0) {
                    val density = recyclerView.resources.displayMetrics.density
                    val verticalMargin = 8f * density
                    val horizontalPadding = 8f * density
                    val cornerRadius = 12f * density

                    val left = itemView.right.toFloat() + dX + horizontalPadding
                    val right = itemView.right.toFloat() - horizontalPadding
                    val top = itemView.top.toFloat() + verticalMargin
                    val bottom = itemView.bottom.toFloat() - verticalMargin

                    val background = RectF(left, top, right, bottom)
                    c.drawRoundRect(background, cornerRadius, cornerRadius, deletePaint)

                    val deleteIcon = ContextCompat.getDrawable(
                        this@MainActivity,
                        android.R.drawable.ic_menu_delete
                    )

                    deleteIcon?.let { icon ->
                        val iconWidth = icon.intrinsicWidth
                        val iconHeight = icon.intrinsicHeight

                        val iconRight = right - 16f * density
                        val iconLeft = iconRight - iconWidth
                        val iconTop = top + (bottom - top - iconHeight) / 2
                        val iconBottom = iconTop + iconHeight

                        icon.setBounds(
                            iconLeft.toInt(),
                            iconTop.toInt(),
                            iconRight.toInt(),
                            iconBottom.toInt()
                        )
                        icon.draw(c)
                    }
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })

        itemTouchHelper.attachToRecyclerView(rvTasks)

        fabAddTask.setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            startActivity(intent)
        }

        applyAccentColor()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTasks()
        applyAccentColor()
    }

    private fun applyAccentColor() {
        val accent = getAccentColor()

        val tvAppTitle: TextView = findViewById(R.id.tvAppTitle)
        val fabAddTask: FloatingActionButton = findViewById(R.id.fabAddTask)
        val switchTheme: SwitchMaterial = findViewById(R.id.switchTheme)
        val btnSettings: ImageButton = findViewById(R.id.btnSettings)

        tvAppTitle.setTextColor(accent)
        fabAddTask.backgroundTintList = ColorStateList.valueOf(accent)
        btnSettings.imageTintList = ColorStateList.valueOf(accent)

        val thumb = ColorStateList.valueOf(accent)
        val track = ColorStateList.valueOf(adjustAlpha(accent, 0.4f))
        switchTheme.thumbTintList = thumb
        switchTheme.trackTintList = track

        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    private fun getAccentColor(): Int {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val defaultColor = ContextCompat.getColor(this, R.color.accent_purple)
        return prefs.getInt(KEY_ACCENT_COLOR, defaultColor)
    }

    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).toInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }
}
