package cz.kureii.raintext.view.adapters

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.kureii.raintext.R
import cz.kureii.raintext.model.PasswordItem
import cz.kureii.raintext.view.viewholders.PasswordViewHolder
import cz.kureii.raintext.viewmodel.PasswordViewModel
import cz.kureii.raintext.utils.ClipboardUtility
import cz.kureii.raintext.view.viewholders.BasePasswordViewHolder
import java.util.Collections

class PasswordAdapter (
    private val items: MutableList<PasswordItem>,
    private val viewModel: PasswordViewModel,
    private val onDeleteClick: (PasswordItem) -> Unit,
    private val onEditClick: (PasswordItem) -> Unit,
    private val context: Context,
    ) : RecyclerView.Adapter<BasePasswordViewHolder>() {
    private val clipboardUtility = ClipboardUtility(context)
    private var currentClipboardTime: Long = R.integer.clipboardTime.toLong()

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_SPACE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePasswordViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_password, parent, false)
                PasswordViewHolder(view, clipboardUtility)
            }
            VIEW_TYPE_SPACE -> {
                val spaceView = View(parent.context)
                spaceView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 200f, context.resources.displayMetrics).toInt())
                BasePasswordViewHolder(spaceView)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun getItemCount(): Int = items.size + 1

    override fun onBindViewHolder(holder: BasePasswordViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            val item = items[position]
            (holder as PasswordViewHolder).bind(item, onDeleteClick, onEditClick, currentClipboardTime)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == items.size) {
            VIEW_TYPE_SPACE
        } else {
            VIEW_TYPE_ITEM
        }
    }



    fun updateClipboardTime(newValue: Long) {
        Log.d("updateClipboardTime","onBindViewHolder currentClipboardTime: $currentClipboardTime")
        currentClipboardTime = newValue
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int) {
        if (to == items.size - 1) {
            return
        }
        if (from < to) {
            for (i in from until to) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(from, to)
    }

    fun finalizeChanges(from: Int, to: Int) {
        viewModel.moveItem(from, to)
    }



}