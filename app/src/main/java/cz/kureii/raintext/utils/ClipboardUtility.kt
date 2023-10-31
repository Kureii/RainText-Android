package cz.kureii.raintext.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper

class ClipboardUtility(private val context: Context) {

    private val clipboardManager: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val handler = Handler(Looper.getMainLooper())


    fun copyToClipboard(label: String, text: String) {
        val clip = ClipData.newPlainText(label, text)
        clipboardManager.setPrimaryClip(clip)
    }

    fun copyUsername(username: String, duration: Long) {
        copyToClipboard("Username", username)
        clearClipboardAfter(duration)
    }

    fun copyPassword(password: String, duration: Long) {
        copyToClipboard("Password", password)
        clearClipboardAfter(duration)
    }

    private fun clearClipboardAfter(duration: Long) {
        handler.postDelayed({
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", ""))
        }, duration*1000)
    }
}
