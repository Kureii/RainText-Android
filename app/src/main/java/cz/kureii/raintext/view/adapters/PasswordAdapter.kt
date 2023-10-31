package cz.kureii.raintext.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.kureii.raintext.R
import cz.kureii.raintext.model.PasswordItem
import cz.kureii.raintext.view.PasswordViewHolder
import cz.kureii.raintext.viewmodel.PasswordViewModel
import cz.kureii.raintext.utils.ClipboardUtility
import java.util.Collections

class PasswordAdapter (
    private val items: MutableList<PasswordItem>,
    private val viewModel: PasswordViewModel,
    private val onDeleteClick: (PasswordItem) -> Unit,
    private val onEditClick: (PasswordItem) -> Unit,
    private val context: Context,
    ) : RecyclerView.Adapter<PasswordViewHolder>() {
    private val clipboardUtility = ClipboardUtility(context)
    private var currentClipboardTime: Long = R.integer.clipboardTime.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_password, parent, false)
        return PasswordViewHolder(view, clipboardUtility)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        val item = items[position]
        Log.d("PasswordAdapter","onBindViewHolder currentClipboardTime: $currentClipboardTime")
        holder.bind(item, onDeleteClick, onEditClick, currentClipboardTime)
    }


    fun updateClipboardTime(newValue: Long) {
        Log.d("updateClipboardTime","onBindViewHolder currentClipboardTime: $currentClipboardTime")
        currentClipboardTime = newValue
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int) {
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