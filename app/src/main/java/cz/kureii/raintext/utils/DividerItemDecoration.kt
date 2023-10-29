package cz.kureii.raintext.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

class DividerItemDecoration(context: Context, @DimenRes dividerHeight: Int) : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable?
    private val mDividerHeight: Int

    init {
        val styledAttributes = context.obtainStyledAttributes(ATTRS)
        mDivider = styledAttributes.getDrawable(0)
        styledAttributes.recycle()

        mDividerHeight = context.resources.getDimension(dividerHeight).toInt()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + mDividerHeight

            mDivider?.setBounds(left, top, right, bottom)
            mDivider?.draw(c)
        }
    }

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }
}
