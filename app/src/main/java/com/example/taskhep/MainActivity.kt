package com.example.taskhep

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter

    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_DARK_MODE = "dark_mode"
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

        val switchTheme: SwitchMaterial = findViewById(R.id.switchTheme)
        val rvTasks: RecyclerView = findViewById(R.id.rvTasks)
        val fabAddTask: FloatingActionButton = findViewById(R.id.fabAddTask)
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
        adapter = TaskAdapter { task ->
            val intent = Intent(this, AddEditTaskActivity::class.java)
            intent.putExtra("task_id", task.id)
            intent.putExtra("task_title", task.title)
            intent.putExtra("task_description", task.description)
            startActivity(intent)
        }

        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = adapter
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

                Thread {
                    val db = TaskDatabase.getDatabase(this@MainActivity)
                    db.taskDao().delete(taskToDelete)

                    runOnUiThread {
                        loadTasksFromDb()
                    }
                }.start()
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

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(rvTasks)

        fabAddTask.setOnClickListener {
            val intent = Intent(this, AddEditTaskActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasksFromDb()
    }

    private fun loadTasksFromDb() {
        Thread {
            val db = TaskDatabase.getDatabase(this)
            val tasks = db.taskDao().getAllTasks()

            runOnUiThread {
                adapter.submitList(tasks)
            }
        }.start()
    }
}
