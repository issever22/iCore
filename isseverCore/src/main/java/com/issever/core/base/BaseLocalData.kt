package com.issever.core.base

import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.issever.core.data.enums.Theme
import com.issever.core.data.remote.CoreNetwork
import com.issever.core.util.CoreConstants.CoreLocalData.DATABASE_NAME
import com.issever.core.util.CoreConstants.CoreLocalData.DEFAULT_LANG
import com.issever.core.util.CoreConstants.CoreLocalData.LANG_KEY
import com.issever.core.util.CoreConstants.CoreLocalData.THEME
import com.issever.core.util.Resource
import com.issever.core.util.ResourceProvider
import com.issever.core.util.extensions.handleError
import java.util.Locale

abstract class BaseLocalData {

    open lateinit var preferences: SharedPreferences
    open var gson = CoreNetwork.gson
    private var isInitialized = false

    init {
        val context = ResourceProvider.getAppContext()
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
            Log.e("BaseLocalData", e.localizedMessage ?: "database operation error")
            Resource.error(e.handleError())
        }
    }

    // Shared Preferences functions ...

    open fun getLocaleLanguage(): String {
        return Locale.getDefault().language
    }

    open fun saveInitialLocale() {
        preferences.edit().putString(DEFAULT_LANG, getLocaleLanguage()).apply()
    }

    open fun getInitialLocale(): String {
        return preferences.getString(DEFAULT_LANG, getLocaleLanguage()) ?: getLocaleLanguage()
    }

    open fun setSelectedLanguage(language: String) {
        preferences.edit().putString(LANG_KEY, language).apply()
    }

    open fun getSelectedLanguage(): String {
        return preferences.getString(LANG_KEY, getInitialLocale()) ?: getInitialLocale()
    }

    open fun setSelectedTheme(theme: Theme) {
        preferences.edit().putString(THEME, theme.name).apply()
        theme.setTheme()
    }

    open fun getSelectedTheme(): Theme {
        val themeName = preferences.getString(THEME, Theme.FOLLOW_SYSTEM.name) ?: Theme.FOLLOW_SYSTEM.name
        return Theme.valueOf(themeName)
    }

    open fun setIntData(saveName: String, data: Int) {
        preferences.edit().putInt(saveName, data).apply()
    }

    open fun getIntData(saveName: String, defaultValue: Int = 0): Int {
        return preferences.getInt(saveName, defaultValue)
    }

    open fun setLongData(saveName: String, data: Long) {
        preferences.edit().putLong(saveName, data).apply()
    }

    open fun getLongData(saveName: String, defaultValue: Long = 0L): Long {
        return preferences.getLong(saveName, defaultValue)
    }

    open fun setStringData(saveName: String, data: String) {
        preferences.edit().putString(saveName, data).apply()
    }

    open fun getStringData(saveName: String): String {
        return preferences.getString(saveName, "") ?: ""
    }

    open fun setBooleanData(saveName: String, data: Boolean) {
        preferences.edit().putBoolean(saveName, data).apply()
    }

    open fun getBooleanData(saveName: String, defaultValue: Boolean = false): Boolean {
        return preferences.getBoolean(saveName, defaultValue)
    }

    open fun setStringArrayData(saveName: String, data: ArrayList<String>) {
        preferences.edit().putStringSet(saveName, data.toSet()).apply()
    }

    open fun getStringArrayData(saveName: String): ArrayList<String> {
        val data = preferences.getStringSet(saveName, setOf())
        return data?.let { ArrayList(it) } ?: arrayListOf()
    }

    open fun <T> setJsonData(saveName: String, data: T) {
        val jsonData = gson.toJson(data)
        preferences.edit().putString(saveName, jsonData).apply()
    }

    open fun <T> getJsonData(saveName: String, type: Class<T>): T {
        val jsonData = preferences.getString(saveName, null)
        return jsonData?.let { gson.fromJson(it, type) }
            ?: throw NoSuchElementException("No data found for key $saveName")
    }
}
