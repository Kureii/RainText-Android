package cz.kureii.raintext.model

import androidx.work.Data
import org.json.JSONArray
import org.json.JSONObject

data class PasswordItem(
    var id: Int,
    val title: String,
    val username: String,
    val password: String
) {

    companion object {

        fun passwordItemListToData(passwordItems: List<PasswordItem>): Data {
            val jsonArray = JSONArray()
            passwordItems.forEach {
                val jsonObject = JSONObject()
                jsonObject.put("id", it.id)
                jsonObject.put("title", it.title)
                jsonObject.put("username", it.username)
                jsonObject.put("password", it.password)
                jsonArray.put(jsonObject)
            }
            return Data.Builder().putString("passwordItems", jsonArray.toString()).build()
        }

        fun dataToPasswordItemList(data: Data): List<PasswordItem> {
            val jsonData = data.getString("passwordItems")
            val jsonArray = JSONArray(jsonData)
            val passwordItems = mutableListOf<PasswordItem>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val item = PasswordItem(
                    jsonObject.getInt("id"),
                    jsonObject.getString("title"),
                    jsonObject.getString("username"),
                    jsonObject.getString("password")
                )
                passwordItems.add(item)
            }
            return passwordItems
        }
    }

}
