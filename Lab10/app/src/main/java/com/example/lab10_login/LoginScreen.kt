package com.example.lab10_login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavHostController) {
    var studentID by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val createClient = StudentAPI.create()
    val contextForToast = LocalContext.current.applicationContext
    var studentItems = remember { mutableStateListOf<LoginClass>() }
    lateinit var sharedPreferences: SharedPreferencesManager
    sharedPreferences = SharedPreferencesManager(context = contextForToast)

    // Check Lifecycle State
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> {}
            Lifecycle.State.INITIALIZED -> {}
            Lifecycle.State.CREATED -> {}
            Lifecycle.State.STARTED -> {}
            Lifecycle.State.RESUMED -> {
                if (sharedPreferences.isLoggedIn) {
                    navController.navigate(Screen.Profile.route)
                }
                if (!sharedPreferences.userId.isNullOrEmpty()) {
                    studentID = sharedPreferences.userId ?: "1"
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Log In", fontSize = 25.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = studentID,
            onValueChange = {
                studentID = it
                isButtonEnabled = studentID.isNotEmpty() && password.isNotEmpty()
            },
            label = { Text(text = "Student ID") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isButtonEnabled = studentID.isNotEmpty() && password.isNotEmpty()
            },
            label = { Text(text = "Password") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                createClient.loginStudent(studentID, password)
                    .enqueue(object : Callback<LoginClass> {
                        override fun onResponse(
                            call: Call<LoginClass>,
                            response: Response<LoginClass>
                        ) {
                            if (response.isSuccessful) {
                                if (response.body()!!.success == 1) {
                                    sharedPreferences.isLoggedIn = true
                                    sharedPreferences.userId = response.body()!!.std_id
                                    Toast.makeText(contextForToast, "Login successful", Toast.LENGTH_LONG).show()
                                    navController.navigate(Screen.Profile.route)
                                } else {
                                    Toast.makeText(contextForToast, "Student ID or password is incorrect.", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                studentItems.clear()
                                Toast.makeText(contextForToast, "Student ID Not Found", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<LoginClass>, t: Throwable) {
                            Toast.makeText(contextForToast, "Error onFailure " + t.message, Toast.LENGTH_LONG).show()
                        }
                    })
            },
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Don't have an account?")
            TextButton(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    navController.navigate(Screen.Register.route)
                }
            ) {
                Text(text = "Register")
            }
        }
    }
}
