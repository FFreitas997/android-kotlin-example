package com.example.happyplaces.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.activities.CreateHappyPlaceActivity
import com.example.happyplaces.database.HappyPlaceEntity
import com.example.happyplaces.databinding.HappyPlaceItemBinding
import com.example.happyplaces.utils.Constants

class HappyPlacesAdapter(private val records: MutableList<HappyPlaceEntity>) : RecyclerView.Adapter<HappyPlacesAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = HappyPlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.bind(record)
        val handler = onItemClickListener ?: return
        holder.itemView.setOnClickListener { handler.onClick(position) }
    }

    override fun getItemCount() = records.size

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val record = records[position]
        Intent(activity, CreateHappyPlaceActivity::class.java)
            .apply { putExtra(Constants.EXTRA_PLACE_EDIT, record.id) }
            .also { activity.startActivity(it) }
        notifyItemChanged(position)
    }

    fun removeAt(position: Int): HappyPlaceEntity {
        val item = records[position]
        records.removeAt(position)
        notifyItemRemoved(position)
        return item
    }

    class ViewHolder(binding: HappyPlaceItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val imageView = binding.ivPlaceImage
        private val titleView = binding.tvTitle
        private val descriptionView = binding.tvDescription

        fun bind(record: HappyPlaceEntity) {
            val bitmap = BitmapFactory
                .decodeByteArray(record.image, 0, record.image.size)

            imageView.setImageBitmap(bitmap)
            titleView.text = record.title
            descriptionView.text = record.description
        }
    }
}