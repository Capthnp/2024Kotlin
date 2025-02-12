import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lab10_login.LoginClass
import com.example.lab10_login.Screen
import com.example.lab10_login.StudentAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegisterScreen(navController: NavHostController) {
    val contextForToast = LocalContext.current
    val createClient = StudentAPI.create()

    var studentID by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf("") } // ✅ เพิ่มช่องกรอกชื่อ
    var password by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") } // ✅ ค่าเพศเริ่มต้นเป็น "Male"
    var isButtonEnabled by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Register", fontSize = 25.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ ช่องกรอก Student ID
        OutlinedTextField(
            value = studentID,
            onValueChange = {
                studentID = it
                isButtonEnabled = validateInput(studentID, studentName, password)
            },
            label = { Text(text = "Student ID") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ ช่องกรอกชื่อ (std_name)
        OutlinedTextField(
            value = studentName,
            onValueChange = {
                studentName = it
                isButtonEnabled = validateInput(studentID, studentName, password)
            },
            label = { Text(text = "Name") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))

        // ✅ RadioButton สำหรับเลือกเพศ
        Text(text = "Select Gender", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedGender == "Male",
                onClick = { selectedGender = "Male" },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Green
                )
            )
            Text(text = "Male")
            Spacer(modifier = Modifier.width(5.dp))
            RadioButton(
                selected = selectedGender == "Female",
                onClick = { selectedGender = "Female" },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Green // กำ
                )
            )
            Text(text = "Female")
            Spacer(modifier = Modifier.width(5.dp))
            RadioButton(
                selected = selectedGender == "Other",
                onClick = { selectedGender = "Other" },
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.Green
                )
            )
            Text(text = "Other")

        }
        Spacer(modifier = Modifier.height(16.dp))

        // ✅ ช่องกรอกรหัสผ่าน
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isButtonEnabled = validateInput(studentID, studentName, password)
            },
            label = { Text(text = "Password") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))

        // ✅ ปุ่ม Register
        Button(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()

                // ✅ เรียก API เพื่อลงทะเบียน
                createClient.registerStudent(
                    std_id = studentID,
                    std_name = studentName,
                    std_password = password,
                    std_gender = selectedGender
                ).enqueue(object : Callback<LoginClass> {
                    override fun onResponse(call: Call<LoginClass>, response: Response<LoginClass>) {
                        if (response.isSuccessful) {
                            Toast.makeText(contextForToast, "Successfully Registered", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.Login.route) // ✅ ไปหน้า Login หลังสมัครสำเร็จ
                        } else {
                            Toast.makeText(contextForToast, "Registration Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginClass>, t: Throwable) {
                        Toast.makeText(contextForToast, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            },
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Register")
        }
    }
}

// ✅ ฟังก์ชันตรวจสอบข้อมูลก่อนสมัคร
fun validateInput(studentID: String, studentName: String, password: String): Boolean {
    return studentID.isNotEmpty() && studentName.isNotEmpty() && password.isNotEmpty()
}
