package cz.kureii.raintext.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.kureii.raintext.R
import cz.kureii.raintext.model.PasswordItem

class PasswordAdapter (
    private val items: List<PasswordItem>,
    private val onDeleteClick: (PasswordItem) -> Unit,
    private val onEditClick: (PasswordItem) -> Unit
    ) : RecyclerView.Adapter<PasswordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_password, parent, false)
        return PasswordViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onDeleteClick, onEditClick)
    }

    /*override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        // Pokud chceš mazat položky přesunutím, můžeš zde implementovat mazání.
        // Pro nynější účely je tato metoda prázdná.
    }*/

}