package com.example.a7minutesworkout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minutesworkout.database.HistoryEntity
import com.example.a7minutesworkout.databinding.ItemHistoryBinding
import java.util.Locale

class HistoryAdapter(private val records: List<HistoryEntity>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position, records[position])
    }

    override fun getItemCount(): Int = records.size

    class ViewHolder(binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        private val tvIndex = binding.tvPosition
        private val tvDate = binding.tvItemDate
        private val tvDesc = binding.tvItemDescription

        fun onBind(position: Int, entity: HistoryEntity) {
            tvIndex.text = String.format(Locale.getDefault(), "%02d", position + 1)
            tvDate.text = entity.date
            tvDesc.text = entity.description
        }

    }
}