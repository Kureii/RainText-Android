package cz.kureii.raintext.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.kureii.raintext.R
import cz.kureii.raintext.model.PasswordItem
import cz.kureii.raintext.utils.DividerItemDecoration
import cz.kureii.raintext.viewmodel.PasswordViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: PasswordViewModel
    private val passwordItems = mutableListOf<PasswordItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(PasswordViewModel::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.passwordsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = SimplePasswordAdapter(passwordItems)
        recyclerView.adapter = adapter


        viewModel.getPasswords().observe(this) { newItems ->
            passwordItems.clear()
            passwordItems.addAll(newItems)
            adapter.notifyDataSetChanged()
        }

        val dividerItemDecoration = DividerItemDecoration(this, R.dimen.divider_height)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    inner class SimplePasswordAdapter(private val items: List<PasswordItem>) : RecyclerView.Adapter<SimplePasswordViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimplePasswordViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_password, parent, false)
            return SimplePasswordViewHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: SimplePasswordViewHolder, position: Int) {
            val item = items[position]
            holder.bind(item)
        }
    }

    inner class SimplePasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: PasswordItem) {
            // Zde můžeš například nastavit hodnoty pro jednotlivé TextViews v password_item.xml
            val titleTextView = itemView.findViewById<TextView>(R.id.titleTextView)
            val usernameTextView = itemView.findViewById<TextView>(R.id.usernameTextView)
            val passwordTextView = itemView.findViewById<TextView>(R.id.passwordTextView)

            titleTextView.text = item.title
            usernameTextView.text = item.username
            passwordTextView.text = item.password // Možná budeš chtít zobrazit pouze tečky místo skutečného hesla
        }
    }

    /*private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.sampleText.text = stringFromJNI()
    }

    /**
     * A native method that is implemented by the 'raintext' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'raintext' library on application startup.
        init {
            System.loadLibrary("raintext")
        }
    }*/
}