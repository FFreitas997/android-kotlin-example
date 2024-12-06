package com.example.happyplaces.adapter.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.R

abstract class SwipeToEditCallback(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private val icon = ContextCompat.getDrawable(context, R.drawable.sharp_edit_24)
    private val inWidth = icon?.intrinsicWidth
    private val inHeight = icon?.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColor = ContextCompat.getColor(context, R.color.md_theme_inverseSurface)
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        /**
         * To disable swipe for a particular item, return 0 from getMovementFlags() method.
         * For example, if you want to disable swipe for the 10th item in the list, you can do it like this:
         * if (viewHolder.adapterPosition == 10) return 0
         */

        if (viewHolder.bindingAdapterPosition == 10) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        icon ?: return
        inWidth ?: return
        inHeight ?: return

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.left.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false)
            return
        }

        // Draw the red delete background
        background.color = backgroundColor
        background.setBounds(
            itemView.left + dX.toInt(),
            itemView.top,
            itemView.left,
            itemView.bottom
        )
        background.draw(c)

        // Calculate position of delete icon
        val iconTop = itemView.top + (itemHeight - inHeight) / 2
        val iconMargin = (itemHeight - inHeight) / 2
        val iconLeft = itemView.left + iconMargin - inWidth
        val iconRight = itemView.left + iconMargin
        val iconBottom = iconTop + inHeight

        // Draw the delete icon
        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        icon.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwiped(viewHolder.bindingAdapterPosition)
    }

    abstract fun onSwiped(position: Int)
}