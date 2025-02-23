package com.example.googletaskproject.core

import android.content.Context
import android.content.SharedPreferences
import com.example.googletaskproject.data.model.TaskRingtoneModel
import com.example.googletaskproject.utils.Const
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SessionManager {

    private const val PREF_NAME = "MyPref"
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private val gson = Gson()

    /**
     * Initialize SharedPreferences (call this in Application class)
     */
    fun init(context: Context) {
        sharedPreferences = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        if (!getBoolean(Const.IS_SESSION_SAVED)) {
            putString(Const.SELECTED_TIME_ZONE, "Auto (Device Time Zone)")
            putObject(Const.RINGTONE_MUSIC, TaskRingtoneModel("None", -1))
            putBoolean(Const.IS_SESSION_SAVED, true)
        }
    }

    /**
     * Save data asynchronously
     */
    fun putString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun putInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun putLong(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun <T> putObject(key: String, obj: T) {
        val json = gson.toJson(obj)
        editor.putString(key, json).apply()
    }
    inline fun <reified T> putList(key: String, list: List<T>) {
        editor.putString(key, Gson().toJson(list)).apply()
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



    inline fun <reified T> getList(key: String): List<T> {
        val json = getString(key)
        return Gson().fromJson(json, object : TypeToken<List<T>>() {}.type) ?: emptyList()
    }
    /**
     * Remove a specific key
     */
    fun removeKey(key: String) {
        editor.remove(key).apply()
    }

    /**
     * Clear all preferences
     */
    fun clearAll() {
        editor.clear().apply()
    }
}