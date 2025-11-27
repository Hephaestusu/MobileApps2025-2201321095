package com.example.taskhep

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvTasks: RecyclerView = findViewById(R.id.rvTasks)
        adapter = TaskAdapter()

        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = adapter

        loadTasksFromDb()

        val fabAddTask: FloatingActionButton = findViewById(R.id.fabAddTask)
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
