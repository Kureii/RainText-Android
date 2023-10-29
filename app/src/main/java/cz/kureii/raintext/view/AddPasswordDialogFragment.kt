package cz.kureii.raintext.view

import android.os.Bundle
import android.view.View
import cz.kureii.raintext.databinding.DialogPasswordBinding
import cz.kureii.raintext.viewmodel.PasswordViewModel

class AddPasswordDialogFragment(private val viewModel: PasswordViewModel) :BasePasswordDialogFragment() {

    private lateinit var localBinding: DialogPasswordBinding

    override fun onViewsCreated(view: View, savedInstanceState: Bundle?) {
        localBinding = DialogPasswordBinding.bind(view)

        val saveButton = localBinding.saveButton
        saveButton.setOnClickListener {
            viewModel.addPassword(
                localBinding.headlineInputEditText.text.toString(),
                localBinding.usernameInputEditText.text.toString(),
                localBinding.passwordInputEditText.text.toString())
            dismiss()
        }

    }
}