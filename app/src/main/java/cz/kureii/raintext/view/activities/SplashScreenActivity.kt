package cz.kureii.raintext.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.work.WorkManager
import com.scottyab.rootbeer.RootBeer
import cz.kureii.raintext.R
import cz.kureii.raintext.model.EncryptionManager
import cz.kureii.raintext.utils.security.EmulatorDetection
import dagger.hilt.android.AndroidEntryPoint
import java.security.Key
import java.security.KeyStore
import java.security.SecureRandom
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    @Inject
    lateinit var encryptionManager: EncryptionManager
    val keyAlias = "cz.kureii.raintext.keyx5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val emulatorDetection = EmulatorDetection()
        val rootBeer = RootBeer(this)


        if (!isKeyPresent()) {
            if (!generateSecretKey()) {
                Toast.makeText(this, "Key generation failed", Toast.LENGTH_LONG).show()
                finishAndRemoveTask()
                return
            }
        }


        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        Toast.makeText(
                            applicationContext,
                            "Exiting the application", Toast.LENGTH_SHORT
                        ).show()
                        finishAndRemoveTask()
                    } else {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(
                            applicationContext,
                            "Authentication error: $errString", Toast.LENGTH_SHORT
                        ).show()
                        finishAndRemoveTask()
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    val secretKey = getSecretKey() // Metoda pro získání klíče z Android Keystore
                    if (secretKey != null) {
                        // Předání klíče do EncryptionManager

                        encryptionManager.setToken(secretKey)
                        val intent =
                            Intent(this@SplashScreenActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Exit")
            .build()

        val sharedPrefTheme = this.getSharedPreferences("THEME", Context.MODE_PRIVATE)

        when (sharedPrefTheme.getInt("Main", 0)) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Light
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // Dark
        }


        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
            finishAndRemoveTask()
        }
        /*if (emulatorDetection.isEmulator()) {

            builder.setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.app_shut_down) + " (" + getString(R.string.emulator_detected) + ")")
                .setCancelable(false)
            builder.create().show()
        } else if (Debug.isDebuggerConnected()) {
            builder.setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.app_shut_down) + " (" + getString(R.string.debug_tool_connected) + ")")
                .setCancelable(false)
            builder.create().show()
        } else if (rootBeer.isRooted) {
            builder.setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.app_shut_down) + " (" + getString(R.string.device_rooted) + ")")
                .setCancelable(false)
            builder.create().show()
        } else {*/
            WorkManager.getInstance(this).getWorkInfosByTagLiveData("saveDataTag")
                .observe(this, Observer { workInfoList ->
                    if (workInfoList.isNullOrEmpty()) {
                        biometricPrompt.authenticate(promptInfo)
                    } else {
                        val workInfo = workInfoList.firstOrNull {it.state.isFinished}
                        if (workInfo != null) {
                            biometricPrompt.authenticate(promptInfo)

                        }

                    }

                })
        //}

    }

    fun generateSecretKey(): Boolean{
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                //setRandomizedEncryptionRequired(true)
                setIsStrongBoxBacked(true)
                setKeySize(256)
            }.build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()

            return true
        } catch (e: Exception) {
            try {
                val keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)

                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                ).apply {
                    setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    //setRandomizedEncryptionRequired(true)
                    setIsStrongBoxBacked(false)
                    setKeySize(256)
                }.build()

                keyGenerator.init(keyGenParameterSpec)
                keyGenerator.generateKey()

                return true
            } catch (ee: Exception) {
                Log.e("SplashScreenActivity", "Failed to generate the key", ee)
                return false
            }
        }
    }

    // Metoda pro získání již vytvořeného klíče
    fun getSecretKey(): SecretKey? {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey(keyAlias, null) as? SecretKey
    }

    fun isKeyPresent(): Boolean {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.containsAlias(keyAlias)
    }
}