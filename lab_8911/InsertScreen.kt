package com.example.lab_8911
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController



@Composable
fun InsertScreen(navController: NavHostController) {
    var textFieldID by remember { mutableStateOf("") }
    var textFieldName by remember { mutableStateOf("") }

    var textFieldAge by remember { mutableStateOf("") }
    val contextForToast = LocalContext.current.applicationContext
    var selectedGender by remember { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(false) }

    val dbHandler = DatabaseHelper.getInstance(contextForToast)
    dbHandler.writableDatabase

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Insert New Student",
            fontSize = 25.sp
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = textFieldID,

            onValueChange = { textFieldID = it
                isButtonEnabled = validateInput(textFieldID, textFieldName, selectedGender, textFieldAge)},
            label = { Text(text = "Student ID ${textFieldID}") }
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = textFieldName,
            onValueChange = { textFieldName = it },
            label = { Text(text = "Student Name") }
        )
        Text("Gender : $selectedGender")
        Row(

            modifier = Modifier
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(selected = selectedGender == "Male", onClick = { selectedGender = "Male" })
            Text("Male", modifier = Modifier.padding(end = 16.dp))
            RadioButton(
                selected = selectedGender == "Female",
                onClick = { selectedGender = "Female" })
            Text("Female", modifier = Modifier.padding(end = 16.dp))
            RadioButton(
                selected = selectedGender == "Other",
                onClick = { selectedGender = "Other" })
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
            horizontalArrangement = Arrangement.Center
        ) {
            Button(modifier = Modifier.width(130.dp), onClick = {
                val result = dbHandler.insertStudent(
                    Student(textFieldID, textFieldName, selectedGender, textFieldAge.toInt())
                )

                if (result > -1) {
                    Toast.makeText(
                        contextForToast,
                        "The student is inserted successfully",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(contextForToast, "Insert Failure", Toast.LENGTH_LONG).show()
                }
                navController.navigateUp()
            }) {
                Text(text = "Save")
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(modifier = Modifier.width(130.dp), onClick = {
                textFieldID = ""
                textFieldName = ""
                navController.navigate(Screen.Home.route)
            }) {
                Text(text = "Cancel")
            }

        }
    }
}
@Composable
fun KindRadioGroupUsage(): String {
    val kinds = listOf("Male", "Female", "Other")
    val (selected, setSelected) = remember { mutableStateOf("") }

    Text(
        text = "Student Gender :",
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
            .padding(start = 16.dp, top = 10.dp),
    )

    Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp)) {
        MyRadioGroup(
            items = kinds,
            selected = selected,
            setSelected = setSelected
        )
    }

    return selected
}

@Composable
fun MyRadioGroup(
    items: List<String>,
    selected: String,
    setSelected: (selected: String) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == item,
                    onClick = {
                        setSelected(item)
                    },
                    enabled = true,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Green
                    )
                )
                Text(text = item, modifier = Modifier.padding(start = 5.dp))
            }
        }
    }
}

fun validateInput(studentID: String, name: String, gender: String, age: String): Boolean {
    return studentID.isNotEmpty() && name.isNotEmpty() &&
            gender.isNotEmpty() && age.isNotEmpty()
}

