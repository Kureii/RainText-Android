package cz.kureii.raintext.view

import android.os.Bundle
import android.view.View
import cz.kureii.raintext.databinding.DialogPasswordBinding
import cz.kureii.raintext.model.PasswordItem
import cz.kureii.raintext.viewmodel.PasswordViewModel

class EditPasswordDialogFragment (private val item: PasswordItem, private val viewModel: PasswordViewModel) :BasePasswordDialogFragment(item) {

    private lateinit var localBinding: DialogPasswordBinding

    override fun onViewsCreated(view: View, savedInstanceState: Bundle?) {
        localBinding = DialogPasswordBinding.bind(view)

        val saveButton = localBinding.saveButton
        saveButton.setOnClickListener {
                viewModel.editPassword(
                    localBinding.headlineInputEditText.text.toString(),
                    localBinding.usernameInputEditText.text.toString(),
                    localBinding.passwordInputEditText.text.toString(),
                    item.id)
                dismiss()

        }

    }
}