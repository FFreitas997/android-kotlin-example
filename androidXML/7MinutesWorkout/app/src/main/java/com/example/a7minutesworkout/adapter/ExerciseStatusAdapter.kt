package com.example.a7minutesworkout.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minutesworkout.R
import com.example.a7minutesworkout.databinding.ItemExerciseStatusBinding
import com.example.a7minutesworkout.model.Exercise
import com.example.a7minutesworkout.model.ExerciseStatus
import java.util.Locale

class ExerciseStatusAdapter(private val exercises: List<Exercise>): RecyclerView.Adapter<ExerciseStatusAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemExerciseStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(exercises[position])
    }

    override fun getItemCount() = exercises.size


    class ViewHolder(binding: ItemExerciseStatusBinding): RecyclerView.ViewHolder(binding.root) {

        private val tvExerciseStatus: TextView = binding.tvItemExerciseStatus

        fun onBind(exercise: Exercise) {
            tvExerciseStatus.text = String.format(Locale.getDefault(), "%02d", exercise.id)
            when(exercise.status){
                ExerciseStatus.COMPLETED -> {
                    tvExerciseStatus.background = ContextCompat
                        .getDrawable(this.itemView.context, R.drawable.item_circular_color_accent_background)
                    tvExerciseStatus.setTextColor(Color.parseColor("#FFFFFF"))
                }
                ExerciseStatus.IN_PROGRESS -> {
                    tvExerciseStatus.background = ContextCompat
                        .getDrawable(this.itemView.context, R.drawable.item_circular_thin_color_accent_border)
                    tvExerciseStatus.setTextColor(Color.parseColor("#212121"))
                }
                ExerciseStatus.NOT_STARTED -> {
                    tvExerciseStatus.background = ContextCompat
                        .getDrawable(this.itemView.context, R.drawable.item_circular_color_grey_background)
                    tvExerciseStatus.setTextColor(Color.parseColor("#212121"))
                }
            }
        }
    }
}