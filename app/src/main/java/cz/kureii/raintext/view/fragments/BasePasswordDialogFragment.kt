package cz.kureii.raintext.view.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import cz.kureii.raintext.R
import cz.kureii.raintext.databinding.DialogPasswordBinding
import cz.kureii.raintext.model.PasswordItem
import cz.kureii.raintext.utils.PasswordGenerator

abstract class BasePasswordDialogFragment (
    private val dialogHeadline: String,
    private val passwordItem: PasswordItem? = null
) :DialogFragment() {

    private lateinit var binding: DialogPasswordBinding

    private fun expandView(view: View, duration: Long, sleep: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            view.visibility = View.VISIBLE
            view.scaleY = 0f
            view.alpha = 0f

            val anim = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleY", 0f, 1f),
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
            )
            anim.duration = duration
            anim.interpolator = LinearInterpolator()
            anim.start()
        }, sleep)
    }

    private fun collapseView(view: View, duration: Long, sleep: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            view.scaleY = 1f
            view.alpha = 1f

            val anim = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleY", 1f, 0f),
                PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
            )
            anim.duration = duration
            anim.interpolator = LinearInterpolator()
            anim.start()
            Handler(Looper.getMainLooper()).postDelayed({
                view.visibility = View.GONE
            }, duration)
        }, sleep)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_corners)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogPasswordBinding.inflate(inflater, container, false)

        binding.dialogPasswordHeadline.text = dialogHeadline

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                checkFieldsAndToggleSaveButton()
            }
        }

        binding.headlineInputEditText.addTextChangedListener(textWatcher)
        binding.usernameInputEditText.addTextChangedListener(textWatcher)
        binding.passwordInputEditText.addTextChangedListener(textWatcher)

        val passwordLayout = binding.passwordInputLayout

        if (passwordItem != null) {
            binding.headlineInputEditText.setText(passwordItem.title)
            binding.usernameInputEditText.setText(passwordItem.username)
            binding.passwordInputEditText.setText(passwordItem.password)
            checkPassword(binding.passwordInputEditText.text)
        }

        passwordLayout.setEndIconOnClickListener {
            val isPasswordVisible = binding.passwordInputEditText.inputType != (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
            if (isPasswordVisible) {
                passwordLayout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_show_password)
                binding.passwordInputEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                passwordLayout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_hide_password)
                binding.passwordInputEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
        }



        binding.passwordInputEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkPassword(s)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.passwordInputEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (binding.passwordGenerateOptions.visibility == View.VISIBLE){
                    collapseView(binding.passwordGenerateOptions, 50, 0)
                }
                if (binding.passwordProgressBarLayout.visibility != View.GONE) {
                    expandView(binding.passwordRequirementsTextView, 200, 50)
                    expandView(binding.passwordProgressBarSpace, 150, 200)
                } else {
                    expandView(binding.passwordProgressBarLayout, 50, 0)
                    expandView(binding.passwordRequirementsTextView, 200, 50)
                    expandView(binding.passwordProgressBarSpace, 150, 250)
                }
            } else {
                collapseView(binding.passwordRequirementsTextView, 100, 0)
                collapseView(binding.passwordProgressBarSpace, 50, 100)
            }
        }

        binding.generatePasswordNumberCharsSeekBar.min = 8
        binding.generatePasswordNumberCharsSeekBar.max = 64

        binding.generatePasswordNumberCharsEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(2))

        binding.generatePasswordNumberCharsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.generatePasswordNumberCharsEditText.setText(progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.generatePasswordNumberCharsEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val number = s.toString().toIntOrNull()
                if (number != null && number in 8..64) {
                    binding.generatePasswordNumberCharsSeekBar.progress = number
                }
                if (number == null || number < 8) {
                    binding.generatePasswordNumberCharsSeekBar.progress = 8
                    binding.generatePasswordNumberCharsEditText.setText("8")
                }
                if (number != null && number > 64) {
                    binding.generatePasswordNumberCharsSeekBar.progress = 64
                    binding.generatePasswordNumberCharsEditText.setText("64")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.generatePasswordNumberCharsSeekBar.progress = 16
        binding.generatePasswordNumberCharsEditText.setText("16")

        val specialCharacters = "!@#\\\$%^&*()_-+={}[]|:;<>,.?~\"'"
        binding.generateIncludeSpecials.tooltipText = specialCharacters
        binding.generateButton.setOnClickListener {
            if (binding.passwordGenerateOptions.visibility == View.GONE) {
                if (binding.passwordInputEditText.hasFocus()) {
                    binding.passwordInputEditText.clearFocus()
                }
                if (binding.passwordProgressBarLayout.visibility == View.GONE) {
                    expandView(binding.passwordProgressBarLayout, 50, 0)
                }
                expandView(binding.passwordGenerateOptions, 200, 0)
            } else {
                collapseView(binding.passwordGenerateOptions, 50, 0)
            }
        }
        binding.confirmGeneratePasswordButton.setOnClickListener {
            val generator = PasswordGenerator(binding.generatePasswordNumberCharsSeekBar.progress,
                binding.generateIncludeSpecials.isChecked, specialCharacters)
            val password = generator.generate()
            binding.passwordInputEditText.setText(password)
            collapseView(binding.passwordGenerateOptions, 50, 0)
        }


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val windowParams = window.attributes

            val displayMetrics = resources.displayMetrics
            val dialogWidth = (displayMetrics.widthPixels * 0.9).toInt()

            windowParams.width = dialogWidth
            windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            windowParams.dimAmount = 0.7f

            window.attributes = windowParams
        }
    }

    private fun checkFieldsAndToggleSaveButton() {
        val isAllFieldsFilled = binding.headlineInputEditText.text.toString().isNotBlank() &&
                binding.usernameInputEditText.text.toString().isNotBlank() &&
                binding.passwordInputEditText.text.toString().isNotBlank()

        val isPasswordCorrect = binding.passwordProgressBar.progress >= 10

        if (passwordItem != null) {
            val isEdited = binding.headlineInputEditText.text.toString() != passwordItem.title ||
                    binding.usernameInputEditText.text.toString() != passwordItem.username ||
                    binding.passwordInputEditText.text.toString() != passwordItem.password
            binding.saveButton.isEnabled = isAllFieldsFilled && isPasswordCorrect && isEdited
        } else {
            binding.saveButton.isEnabled = isAllFieldsFilled && isPasswordCorrect
        }

    }

    private fun checkPassword(s: Editable?) {
        val password = s.toString()
        var progress = 0f
        if (password.length >= 8) {
            progress += password.length * 1.85f
            binding.passwordRequirementsLengthWrong.visibility = View.GONE
            binding.passwordRequirementsLengthOK.visibility = View.VISIBLE
        } else {
            progress += password.length * 0.5f
            binding.passwordRequirementsLengthWrong.visibility = View.VISIBLE
            binding.passwordRequirementsLengthOK.visibility = View.GONE
        }

        if (password.any { it.isDigit() }) {
            progress *= 1.2f
            binding.passwordRequirementsNumbersOK.visibility = View.VISIBLE
            binding.passwordRequirementsNumbersWrong.visibility = View.GONE
        } else {
            progress *= 0.75f
            binding.passwordRequirementsNumbersOK.visibility = View.GONE
            binding.passwordRequirementsNumbersWrong.visibility = View.VISIBLE
        }

        if (password.any { it.isLowerCase() }) {
            progress *= 1.0112f
            binding.passwordRequirementsMinusWrong.visibility = View.GONE
            binding.passwordRequirementsMinusOK.visibility = View.VISIBLE
        } else {
            progress *= 0.75f
            binding.passwordRequirementsMinusWrong.visibility = View.VISIBLE
            binding.passwordRequirementsMinusOK.visibility = View.GONE
        }

        if (password.any { it.isUpperCase() }) {
            progress *= 1.0113f
            binding.passwordRequirementsMajusculesWrong.visibility = View.GONE
            binding.passwordRequirementsMajusculesOK.visibility = View.VISIBLE
        } else {
            progress *= 0.75f
            binding.passwordRequirementsMajusculesWrong.visibility = View.VISIBLE
            binding.passwordRequirementsMajusculesOK.visibility = View.GONE
        }

        if (password.firstOrNull()?.isUpperCase() == true) {
            progress *= 0.95f
        }

        if (password.takeLast(4).all { it.isDigit() }) {
            progress *= 0.75f
        }

        if (password.any { !it.isLetterOrDigit() }) {
            progress *= 1.45f
        } else {
            progress *= 0.7f
        }

        if (progress > 100) {
            progress=100f
        }
        setProgressBarColor(binding.passwordProgressBar, progress.toInt())
        binding.passwordProgressBar.progress = progress.toInt()
        checkFieldsAndToggleSaveButton()
    }

    private fun setProgressBarColor(progressBar: ProgressBar, progress: Int) {
        val hue = progress * 1.8f
        val hsv = floatArrayOf(hue, 1f, 1f)
        val color = Color.HSVToColor(hsv)

        val drawable = progressBar.progressDrawable.mutate() as LayerDrawable
        val progressDrawable = drawable.findDrawableByLayerId(android.R.id.progress)
        progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val closeButton = view.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener {
            dismiss()
        }
        onViewsCreated(view, savedInstanceState)
    }
    abstract fun onViewsCreated(view: View, savedInstanceState: Bundle?)

}