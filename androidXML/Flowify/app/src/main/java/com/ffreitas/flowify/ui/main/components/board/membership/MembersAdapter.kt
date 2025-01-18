package com.ffreitas.flowify.ui.main.components.board.membership

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ffreitas.flowify.R
import com.ffreitas.flowify.data.models.User
import com.ffreitas.flowify.databinding.MembershipItemBinding

class MembersAdapter(private val context: Context, private val members: MutableList<User>) :
    RecyclerView.Adapter<MembersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = MembershipItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]
        holder.bind(member)
    }

    override fun getItemCount(): Int = members.size

    fun removeAt(position: Int): User {
        val item = members[position]
        members.removeAt(position)
        notifyItemRemoved(position)
        return item
    }

    inner class ViewHolder(val context: Context, layout: MembershipItemBinding) :
        RecyclerView.ViewHolder(layout.root) {

        private val ivProfile = layout.personPicture
        private val tvName = layout.personName
        private val tvEmail = layout.personEmail

        fun bind(member: User) {
            tvName.text = member.name
            tvEmail.text = member.email

            Glide
                .with(context)
                .load(member.picture)
                .centerCrop()
                .placeholder(R.drawable.person)
                .into(ivProfile)
        }
    }
}