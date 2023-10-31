package cz.kureii.raintext.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cz.kureii.raintext.model.PasswordItem
import cz.kureii.raintext.model.PasswordDatabaseHelper

class PasswordViewModel(application: Application) : AndroidViewModel(application) {
    private val _passwords = MutableLiveData<MutableList<PasswordItem>>()
    private val dbHelper = PasswordDatabaseHelper(application)
    private val sharedPref: SharedPreferences = application.getSharedPreferences("SAFETY", Context.MODE_PRIVATE)
    private val defaultClipboardTime = cz.kureii.raintext.R.integer.clipboardTime

    val clipboardTime: MutableLiveData<Long> = MutableLiveData()

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "Clipboard_Time") {
            updateClipboardTime()
        }
    }
    private fun updateClipboardTime() {
        val storedValue = sharedPref.getInt("Clipboard_Time", defaultClipboardTime).toLong()
        clipboardTime.value = storedValue
    }

    init {
        loadPasswordsFromDatabase()
        val storedValue = sharedPref.getInt("Clipboard_Time", defaultClipboardTime).toLong()
        clipboardTime.value = storedValue
        sharedPref.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

    }

    override fun onCleared() {
        super.onCleared()
        sharedPref.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }


    private fun loadPasswordsFromDatabase() {
        val passwords = dbHelper.getAllPasswords()
        _passwords.value = passwords
    }

    fun getPasswords(): MutableLiveData<MutableList<PasswordItem>> {
        return this._passwords
    }


    fun addPassword(title: String, username: String, password: String) {
        val newItem = PasswordItem(_passwords.value?.size ?: 0, title, username, password)
        _passwords.value?.add(newItem)
        _passwords.postValue(_passwords.value)
    }

    fun editPassword(title: String, username: String, password: String, id: Int) {
        val currentList = _passwords.value

        val index = currentList?.indexOfFirst { it.id == id }

        if (index != null && index != -1) {
            val updatedItem = currentList[index].copy(
                title = title,
                username = username,
                password = password
            )
            currentList[index] = updatedItem
            _passwords.postValue(currentList)
        } else {
            Log.e("PasswordViewModel", "Item with ID not found $id")
        }
    }

    fun deletePassword(item: PasswordItem) {
        _passwords.value?.remove(item)
        _passwords.postValue(_passwords.value)
    }

    fun moveItem(from: Int, to: Int) {
        val currentList = _passwords.value ?: return

        if (from in currentList.indices && to in currentList.indices) {
            val fromItem = currentList[from]
            currentList.removeAt(from)
            currentList.add(to, fromItem)
            refreshIds()
            _passwords.postValue(currentList)
        } else {
            Log.e("PasswordViewModel", "Invalid indices for moving: from $from, to $to")
        }
    }


    private fun refreshIds() {
        _passwords.value?.forEachIndexed { index, passwordItem ->
            passwordItem.id = index
        }
        _passwords.postValue(_passwords.value)
    }
}

