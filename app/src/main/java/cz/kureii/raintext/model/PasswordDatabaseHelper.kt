package cz.kureii.raintext.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PasswordDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "cz.kureii.raintext.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "records"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSQL = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_USERNAME TEXT," +
                "$COLUMN_PASSWORD TEXT)"
        db.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertPassword(item: PasswordItem): Long {
        val values = ContentValues().apply {
            put(COLUMN_TITLE, item.title)
            put(COLUMN_USERNAME, item.username)
            put(COLUMN_PASSWORD, item.password)
        }
        return writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun deleteAllPasswords() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }

    fun getAllPasswords(): MutableList<PasswordItem> {
        val passwords = mutableListOf<PasswordItem>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                val username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))
                val password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
                passwords.add(PasswordItem(id, title, username, password))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return passwords
    }

}
