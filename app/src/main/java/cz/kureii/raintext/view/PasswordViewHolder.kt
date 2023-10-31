package cz.kureii.raintext.view

import android.content.Context
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
        onEditClick: (PasswordItem) -> Unit
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
        val context = itemView.context
        val sharedPref = context.getSharedPreferences("SAFETY", Context.MODE_PRIVATE)
        val defaultValue = 30
        val storedValue = sharedPref.getInt("Clipboard_Time", defaultValue).toLong()


        passwordTextView.setOnLongClickListener {
            clipboardUtility.copyPassword(item.password, storedValue)
            Toast.makeText(it.context, "Heslo zkopírováno", Toast.LENGTH_SHORT).show()
            true
        }

        usernameTextView.setOnLongClickListener {
            clipboardUtility.copyUsername(item.username, storedValue)
            Toast.makeText(it.context, "Uživatelské jméno zkopírováno", Toast.LENGTH_SHORT).show()
            true
        }

    }
}
