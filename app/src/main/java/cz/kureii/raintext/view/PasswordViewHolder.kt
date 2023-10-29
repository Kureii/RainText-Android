package cz.kureii.raintext.view

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.kureii.raintext.R
import cz.kureii.raintext.model.PasswordItem

class PasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteIconButton)
    private val passwordTextView: TextView = itemView.findViewById(R.id.passwordTextView)
    private val showPasswordIcon: ImageButton = itemView.findViewById(R.id.showPasswordIcon)
    private val hidePasswordIcon: ImageButton = itemView.findViewById(R.id.hidePasswordIcon)

    private var isPasswordVisible = false

    fun bind(item: PasswordItem, onDeleteClick: (PasswordItem) -> Unit, onEditClick: (PasswordItem) ->Unit) {
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
        editButton.setOnClickListener{
            onEditClick(item)
        }
    }
}
