package cz.kureii.raintext.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper

class ClipboardUtility(private val context: Context) {

    private val clipboardManager: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val handler = Handler(Looper.getMainLooper())

    private val clipboardListener = ClipboardManager.OnPrimaryClipChangedListener {
        val newClip = clipboardManager.primaryClip
        if (newClip?.getItemAt(0)?.text != currentClip?.getItemAt(0)?.text) {
            handler.removeCallbacksAndMessages(null)
        }
    }

    private var currentClip: ClipData? = null

    init {
        clipboardManager.addPrimaryClipChangedListener(clipboardListener)
    }

    private fun copyToClipboard(label: String, text: String, duration: Long) {
        handler.removeCallbacksAndMessages(null)
        val clip = ClipData.newPlainText(label, text)
        currentClip = clip
        clipboardManager.setPrimaryClip(clip)

        clearClipboardAfter(duration)
    }

    fun copyUsername(username: String, duration: Long) {
        copyToClipboard("Username", username, duration)
    }

    fun copyPassword(password: String, duration: Long) {
        copyToClipboard("Password", password, duration)
    }

    private fun clearClipboardAfter(duration: Long) {
        handler.postDelayed({
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", ""))
        }, duration*1000)
    }
}

