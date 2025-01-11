package com.ffreitas.flowify.ui.main.components.board.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ffreitas.flowify.R
import com.ffreitas.flowify.data.models.Board
import com.ffreitas.flowify.databinding.BoardItemBinding

class BoardsAdapter(private val context: Context, private val records: List<Board>) :
    RecyclerView.Adapter<BoardsAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = BoardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(records[position])
        val handler = onClickListener ?: return
        holder.itemView.setOnClickListener { handler.onClick(position, records[position]) }
    }

    fun setOnClickListener(listener: OnClickListener) {
        onClickListener = listener
    }

    override fun getItemCount(): Int = records.size

    class ViewHolder(val context: Context, layout: BoardItemBinding) :
        RecyclerView.ViewHolder(layout.root) {

        private val name = layout.boardName
        private val picture = layout.boardImage
        private val createdAt = layout.boardDate

        fun bind(board: Board) {
            name.text = board.name
            createdAt.text = board.createdAt

            Glide
                .with(context)
                .load(board.picture)
                .centerCrop()
                .placeholder(R.drawable.default_board)
                .into(picture)
        }
    }
}

fun interface OnClickListener {
    fun onClick(position: Int, board: Board)
}