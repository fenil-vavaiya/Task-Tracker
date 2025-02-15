package com.example.googletaskproject.core

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.googletaskproject.domain.TaskRingtoneModel
import com.example.googletaskproject.utils.Const
import com.google.gson.Gson

object SessionManager {

    private const val PREF_NAME = "app_preferences"
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()


    /**
     * Initialize SharedPreferences (call this in Application class)
     */

    fun init(context: Context, useEncryption: Boolean = true) {
        sharedPreferences = if (useEncryption) {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
        if (!getBoolean(Const.IS_SESSION_SAVED)) {
            putString(Const.SELECTED_TIME_ZONE, "Auto (Device Time Zone)")
            putObject(Const.RINGTONE_MUSIC,  TaskRingtoneModel("None", -1))

            putBoolean(Const.IS_SESSION_SAVED, true)
        }

    }

    /**
     * Save data asynchronously
     */

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    fun <T> putObject(key: String, obj: T) {
        val json = gson.toJson(obj)
        sharedPreferences.edit().putString(key, json).apply()
    }

    /**
     * Retrieve data with default values
     */

    fun getString(key: String, default: String = ""): String {
        return sharedPreferences.getString(key, default) ?: default
    }

    fun getInt(key: String, default: Int = 0): Int {
        return sharedPreferences.getInt(key, default)
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    fun getLong(key: String, default: Long = 0L): Long {
        return sharedPreferences.getLong(key, default)
    }

    /**
     * Retrieve custom object from JSON string
     */
    fun <T> getObject(key: String, clazz: Class<T>): T? {
        val json = sharedPreferences.getString(key, null) ?: return null
        return gson.fromJson(json, clazz)
    }

    /**
     * Remove a specific key
     */
    fun removeKey(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    /**
     * Clear all preferences
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
