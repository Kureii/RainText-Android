package cz.kureii.raintext.view.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cz.kureii.raintext.R
import cz.kureii.raintext.databinding.BottomSheetSettingsBinding
import cz.kureii.raintext.model.ThemeType

class BottomSheetSettingsFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetSettingsBinding.inflate(inflater, container, false)


        // Theme spinner
        fillSpinner(
            spinner = binding.themeOption,
            options = ThemeType.values().map { getString(it.displayNameResId) },
            sharedPrefKey = "Main",
            sharedPrefName = "THEME",
            defaultPrefValue = 0,
            customDropDownLayout = R.layout.custom_spinner_dropdown_item,
            setSelection = { themeStoredValue, spinner, _ -> spinner.setSelection(themeStoredValue)},
            onItemSelectedAction = { position, _ ->
                when (position) {
                    0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Light
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // Dark
                }
                return@fillSpinner -1
            }
        )

        // Clipboard time spinner
        fillSpinner(
            spinner = binding.clipboardTime,
            options = resources.getStringArray(R.array.clipboard_time).toList(),
            sharedPrefKey = "Clipboard_Time",
            sharedPrefName = "SAFETY",
            defaultPrefValue = R.integer.clipboardTime,
            customDropDownLayout = R.layout.custom_spinner_dropdown_item,
            setSelection = {clipboardTimeStoredValue, spinner, adapter ->
                spinner.setSelection(clipboardTimeStoredValue)
                val clipboardTimePosition = adapter.getPosition("${clipboardTimeStoredValue}s")
                spinner.setSelection(clipboardTimePosition)
                           },
            onItemSelectedAction = { _, spinner ->
                val selectedValueString = spinner.selectedItem.toString()
                return@fillSpinner selectedValueString.removeSuffix("s").toInt()
            }
        )

        // Turn-off time
        fillSpinner(
            spinner = binding.turnOffTime,
            options = resources.getStringArray(R.array.turn_off_time).toList(),
            sharedPrefKey = "Turn_Off_Time",
            sharedPrefName = "SAFETY",
            defaultPrefValue = R.integer.turnOffTime,
            customDropDownLayout = R.layout.custom_spinner_dropdown_item,
            setSelection = {turnOffTimeStoredValue, spinner, adapter ->
                spinner.setSelection(turnOffTimeStoredValue)
                val turnOffTimePosition = adapter.getPosition("${turnOffTimeStoredValue}s")
                spinner.setSelection(turnOffTimePosition)
            },
            onItemSelectedAction = { _, spinner ->
                val selectedValueString = spinner.selectedItem.toString()
                return@fillSpinner selectedValueString.removeSuffix("s").toInt()
            }
        )

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
    fun <T> fillSpinner(
        spinner: Spinner,
        options: List<T>,
        sharedPrefKey: String,
        sharedPrefName: String,
        defaultPrefValue: Int,
        customDropDownLayout: Int? = null,
        setSelection: (Int, Spinner, ArrayAdapter<T>) -> Unit,
        onItemSelectedAction: (Int, Spinner) -> Int
    ) {
        val context = spinner.context

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
        customDropDownLayout?.let { adapter.setDropDownViewResource(it) }
        spinner.adapter = adapter

        val sharedPref = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        val storedValue = sharedPref.getInt(sharedPrefKey, defaultPrefValue)
        setSelection(storedValue, spinner, adapter)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newValue: Int = onItemSelectedAction(position, spinner)

                Log.i("fillSpinner", "newValue: $newValue")

                if (newValue == -1) {
                    with(sharedPref.edit()) {
                        putInt(sharedPrefKey, position)
                        apply()
                    }
                } else {
                    Log.i("fillSpinner", "save NewValue: $newValue")
                    with(sharedPref.edit()) {
                        putInt(sharedPrefKey, newValue)
                        apply()
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


}
