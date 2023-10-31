package cz.kureii.raintext.view

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cz.kureii.raintext.R
import cz.kureii.raintext.model.PasswordItem
import cz.kureii.raintext.utils.ClipboardUtility

class PasswordViewHolder(itemView: View, private val clipboardUtility: ClipboardUtility) :
    RecyclerView.ViewHolder(itemView) {
    private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteIconButton)
    private val passwordTextView: TextView = itemView.findViewById(R.id.passwordTextView)
    private val showPasswordIcon: ImageButton = itemView.findViewById(R.id.showPasswordIcon)
    private val hidePasswordIcon: ImageButton = itemView.findViewById(R.id.hidePasswordIcon)

    private var isPasswordVisible = false

    fun bind(
        item: PasswordItem,
        onDeleteClick: (PasswordItem) -> Unit,
        onEditClick: (PasswordItem) -> Unit,
        clipboardTime: Long
    ) {
        val titleTextView = itemView.findViewById<TextView>(R.id.titleTextView)
        val usernameTextView = itemView.findViewById<TextView>(R.id.usernameTextView)

        titleTextView.text = item.title
        usernameTextView.text = item.username

        passwordTextView.text = "•".repeat(16)

        showPasswordIcon.setOnClickListener {
            passwordTextView.text = item.password
            isPasswordVisible = true
            showPasswordIcon.visibility = View.GONE
            hidePasswordIcon.visibility = View.VISIBLE
        }

        hidePasswordIcon.setOnClickListener {
            passwordTextView.text = "•".repeat(16)
            isPasswordVisible = false
            hidePasswordIcon.visibility = View.GONE
            showPasswordIcon.visibility = View.VISIBLE
        }

        deleteButton.setOnClickListener {
            onDeleteClick(item)
        }

        val editButton: ImageButton = itemView.findViewById(R.id.editIcon)
        editButton.setOnClickListener {
            onEditClick(item)
        }

        passwordTextView.setOnLongClickListener {
            Log.i("PasswordViewHolder", "Clipboard stored value: $clipboardTime")
            clipboardUtility.copyPassword(item.password, clipboardTime)
            Toast.makeText(it.context, itemView.context.getString(R.string.password_copied), Toast.LENGTH_SHORT).show()
            true
        }

        usernameTextView.setOnLongClickListener {
            Log.i("PasswordViewHolder", "Clipboard stored value: $clipboardTime")
            clipboardUtility.copyUsername(item.username, clipboardTime)
            Toast.makeText(it.context, itemView.context.getString(R.string.username_copied), Toast.LENGTH_SHORT).show()
            true
        }

    }


}
