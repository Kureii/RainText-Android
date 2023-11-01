package cz.kureii.raintext.view.activities

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import cz.kureii.raintext.R
import cz.kureii.raintext.model.PasswordItem
import cz.kureii.raintext.services.SavePasswordWorker
import cz.kureii.raintext.utils.DividerItemDecoration
import cz.kureii.raintext.view.adapters.DragManageAdapter
import cz.kureii.raintext.view.adapters.PasswordAdapter
import cz.kureii.raintext.view.fragments.AddPasswordDialogFragment
import cz.kureii.raintext.view.fragments.BottomSheetSettingsFragment
import cz.kureii.raintext.view.fragments.EditPasswordDialogFragment
import cz.kureii.raintext.viewmodel.PasswordViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: PasswordViewModel
    private val passwordItems = mutableListOf<PasswordItem>()
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {finish()}
    private val sharedPref: SharedPreferences by lazy { getSharedPreferences("SAFETY", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        viewModel = ViewModelProvider(this)[PasswordViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.passwordsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val addButton = findViewById<FloatingActionButton>(R.id.addButton)
        addButton.setOnClickListener {
            val dialog = AddPasswordDialogFragment(viewModel, this.getString(R.string.add_item))
            dialog.show(supportFragmentManager, "AddPasswordDialog")
        }
        val adapter = PasswordAdapter(passwordItems,
            viewModel,
            onDeleteClick = { selectedItem ->
                showDeleteConfirmationDialog(selectedItem)
            },
            onEditClick = { selectedItem ->
                val editDialog = EditPasswordDialogFragment(selectedItem, viewModel, this.getString(R.string.edit_item))
                editDialog.show(supportFragmentManager, "EditPasswordDialog")
            }, this)

        recyclerView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this, R.dimen.divider_height)
        recyclerView.addItemDecoration(dividerItemDecoration)

        viewModel.getPasswords().observe(this) { newItems ->
            passwordItems.clear()
            passwordItems.addAll(newItems)
            adapter.notifyDataSetChanged()
        }

        viewModel.clipboardTime.observe(this, Observer { newValue ->
            adapter.updateClipboardTime(newValue)
        })


        val callback = DragManageAdapter(adapter, ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(recyclerView)



        val bottomSheetHeaderLayout: LinearLayout = findViewById(R.id.bottomSheetSettingsMainActivityLayout)

        bottomSheetHeaderLayout.setOnTouchListener { v, event ->
            val bottomSheetFragment = BottomSheetSettingsFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
            bottomSheetHeaderLayout.performClick()
        }

    }

    override fun onStart() {
        super.onStart()
        handler.removeCallbacks(runnable)
    }

    override fun onStop() {
        super.onStop()

        val passwordData = PasswordItem.passwordItemListToData(passwordItems)

        val savePasswordWorkRequest = OneTimeWorkRequest.Builder(SavePasswordWorker::class.java)
            .setInputData(passwordData)
            .build()

        WorkManager.getInstance(this).enqueue(savePasswordWorkRequest)
        val delayMillis = sharedPref.getInt("Turn_Off_Time", R.integer.turnOffTime) * 1000L
        handler.postDelayed(runnable, delayMillis)
    }


    private fun showDeleteConfirmationDialog(item: PasswordItem) {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle(getString(R.string.warning))
            .setMessage(getString(R.string.warning_delete_password) + " (\"" + item.title + "\")")
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                val infoString = getString(R.string.item) + " \"" + item.title + "\" " + getString(R.string.was_deleted)
                viewModel.deletePassword(item)
                Toast.makeText(this, infoString, Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()

    }
}