package com.example.data_store

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore2 by preferencesDataStore("setting")

class DataStoreExample(private val context: Context) {

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("userName")
        val USER_EMAIL_KEY = stringPreferencesKey("email")
        val USER_PASSWORD_KEY = stringPreferencesKey("password")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    // Save
    suspend fun saveUserName(name: String) {
        context.dataStore2.edit { prefs -> prefs[USER_NAME_KEY] = name }
    }

    suspend fun saveEmail(email: String) {
        context.dataStore2.edit { prefs -> prefs[USER_EMAIL_KEY] = email }
    }

    suspend fun savePassword(password: String) {
        context.dataStore2.edit { prefs -> prefs[USER_PASSWORD_KEY] = password }
    }

    suspend fun saveDarkMode(darkMode: Boolean) {
        context.dataStore2.edit { prefs -> prefs[DARK_MODE] = darkMode }
    }

    suspend fun clearAllData() {
        context.dataStore2.edit { prefs -> prefs.clear() }
    }

    // Read
    val readUserName: Flow<String> = context.dataStore2.data.map { it[USER_NAME_KEY] ?: "" }
    val readEmail: Flow<String> = context.dataStore2.data.map { it[USER_EMAIL_KEY] ?: "" }
    val readPassword: Flow<String> = context.dataStore2.data.map { it[USER_PASSWORD_KEY] ?: "" }
    val readDarkMode: Flow<Boolean> = context.dataStore2.data.map { it[DARK_MODE] ?: false }
}


@Composable
fun DataStoreExampleScreen() {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreExample(context) }
    val scope = rememberCoroutineScope()

    val savedUserName by dataStoreManager.readUserName.collectAsState(initial = "")
    val savedEmail by dataStoreManager.readEmail.collectAsState(initial = "")
    val savedPassword by dataStoreManager.readPassword.collectAsState(initial = "")
    val darkMode by dataStoreManager.readDarkMode.collectAsState(initial = false)

    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (darkMode) Color.Black else Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Saved UserName: $savedUserName",
            color = if (darkMode) Color.White else Color.Black
        )
        Text(
            text = "Saved Email: $savedEmail",
            color = if (darkMode) Color.White else Color.Black
        )
        Text(
            text = "Saved Password: $savedPassword",
            color = if (darkMode) Color.White else Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("UserName") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        dataStoreManager.saveUserName(userName)
                        dataStoreManager.saveEmail(email)
                        dataStoreManager.savePassword(password)
                        userName = ""
                        email = ""
                        password = ""
                    }
                }
            ) {
                Text("Save")
            }

            Button(
                onClick = {
                    scope.launch {
                        dataStoreManager.clearAllData()
                    }
                }
            ) {
                Text("Clear")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Dark Mode",
                color = if (darkMode) Color.White else Color.Black
            )
            Switch(
                checked = darkMode,
                onCheckedChange = { checked ->
                    scope.launch {
                        dataStoreManager.saveDarkMode(checked)
                    }
                }
            )
        }
    }
}
