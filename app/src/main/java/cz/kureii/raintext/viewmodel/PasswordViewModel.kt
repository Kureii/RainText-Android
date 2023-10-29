package cz.kureii.raintext.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.kureii.raintext.model.PasswordItem

class PasswordViewModel : ViewModel() {
    private val _passwords = MutableLiveData<MutableList<PasswordItem>>()

    init {
        _passwords.value = mutableListOf(
            PasswordItem(0, "Nadpis 1", "Uživatel1", "Heslo123"),
            PasswordItem(1, "Nadpis 2", "Uživatel2", "Heslo456"),
            PasswordItem(2, "Nadpis 3", "Uživatel3", "Heslo789")
            // další pevné hodnoty...
        )
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
            Log.e("PasswordViewModel", "Nenalezena položka s ID $id")
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
            Log.e("PasswordViewModel", "Neplatné indexy pro přesunutí: from $from, to $to")
        }
    }


    private fun refreshIds() {
        _passwords.value?.forEachIndexed { index, passwordItem ->
            passwordItem.id = index
        }
        _passwords.postValue(_passwords.value)
        _passwords.value?.forEachIndexed { index, passwordItem ->
            val id = passwordItem.id
            val headline = passwordItem.title
            val username = passwordItem.username
            val password = passwordItem.password
            Log.i("PasswordViewModel", " id: $id, headline: $headline, username: $username, password: $password")

        }
    }
}

