package com.issever.core.base

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.issever.core.data.remote.CoreNetwork
import com.issever.core.util.CoreConstants.CoreLocalData.DATABASE_NAME
import com.issever.core.util.CoreConstants.CoreLocalData.DEFAULT_LANG
import com.issever.core.util.CoreConstants.CoreLocalData.ENGLISH
import com.issever.core.util.CoreConstants.CoreLocalData.FOLLOW_SYSTEM
import com.issever.core.util.CoreConstants.CoreLocalData.LANG_KEY
import com.issever.core.util.CoreConstants.CoreLocalData.THEME
import com.issever.core.util.Resource
import com.issever.core.util.ResourceProvider
import com.issever.core.util.extensions.handleError
import java.util.Locale

abstract class BaseLocalData {

    open lateinit var preferences: SharedPreferences
    open lateinit var gson: Gson
    private var isInitialized = false

    open fun init(context: Context) {
        if (isInitialized) return
        val masterKey: MasterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        preferences = EncryptedSharedPreferences.create(
            context,
            DATABASE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        gson = CoreNetwork.gson
        isInitialized = true
    }

    suspend fun <T> databaseOperation(
        operation: suspend () -> T,
        successMessage: String? = null
    ): Resource<T> {
        return try {
            val result = operation.invoke()
            Resource.success(result, successMessage)
        } catch (e: Exception) {
            Resource.error(e.handleError())
        }
    }

    // Shared Preferences functions ...

    fun saveInitialLocale() {
        val defaultLang = Locale.getDefault().language
        preferences.edit().putString(DEFAULT_LANG, defaultLang).apply()
    }

    fun getInitialLocale(): String {
        return preferences.getString(DEFAULT_LANG, ENGLISH) ?: ENGLISH
    }

    fun setSelectedLanguage(language: String) {
        preferences.edit().putString(LANG_KEY, language).apply()
    }

    fun getSelectedLanguage(): String {
        return preferences.getString(LANG_KEY, FOLLOW_SYSTEM) ?: FOLLOW_SYSTEM
    }

    fun setSelectedTheme(theme: String) {
        preferences.edit().putString(THEME, theme).apply()
    }

    fun getSelectedTheme(): String {
        return preferences.getString(THEME, FOLLOW_SYSTEM) ?: FOLLOW_SYSTEM
    }

    fun setIntData(saveName: String, data: Int) {
        preferences.edit().putInt(saveName, data).apply()
    }

    fun getIntData(saveName: String, defaultValue: Int = 0): Int {
        return preferences.getInt(saveName, defaultValue)
    }

    fun setLongData(saveName: String, data: Long) {
        preferences.edit().putLong(saveName, data).apply()
    }

    fun getLongData(saveName: String, defaultValue: Long = 0L): Long {
        return preferences.getLong(saveName, defaultValue)
    }

    fun setStringData(saveName: String, data: String) {
        preferences.edit().putString(saveName, data).apply()
    }

    fun getStringData(saveName: String): String {
        return preferences.getString(saveName, "") ?: ""
    }

    fun setBooleanData(saveName: String, data: Boolean) {
        preferences.edit().putBoolean(saveName, data).apply()
    }

    fun getBooleanData(saveName: String, defaultValue: Boolean = false): Boolean {
        return preferences.getBoolean(saveName, defaultValue)
    }

    fun setStringArrayData(saveName: String, data: ArrayList<String>) {
        preferences.edit().putStringSet(saveName, data.toSet()).apply()
    }

    fun getStringArrayData(saveName: String): ArrayList<String> {
        val data = preferences.getStringSet(saveName, setOf())
        return data?.let { ArrayList(it) } ?: arrayListOf()
    }

    fun <T> setJsonData(saveName: String, data: T) {
        val jsonData = gson.toJson(data)
        preferences.edit().putString(saveName, jsonData).apply()
    }

    fun <T> getJsonData(saveName: String, type: Class<T>): T {
        val jsonData = preferences.getString(saveName, null)
        return jsonData?.let { gson.fromJson(it, type) }
            ?: throw NoSuchElementException("No data found for key $saveName")
    }
}
