package cz.kureii.raintext.services

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import cz.kureii.raintext.R
import cz.kureii.raintext.model.EncryptionManager
import cz.kureii.raintext.model.PasswordDatabaseHelper
import cz.kureii.raintext.model.PasswordItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SavePasswordWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val encryptionManager: EncryptionManager
) : Worker(appContext, workerParams) {


    override fun doWork(): Result {
        try {
            val dbHelper = PasswordDatabaseHelper(applicationContext)

            val passwordItems = PasswordItem.dataToPasswordItemList(inputData)

            for (item in passwordItems) {
                var ultimateString = joinStrings(item.title, item.username, item.password)
                Log.i("SavePasswordWorker", "UltimateString: $ultimateString")
                if (ultimateString.isEmpty()) {
                    ultimateString = joinStrings(
                        getString(applicationContext, R.string.headline),
                        getString(applicationContext, R.string.user_name),
                        getString(applicationContext, R.string.password)
                    )
                }
                item.clear()
                Log.i("SavePasswordWorker", "UltimateString: $ultimateString")
                Log.i("SavePasswordWorker", "UltimateString: ${ultimateString.toByteArray().toHex()}")

                item.encryptedData = encryptionManager.encrypt(ultimateString.toByteArray())
            }

            dbHelper.deleteAllPasswords()

            for (item in passwordItems) {
                dbHelper.insertPassword(item)
            }


            return Result.success()
        } catch (e: Exception) {
            Log.e("SavePasswordWorker", "Došlo k chybě při zpracování dat", e)
            return Result.failure()
        }
    }

    fun joinStrings(vararg strings: String): String {
        val separator = '\u001F'  // Unit Separator
        return strings.joinToString(separator.toString())
    }
    private fun ByteArray.toHex(): String {
        return joinToString(separator = " ") { byte -> "%02x".format(byte) }
    }
}
