package cz.kureii.raintext.view.activities

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        viewModel = ViewModelProvider(this)[PasswordViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.passwordsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val addButton = findViewById<FloatingActionButton>(R.id.addButton)
        addButton.setOnClickListener {
            val dialog = AddPasswordDialogFragment(viewModel)
            dialog.show(supportFragmentManager, "AddPasswordDialog")
        }
        val adapter = PasswordAdapter(passwordItems,
            viewModel,
            onDeleteClick = { selectedItem ->
                showDeleteConfirmationDialog(selectedItem)
            },
            onEditClick = { selectedItem ->
                val editDialog = EditPasswordDialogFragment(selectedItem, viewModel)
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

        val callback = DragManageAdapter(adapter, ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(recyclerView)



        val bottomSheetHeaderLayout: LinearLayout = findViewById(R.id.bottomSheetSettingsMainActivityLayout)

        bottomSheetHeaderLayout.setOnTouchListener { v, event ->
            Log.i("MainActivity", "tap or drag")
            val bottomSheetFragment = BottomSheetSettingsFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
            bottomSheetHeaderLayout.performClick()
        }

    }

    override fun onStop() {
        super.onStop()

        val passwordData = PasswordItem.passwordItemListToData(passwordItems)

        val savePasswordWorkRequest = OneTimeWorkRequest.Builder(SavePasswordWorker::class.java)
            .setInputData(passwordData)
            .build()

        WorkManager.getInstance(this).enqueue(savePasswordWorkRequest)
    }


    private fun showDeleteConfirmationDialog(item: PasswordItem) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.warning))
            .setMessage(getString(R.string.warning_delete_password))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deletePassword(item)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
}