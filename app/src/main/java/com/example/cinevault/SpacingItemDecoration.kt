package com.example.cinevault

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(
    private val spacing: Int,
    private val isHorizontal: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        if (isHorizontal) {
            outRect.left = if (position == 0) spacing else spacing / 2
            outRect.right = spacing / 2
        } else {
            outRect.top = if (position == 0) spacing else spacing / 2
            outRect.bottom = spacing / 2
        }
    }
}