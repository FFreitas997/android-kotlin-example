package com.ffreitas.flowify.ui.main.components.board.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ffreitas.flowify.data.models.Task
import com.ffreitas.flowify.databinding.TaskItemBinding

class TasksAdapter(private val tasks: MutableList<Task>) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    fun removeAt(position: Int): Task {
        val item = tasks[position]
        tasks.removeAt(position)
        notifyItemRemoved(position)
        return item
    }

    inner class TaskViewHolder(layout: TaskItemBinding) : RecyclerView.ViewHolder(layout.root) {

        private val tvTitle = layout.tvTitle
        private val tvDate = layout.tvDate
        private val tvPerson = layout.tvPerson
        private val tvDescription = layout.tvDescription

        fun bind(task: Task) {
            tvTitle.text = task.title
            tvDate.text = task.createdAt
            tvPerson.text = task.createdByName
            tvDescription.text = task.description
        }
    }
}