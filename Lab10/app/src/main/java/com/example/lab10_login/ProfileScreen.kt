
package com.example.lab10_login
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.Lifecycle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ProfileScreen(navController: NavHostController) {
    lateinit var sharedPreferences: SharedPreferencesManager
    val contextForToast = LocalContext.current.applicationContext
    sharedPreferences = SharedPreferencesManager(contextForToast)
    val userId = sharedPreferences.userId ?: ""
    val createClient = StudentAPI.create()
    val initialStudent = ProfileClass(std_id = "", std_name = "", std_gender = "", role = "")
    var studentItems by remember { mutableStateOf(initialStudent) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> {}
            Lifecycle.State.INITIALIZED -> {}
            Lifecycle.State.CREATED -> {}
            Lifecycle.State.STARTED -> {}
            Lifecycle.State.RESUMED -> {
                createClient.searchStudent(userId)
                    .enqueue(object : Callback<ProfileClass> {
                        override fun onResponse(
                            call: Call<ProfileClass>,
                            response: Response<ProfileClass>
                        ) {
                            if (response.isSuccessful) {
                                studentItems = ProfileClass(
                                    response.body()!!.std_id, response.body()!!.std_name,
                                    response.body()!!.std_gender, response.body()!!.role
                                )
                            } else {
                                Toast.makeText(
                                    contextForToast,
                                    "Student ID Not Found", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<ProfileClass>, t: Throwable) {
                            Toast.makeText(
                                contextForToast,
                                "Error onFailure "+t.message, Toast.LENGTH_LONG).show()
                        }
                    })
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Profile",
            fontSize = 25.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            lineHeight = 30.sp,
            text = "Student ID: $userId\n\nName: ${studentItems.std_name}\n\nGender: ${studentItems.std_gender}\n\nRole: ${studentItems.role} ",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        val isInvisible = studentItems.role == "admin"
        Box(/// For admin role
            content = {
                if (isInvisible) {
                    Button(
                        modifier = Modifier.fillMaxWidth()
                            .height(50.dp),
                        onClick = { isDialogVisible = true }
                    ) {
                        Text(text = "Show all students")
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextButton(
            onClick = {
                isDialogVisible = true
            }
        ) {
            Text("Logout")
        }


        if (isDialogVisible) {
            AlertDialog(
                onDismissRequest = { isDialogVisible = false }, // Close dialog if dismissed
                title = {
                    Text(text = "Logout")
                },
                text = {
                    Column {
                        Text("Do you want to logout?")
                        Row ( modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center){
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { isChecked = it }
                            )
                            Text("Remember my student id")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {

                            if (isChecked) {
                                sharedPreferences.clearUserLogin()

                            }
                            else{
                                sharedPreferences.clearUserAll()

                            }
                            isDialogVisible = false
                            navController.navigate(Screen.Login.route)
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { isDialogVisible = false } // Close dialog if canceled
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }
}