package com.dicoding.foundup.data.pref

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreference private constructor(context: Context) {
    private val dataStore = context.dataStore

    private val USER_TOKEN = stringPreferencesKey("user_token")
    private val LOGIN_STATUS = booleanPreferencesKey("login_status")


    suspend fun setStatusLogin(status: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOGIN_STATUS] = status
        }
    }

    val getStatusLogin: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[LOGIN_STATUS] ?: false
    }

    suspend fun saveUserToken(token: String?) {
        Log.d("UserPreference", "Saving token: $token") // Log token sebelum disimpan
        dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token.orEmpty()
        }
    }

    val getUserToken: Flow<String?> = dataStore.data.map { preferences ->
        val token = preferences[USER_TOKEN]
        Log.d("UserPreference", "Retrieved token: $token") // Log token saat diambil
        token
    }



    suspend fun clearUserToken() {
        dataStore.edit { preferences ->
            preferences.remove(USER_TOKEN)
        }
    }


    suspend fun clearUserLogin() {
        dataStore.edit { preferences ->
            preferences.remove(LOGIN_STATUS)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null


        fun getInstance(context: Context): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(context)
                INSTANCE = instance
                instance
            }
        }
    }
}