package cz.kureii.raintext.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

class DividerItemDecoration(context: Context, @DimenRes dividerHeight: Int) : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable?
    private val mDividerHeight: Int
    private val mOffset: Int

    init {
        val styledAttributes = context.obtainStyledAttributes(ATTRS)
        mDivider = styledAttributes.getDrawable(0)
        styledAttributes.recycle()

        mDividerHeight = context.resources.getDimension(dividerHeight).toInt()
        mOffset = dpToPx(context, 2).toInt()  // převedení 2dp na pixely
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount - 2) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin - mOffset
            val bottom = top + mDividerHeight

            mDivider?.setBounds(left, top, right, bottom)
            mDivider?.draw(c)
        }
    }

    private fun dpToPx(context: Context, dp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics)
    }

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }
}