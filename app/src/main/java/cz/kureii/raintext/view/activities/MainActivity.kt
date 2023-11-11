package cz.kureii.raintext.view.activities

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import cz.kureii.raintext.R
import cz.kureii.raintext.databinding.LoadingFragmentBinding
import cz.kureii.raintext.model.PasswordItem
import cz.kureii.raintext.services.SavePasswordWorker
import cz.kureii.raintext.utils.DividerItemDecoration
import cz.kureii.raintext.view.adapters.DragManageAdapter
import cz.kureii.raintext.view.adapters.PasswordAdapter
import cz.kureii.raintext.view.fragments.AddPasswordDialogFragment
import cz.kureii.raintext.view.fragments.BottomSheetSettingsFragment
import cz.kureii.raintext.view.fragments.EditPasswordDialogFragment
import cz.kureii.raintext.view.fragments.LoadingFragment
import cz.kureii.raintext.viewmodel.PasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: PasswordViewModel by viewModels()
    private val passwordItems = mutableListOf<PasswordItem>()
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {finish()}
    private val sharedPref: SharedPreferences by lazy { getSharedPreferences("SAFETY", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        if (viewModel.onDecryptionComplete.value == true) {
            onDataLoaded()
        } else {
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.loadingFragmentContainer, LoadingFragment())
                    .commit()
            }
        }
    }

    fun onDataLoaded() {
        val goGone = findViewById<ConstraintLayout>(R.id.onMainActivityGone)
        goGone.visibility = View.GONE
        if (viewModel.onDecryptionComplete.value == false) {
            supportFragmentManager.findFragmentById(R.id.loadingFragmentContainer)?.let { fragment ->
                supportFragmentManager.beginTransaction().remove(fragment).commit()
                Log.i("loadingFragmentContainer", "deleted")
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.passwordsRecyclerView)
        recyclerView.visibility = View.VISIBLE
        recyclerView.layoutManager = LinearLayoutManager(this)
        val addButton = findViewById<FloatingActionButton>(R.id.addButton)
        addButton.visibility = View.VISIBLE
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
        bottomSheetHeaderLayout.visibility = View.VISIBLE

        bottomSheetHeaderLayout.setOnTouchListener { _, _ ->
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

        val savePasswordWorkRequest = OneTimeWorkRequestBuilder<SavePasswordWorker>()
            .setInputData(passwordData)
            .addTag("saveDataTag") // Přidá tag pro pozdější identifikaci requestu
            .build()

        WorkManager.getInstance(this)
            .beginUniqueWork("saveDataTag", ExistingWorkPolicy.KEEP, savePasswordWorkRequest)
            .enqueue()

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