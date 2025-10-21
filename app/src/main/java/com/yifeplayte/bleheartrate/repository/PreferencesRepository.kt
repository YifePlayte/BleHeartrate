package com.yifeplayte.bleheartrate.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yifeplayte.bleheartrate.model.DisplayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesRepository(private val context: Context) {
    
    private val savedDeviceNameKey = stringPreferencesKey("saved_device_name")
    private val savedDeviceAddressKey = stringPreferencesKey("saved_device_address")
    private val autoReconnectKey = booleanPreferencesKey("auto_reconnect")
    private val displayModeKey = stringPreferencesKey("display_mode")
    
    val savedDeviceName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[savedDeviceNameKey]
    }
    
    val savedDeviceAddress: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[savedDeviceAddressKey]
    }
    
    val autoReconnect: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[autoReconnectKey] ?: true
    }
    
    val displayMode: Flow<DisplayMode> = context.dataStore.data.map { preferences ->
        val mode = preferences[displayModeKey] ?: DisplayMode.FLOATING_WINDOW.name
        DisplayMode.valueOf(mode)
    }
    
    suspend fun saveDevice(name: String, address: String) {
        context.dataStore.edit { preferences ->
            preferences[savedDeviceNameKey] = name
            preferences[savedDeviceAddressKey] = address
        }
    }
    
    suspend fun clearSavedDevice() {
        context.dataStore.edit { preferences ->
            preferences.remove(savedDeviceNameKey)
            preferences.remove(savedDeviceAddressKey)
        }
    }
    
    suspend fun setAutoReconnect(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[autoReconnectKey] = enabled
        }
    }
    
    suspend fun setDisplayMode(mode: DisplayMode) {
        context.dataStore.edit { preferences ->
            preferences[displayModeKey] = mode.name
        }
    }
}
