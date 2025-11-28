package com.example.taskhep

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class TaskAdapter(
    private val onItemClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_ACCENT_COLOR = "accent_color"

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem == newItem
        }
    }

    fun getItemAt(position: Int): Task = getItem(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        itemView: View,
        private val onItemClick: (Task) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val card: MaterialCardView = itemView.findViewById(R.id.cardTask)

        fun bind(task: Task) {
            tvTitle.text = task.title
            tvDescription.text = task.description

            itemView.setOnClickListener { onItemClick(task) }

            val context = itemView.context
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val defaultColor = ContextCompat.getColor(context, R.color.accent_purple)
            val accent = prefs.getInt(KEY_ACCENT_COLOR, defaultColor)

            card.strokeColor = accent
        }
    }
}
