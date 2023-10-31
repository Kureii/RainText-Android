package cz.kureii.raintext.services

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import cz.kureii.raintext.model.PasswordDatabaseHelper
import cz.kureii.raintext.model.PasswordItem

class SavePasswordWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        try {
            val passwordItems = PasswordItem.dataToPasswordItemList(inputData)
            val dbHelper = PasswordDatabaseHelper(applicationContext)

            // Smazání všech záznamů v databázi
            dbHelper.deleteAllPasswords()

            // Vložení všech záznamů
            for (item in passwordItems) {
                dbHelper.insertPassword(item)
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}
