package com.example.happyplaces.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.database.HappyPlaceEntity
import com.example.happyplaces.databinding.HappyPlaceItemBinding

class HappyPlacesAdapter(private val records: List<HappyPlaceEntity>) :
    RecyclerView.Adapter<HappyPlacesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = HappyPlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.bind(record)
    }

    override fun getItemCount() = records.size

    class ViewHolder(binding: HappyPlaceItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val imageView = binding.ivPlaceImage
        private val titleView = binding.tvTitle
        private val descriptionView = binding.tvDescription

        fun bind(record: HappyPlaceEntity) {
            imageView.setImageURI(Uri.parse(record.image))
            titleView.text = record.title
            descriptionView.text = record.description
        }
    }
}