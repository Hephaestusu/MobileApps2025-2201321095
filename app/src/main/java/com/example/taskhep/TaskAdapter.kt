package com.example.taskhep

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val onItemClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val items = mutableListOf<Task>()

    fun submitList(newItems: List<Task>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): Task {
        return items[position]
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)

        fun bind(task: Task) {
            tvTitle.text = task.title
            tvDescription.text = task.description

            itemView.setOnClickListener {
                onItemClick(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
