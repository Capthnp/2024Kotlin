package com.example.lab_8911

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun EditScreen(navController: NavHostController){
    val data = navController.previousBackStackEntry?.savedStateHandle?.get<Student>("data")
        ?: Student(std_id = "", std_name = "", std_gender = "", std_age = 0)

    var textFieldID by remember { mutableStateOf(data.std_id) }
    var textFieldName by remember { mutableStateOf(data.std_name) }
    var Gender by remember { mutableStateOf(data.std_gender) }
    var textFieldAge by remember { mutableStateOf(data.std_age.toString()) }
    val contextForToast = LocalContext.current.applicationContext
    var showDialog by remember { mutableStateOf(false) }
    val dbHandler = DatabaseHelper.getInstance(contextForToast)
    dbHandler.writableDatabase
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Edit Student",
            fontSize = 25.sp
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = textFieldID,
            onValueChange = {  },
            label = { Text(text = "Student ID") },
            enabled = false
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = textFieldName,
            onValueChange = { textFieldName = it },
            label = { Text(text = "Student Name") }
        )
        Text(
            text = "Gender :" ,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        )
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = Gender == "Male",
                onClick = { Gender = "Male" },
                colors = RadioButtonDefaults.colors(selectedColor = Color.Green)
            )
            Text("Male", modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = Gender == "Female",
                onClick = { Gender = "Female" },
                colors = RadioButtonDefaults.colors(selectedColor = Color.Green)
            )
            Text("Female", modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = Gender == "Other",
                onClick = { Gender = "Other" },
                colors = RadioButtonDefaults.colors(selectedColor = Color.Green)
            )
            Text("Other")
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = textFieldAge,
            onValueChange = { textFieldAge = it },
            label = { Text(text = "Student Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                onClick = {
                    showDialog = true
                }
            ) {
                Text(text = "Delete")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Confirm Delete") },
                    text = { Text(text = "Do you want to delete a student : ${data.std_id} ?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                val result = dbHandler.deleteStudent(textFieldID)
                                if (result > 0) {
                                    Toast.makeText(
                                        contextForToast,
                                        "Student deleted successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(Screen.Home.route)
                                } else {
                                    Toast.makeText(
                                        contextForToast,
                                        "Delete Failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                showDialog = false
                            }
                        ) {
                            Text(text = "Yes")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text(text = "No")
                        }
                    }
                )
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val result = dbHandler.updateStudent(
                        Student(textFieldID, textFieldName, Gender, textFieldAge.toInt())
                    )

                    if (result > 0) {
                        Toast.makeText(
                            contextForToast,
                            "The student has been updated successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(contextForToast, "Update Failure", Toast.LENGTH_LONG).show()
                    }
                    navController.navigateUp()
                }


            ) {
                Text(text = "Update")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate(Screen.Home.route)
                }
            ) {
                Text(text = "Cancel")
            }
        }

    }

}