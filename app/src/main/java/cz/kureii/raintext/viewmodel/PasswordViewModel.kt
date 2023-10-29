package cz.kureii.raintext.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.kureii.raintext.model.PasswordItem

class PasswordViewModel : ViewModel() {
    private val _passwords = MutableLiveData<List<PasswordItem>>()
    init {
        _passwords.value = listOf(
            PasswordItem(0, "Nadpis 1", "Uživatel1", "Heslo123"),
            PasswordItem(1, "Nadpis 2", "Uživatel2", "Heslo456"),
            PasswordItem(2, "Nadpis 3", "Uživatel3", "Heslo789"),
            // další pevné hodnoty...
        )

    }



    fun getPasswords(): LiveData<List<PasswordItem>> {
        return _passwords
    }

    fun addPassword(title: String, username: String, password: String) {
        val newId = _passwords.value?.size ?: 0
        Log.d("android", newId.toString())
        Log.d("android", title)
        Log.d("android", username)
        Log.d("android", password)
        val newItem = PasswordItem(newId, title, username, password)
        val currentList = _passwords.value ?: emptyList()
        _passwords.value = currentList + newItem
    }

    fun editPassword(title: String, username: String, password: String, id: Int) {
        val currentList = _passwords.value?.toMutableList() ?: mutableListOf()

        val index = currentList.indexOfFirst { it.id == id }

        if (index != -1) {
            val updatedItem = currentList[index].copy(
                title = title,
                username = username,
                password = password
            )
            currentList[index] = updatedItem
            _passwords.value = currentList
        } else {
            Log.e("PasswordViewModel", "Nenalezena položka s ID $id")
        }
    }

    fun deletePassword(item: PasswordItem) {
        val currentList = _passwords.value?.toMutableList()
        currentList?.remove(item)
        _passwords.value = currentList
    }


}
