package cz.kureii.raintext.view.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import cz.kureii.raintext.R
import cz.kureii.raintext.databinding.LoadingFragmentBinding
import cz.kureii.raintext.model.EncryptionManager
import cz.kureii.raintext.view.activities.MainActivity
import cz.kureii.raintext.viewmodel.PasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoadingFragment : Fragment() {
    private val viewModel: PasswordViewModel by activityViewModels()
    @Inject lateinit var encryptionManager: EncryptionManager


    private var _binding: LoadingFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoadingFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.decryptProgress.observe(viewLifecycleOwner) { progress ->
            binding.loadBar.progress = progress
            binding.loadBarText.text = getString(R.string.decrypting, progress)
        }

        viewModel.onDecryptionComplete.observe(viewLifecycleOwner) { isComplete ->
            if(isComplete) {
                finishLoading()
            }
        }
        viewModel.decryptPasswords()

    }
    private fun finishLoading() {
        if (activity is MainActivity) {
            (activity as MainActivity).onDataLoaded()
        }
        parentFragmentManager.beginTransaction().remove(this).commit()
        Log.i("LoadingFragment", "ended")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}