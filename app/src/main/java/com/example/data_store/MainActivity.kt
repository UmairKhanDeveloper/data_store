package com.example.data_store

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data_store.ui.theme.Data_storeTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStoreManager = dataStoreManger(applicationContext)

        setContent {
            val isDarkMode by dataStoreManager.readDarkMode.collectAsState(initial = false)

            Data_storeTheme(darkTheme = isDarkMode) {
                DataStoreScreen(dataStoreManager)
            }
        }
    }
}


val Context.datastore by preferencesDataStore(name = "setting")

class dataStoreManger(private val context: Context) {

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("username")
        val EMAIL_NAME_KEY = stringPreferencesKey("email")
        val PASSWORD_NAME_KEY = stringPreferencesKey("password")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    }


    // save
    suspend fun saveUserName(name: String) {
        context.datastore.edit { prefs ->
            prefs[USER_NAME_KEY] = name
        }
    }

    suspend fun saveEmail(email: String) {
        context.datastore.edit { prefs ->
            prefs[EMAIL_NAME_KEY] = email
        }
    }

    suspend fun savePassword(password: String) {
        context.datastore.edit { prefs ->
            prefs[PASSWORD_NAME_KEY] = password
        }
    }

    suspend fun darkMode(darkMode: Boolean) {
        context.datastore.edit { prefs ->
            prefs[DARK_MODE_KEY] = darkMode
        }
    }

    suspend fun clearData() {
        context.datastore.edit { prefs ->
            prefs.remove(USER_NAME_KEY)
            prefs.remove(EMAIL_NAME_KEY)
            prefs.remove(PASSWORD_NAME_KEY)
        }
    }

    // read
    val readNameFlow: Flow<String> = context.datastore.data
        .map { prefs ->
            prefs[USER_NAME_KEY] ?: ""
        }


    val readEmailFlow: Flow<String> = context.datastore.data
        .map { prefs ->
            prefs[EMAIL_NAME_KEY] ?: ""
        }

    val readPasswordFlow: Flow<String> = context.datastore.data
        .map { prefs ->
            prefs[PASSWORD_NAME_KEY] ?: ""
        }


    val readDarkMode: Flow<Boolean> = context.datastore.data
        .map { prefs ->
            prefs[DARK_MODE_KEY] ?: false
        }
}


@Composable
fun DataStoreScreen(dataStoreManager: dataStoreManger) {
    val coroutineScope = rememberCoroutineScope()

    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    val savedName by dataStoreManager.readNameFlow.collectAsState(initial = "")
    val savedEmail by dataStoreManager.readEmailFlow.collectAsState(initial = "")
    val savedPassword by dataStoreManager.readPasswordFlow.collectAsState(initial = "")
    val isDarkMode by dataStoreManager.readDarkMode.collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "User Info Storage",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Username") },
            placeholder = { Text("Enter your name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        var isPasswordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("Password") },
            placeholder = { Text("Enter your password") },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon =
                    if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(imageVector = icon, contentDescription = "Toggle Password")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.saveUserName(nameInput)
                        dataStoreManager.saveEmail(emailInput)
                        dataStoreManager.savePassword(passwordInput)
                        nameInput = ""
                        emailInput = ""
                        passwordInput = ""
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("Save")
            }

            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.clearData()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Text("Clear")
            }
        }

        Divider(Modifier.padding(vertical = 16.dp))

        Text(
            text = "Saved Data",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
            color = if (isDarkMode) Color.White else Color.Black

        )

        InfoRow(label = "Name", value = savedName, dataStoreManager)
        InfoRow(label = "Email", value = savedEmail, dataStoreManager)
        InfoRow(label = "Password", value = savedPassword, dataStoreManager)

        Divider(
            Modifier.padding(vertical = 16.dp),
            color = if (isDarkMode) Color.White else Color.Black
        )

        Text(
            text = "App Settings",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
            color = if (isDarkMode) Color.White else Color.Black
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Dark Mode",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDarkMode) Color.White else Color.Black
            )
            Switch(
                checked = isDarkMode,
                onCheckedChange = { checked ->
                    coroutineScope.launch {
                        dataStoreManager.darkMode(checked)
                    }
                }
            )
        }
    }
}


@Composable
fun InfoRow(label: String, value: String, dataStoreManager: dataStoreManger) {
    val isDarkMode by dataStoreManager.readDarkMode.collectAsState(initial = false)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDarkMode) Color.White else Color.Black
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDarkMode) Color.White else Color.Black
        )
    }
}
