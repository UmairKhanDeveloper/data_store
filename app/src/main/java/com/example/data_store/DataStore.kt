package com.example.data_store

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore1 by preferencesDataStore("setting")

class data_Store_Manager(private val context: Context) {

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("userName")
        val USER_EMAIL_KEY = stringPreferencesKey("email")
        val USER_PASSWORD_KEY = stringPreferencesKey("password")
        val DARK_KEY = booleanPreferencesKey("dark_mode")
    }

    //save

    suspend fun saveUsername(name: String) {
        context.dataStore1.edit { prefs ->
            prefs[USER_NAME_KEY] = name

        }
    }


    suspend fun saveEmail(email: String) {
        context.dataStore1.edit { prefs ->
            prefs[USER_EMAIL_KEY] = email
        }

    }

    suspend fun savePassword(password: String) {
        context.dataStore1.edit { prefs ->
            prefs[USER_PASSWORD_KEY] = password
        }
    }

    suspend fun saveDarkMode(darkMode: Boolean) {
        context.dataStore1.edit { prefs ->
            prefs[DARK_KEY] = darkMode

        }
    }

    suspend fun clearAll() {
        context.dataStore1.edit { prefs ->
            prefs.remove(USER_NAME_KEY)
            prefs.remove(USER_EMAIL_KEY)
            prefs.remove(USER_PASSWORD_KEY)

        }

    }


    // read

    val readUserName: Flow<String> = context.dataStore1.data
        .map { prefs ->
            prefs[USER_NAME_KEY] ?: ""
        }

    val readEmail: Flow<String> = context.dataStore1.data
        .map { prefs ->
            prefs[USER_EMAIL_KEY] ?: ""
        }

    val readPassword: Flow<String> = context.dataStore1.data
        .map { prefs ->
            prefs[USER_PASSWORD_KEY] ?: ""
        }

    val readDarkMode: Flow<Boolean> = context.dataStore1.data
        .map {
            it[DARK_KEY] ?: false
        }


}


@Composable
fun StoreData() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStoreManger = remember { data_Store_Manager(context) }

    var nameInput by remember { mutableStateOf("") }
    val saveUserName by dataStoreManger.readUserName.collectAsState(initial = "")

    var emailInput by remember { mutableStateOf("") }
    val saveEmail by dataStoreManger.readEmail.collectAsState(initial = "")

    var passwordInput by remember { mutableStateOf("") }
    val savePassword by dataStoreManger.readPassword.collectAsState(initial = "")

    val darkMode by dataStoreManger.readDarkMode.collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = if (darkMode) Color.Black else Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Save UserName : $saveUserName",
            color = if (darkMode) Color.White else Color.Black,
            fontSize = 12.sp
        )
        Text(
            text = "Save Email : $saveEmail",
            color = if (darkMode) Color.White else Color.Black,
            fontSize = 12.sp
        )
        Text(
            text = "Save Password : $savePassword",
            color = if (darkMode) Color.White else Color.Black,
            fontSize = 12.sp
        )


        TextField(value = nameInput, onValueChange = {
            nameInput = it
        }, placeholder = {
            Text(text = "Enter your UserName")
        }, label = {
            Text(text = "UserName")
        })

        Spacer(modifier = Modifier.height(20.dp))

        TextField(value = emailInput, onValueChange = {
            emailInput = it
        }, placeholder = {
            Text(text = "Enter your Email")
        }, label = {
            Text(text = "Email")
        })

        Spacer(modifier = Modifier.height(20.dp))

        TextField(value = passwordInput, onValueChange = {
            passwordInput = it
        }, placeholder = {
            Text(text = "Enter your Password")
        }, label = {
            Text(text = "Password")
        })

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Save All Data",
                fontSize = 16.sp,
                color = if (darkMode) Color.White else Color.Black, modifier = Modifier.clickable {
                    scope.launch {
                        dataStoreManger.saveUsername(nameInput)
                        dataStoreManger.saveEmail(emailInput)
                        dataStoreManger.savePassword(passwordInput)
                        nameInput = ""
                        emailInput = ""
                        passwordInput = ""
                    }
                }
            )

            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = "Delete All Data",
                fontSize = 16.sp,
                color = if (darkMode) Color.White else Color.Black, modifier = Modifier.clickable {
                    scope.launch {
                        dataStoreManger.clearAll()
                    }
                }
            )


        }
        Spacer(modifier = Modifier.height(20.dp))

        Switch(checked = darkMode, onCheckedChange = { checked ->
            scope.launch {
                dataStoreManger.saveDarkMode(checked)
            }
        })

    }


}