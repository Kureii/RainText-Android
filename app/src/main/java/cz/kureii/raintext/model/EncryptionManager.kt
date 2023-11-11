package cz.kureii.raintext.model

import android.content.Context
import android.provider.Settings.Secure
import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionManager @Inject constructor() {
    /*private var fingerprintToken: ByteArray? = null
    private var identifier: ByteArray? = null

    external fun nativeEncrypt(
        token: ByteArray,
        identifier: ByteArray,
        plainText: ByteArray,
        iterations: Int
    ): ByteArray

    external fun nativeDecrypt(
        token: ByteArray,
        identifier: ByteArray,
        encryptedText: ByteArray,
        iterations: Int
    ): ByteArray

    fun setToken(context: Context, token: ByteArray): Boolean {
        return try {
            Log.i("set Token", "starting set token")
            fingerprintToken = token
            identifier = Secure.getString(context.contentResolver, Secure.ANDROID_ID).toByteArray()
            Log.i("tokens", "token: ${fingerprintToken!!.toHex()} - identifer: ${identifier!!.toHex()}")
            fingerprintToken != null && identifier != null
        } catch (_: Exception) {
            false
        }
    }

    fun encrypt(data: String): ByteArray {
        return if (fingerprintToken != null && identifier != null && data.isNotEmpty()) {
            //Log.i("EncryptionManager", "encrypting")
            nativeEncrypt(fingerprintToken!!, identifier!!, data.toByteArray(), 1)
        } else {
            ByteArray(0)
        }
    }

    fun decrypt(data: ByteArray): String {
        return if (fingerprintToken != null && identifier != null && data.isNotEmpty()) {
            //Log.i("EncryptionManager", "decrypting data: $data")
            String(nativeDecrypt(fingerprintToken!!, identifier!!, data, 1))
        } else {
            ""
        }
    }*/
    private var secretKey: SecretKey? = null

    fun setToken(secretKey: SecretKey) {
        this.secretKey = secretKey
    }

    fun encrypt(data: ByteArray): ByteArray {
        if (secretKey == null) {
            Log.e("EncryptionManager", "Secret key is not set.")
            return ByteArray(0)
        }

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        var ci = cipher.doFinal(data)
        Log.i("encrypt", "encripted: ${ci.toHex()}")
        Log.i("encrypt", "iv: ${cipher.iv.toHex()}")

        return cipher.iv + ci
    }

    fun decrypt(data: ByteArray): ByteArray {
        if (secretKey == null) {
            Log.e("EncryptionManager", "Secret key is not set.")
            return ByteArray(0)
        }

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, data, 0, 12))
        return cipher.doFinal(data, 12, data.size - 12)
    }
    private fun ByteArray.toHex(): String {
        return joinToString(separator = " ") { byte -> "%02x".format(byte) }
    }

}
