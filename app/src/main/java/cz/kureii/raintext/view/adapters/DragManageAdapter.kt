package cz.kureii.raintext.view.adapters

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragManageAdapter(adapter: PasswordAdapter, dragDirs: Int, swipeDirs: Int)
    : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    private val passwordAdapter = adapter
    private var targetPosition = -1
    private var fromPosition = -1

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            if (viewHolder != null) {
                fromPosition = viewHolder.adapterPosition
            }
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val localFromPosition = viewHolder.adapterPosition
        targetPosition = target.adapterPosition

        if (targetPosition == passwordAdapter.itemCount - 1) {
            return false
        }
        (recyclerView.adapter as? PasswordAdapter)?.moveItem(localFromPosition, targetPosition)
        return true
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        super.clearView(recyclerView, viewHolder)
        if (targetPosition != -1) {
            if (fromPosition != targetPosition) {
                passwordAdapter.finalizeChanges(fromPosition, targetPosition)
            }
            targetPosition = -1
            fromPosition = -1
        }

    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
}
