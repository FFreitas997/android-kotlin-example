package com.ffreitas.flowify.ui.main.components.task

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ffreitas.flowify.R
import com.ffreitas.flowify.data.models.Task
import com.ffreitas.flowify.databinding.MainTaskItemBinding

class MainTasksAdapter(private val context: Context, private val tasks: List<Task>) :
    RecyclerView.Adapter<MainTasksAdapter.TaskViewHolder>() {

    private var onClick: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = MainTaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
        val handler = onClick ?: return
        holder.itemView.setOnClickListener { handler.onClick(tasks[position]) }
    }

    override fun getItemCount(): Int = tasks.size

    fun setOnClickListener(listener: OnClickListener) {
        onClick = listener
    }

    inner class TaskViewHolder(val context: Context, layout: MainTaskItemBinding) :
        RecyclerView.ViewHolder(layout.root) {

        private val tvTitle = layout.taskTitle
        private val tvCreatedBy = layout.taskCreatedBy
        private val tvDate = layout.taskDate
        private val tvBoardName = layout.boardName
        private val ivBoardImage = layout.boardImage

        fun bind(task: Task) {
            tvTitle.text = task.title
            tvCreatedBy.text = task.createdByName
            tvDate.text = task.createdAt
            tvBoardName.text = task.boardName

            Glide
                .with(context)
                .load(task.boardImage)
                .centerCrop()
                .placeholder(R.drawable.task_alt_24px)
                .into(ivBoardImage)
        }
    }
}

fun interface OnClickListener {
    fun onClick(task: Task)
}