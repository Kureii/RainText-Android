package cz.kureii.raintext.view.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cz.kureii.raintext.R
import cz.kureii.raintext.databinding.BottomSheetSettingsBinding

class BottomSheetSettingsFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetSettingsBinding.inflate(inflater, container, false)

        val clipboardTimeSpinner = binding.clipboardTime
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.clipboard_time,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        clipboardTimeSpinner.adapter = adapter

        val sharedPref = requireActivity().getSharedPreferences("SAFETY", Context.MODE_PRIVATE)
        val defaultValue = 30
        val storedValue = sharedPref.getInt("Clipboard_Time", defaultValue)
        val position = adapter.getPosition("${storedValue}s")
        clipboardTimeSpinner.setSelection(position)

        clipboardTimeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedValueString = clipboardTimeSpinner.selectedItem.toString()
                val selectedValueInt = selectedValueString.removeSuffix("s").toInt()

                with(sharedPref.edit()) {
                    putInt("Clipboard_Time", selectedValueInt)
                    apply()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val turnOffTimeSpinner = binding.turnOffTime
        val turnOffAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.turn_off_time,
            android.R.layout.simple_spinner_item
        )
        turnOffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        turnOffTimeSpinner.adapter = turnOffAdapter

        val turnOffDefaultValue = 45
        val storedTurnOffValue = sharedPref.getInt("Turn_Off_Time", turnOffDefaultValue)
        val turnOffPosition = turnOffAdapter.getPosition("${storedTurnOffValue}s")
        turnOffTimeSpinner.setSelection(turnOffPosition)

        turnOffTimeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedValueString = turnOffTimeSpinner.selectedItem.toString()
                val selectedValueInt = selectedValueString.removeSuffix("s").toInt()

                with(sharedPref.edit()) {
                    putInt("Turn_Off_Time", selectedValueInt)
                    apply()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout

            val parent = bottomSheet?.parent as? CoordinatorLayout
            parent?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Fragment_absolute_background))
        }
        return dialog
    }
}
