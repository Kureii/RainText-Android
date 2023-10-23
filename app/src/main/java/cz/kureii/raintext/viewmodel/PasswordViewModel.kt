package cz.kureii.raintext.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.kureii.raintext.model.PasswordItem

class PasswordViewModel : ViewModel() {
    private val passwords = MutableLiveData<List<PasswordItem>>()

    init {
        passwords.value = listOf(
            PasswordItem(1, "Nadpis 1", "Uživatel1", "Heslo123"),
            PasswordItem(2, "Nadpis 2", "Uživatel2", "Heslo456"),
            // další pevné hodnoty...
        )
    }

    fun getPasswords(): LiveData<List<PasswordItem>> {
        return passwords
    }
}
