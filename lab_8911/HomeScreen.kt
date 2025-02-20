import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.compose.ui.Alignment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import com.example.lab_8911.DatabaseHelper
import com.example.lab_8911.Screen
import com.example.lab_8911.Student


@Composable
fun HomeScreen(navController: NavHostController) {
    var studentItemsList = remember { mutableStateListOf<Student>() }
    val contextForToast = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    var studentCount by remember { mutableStateOf(0) }

    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    var textFieldID by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        showAllData(studentItemsList, contextForToast) { count ->
            studentCount = count
        }
    }

    Column {
        Spacer(modifier = Modifier.height(height = 15.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Search:", fontSize = 20.sp)

            OutlinedTextField(
                value = textFieldID,
                onValueChange = { textFieldID = it },
                modifier = Modifier
                    .width(230.dp)
                    .padding(5.dp),
                label = { Text("Student ID") }
            )

            Button(
                onClick = {
                    if (textFieldID.trim().isEmpty()) {
                        showAllData(studentItemsList, contextForToast) { count ->
                            studentCount = count
                        }
                    } else {
                        searchStudent(textFieldID, studentItemsList, contextForToast) { count ->
                            studentCount = count
                        }
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "search")
            }

        }
        Spacer(modifier = Modifier.height(height = 7.dp))
        Row(
            Modifier.fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.85f)) {
                Text(
                    text = "Student Lists :  $studentCount ",
                    fontSize = 25.sp
                )
            }
            Button(onClick = {
                navController.navigate(Screen.Insert.route)
            }) {
                Text(text = "Add Student")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(
                items = studentItemsList
            ) { index, item ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(130.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    shape = RoundedCornerShape(corner = CornerSize(16.dp)),
                    onClick = {
                        Toast.makeText(
                            contextForToast, "Click on ${item.std_name}.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(Dp(value = 130f))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ID : ${item.std_id}\n" +
                                    "Name : ${item.std_name}\n" +
                                    "Gender : ${item.std_gender}\n" +
                                    "Age : ${item.std_age}",
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "data",
                                    Student(item.std_id, item.std_name, item.std_gender, item.std_age)
                                )
                                navController.navigate(Screen.Edit.route)
                            }
                        ) {
                            Text(text = "Edit/Delete",  modifier = Modifier)
                        }
                    }
                }
            }
        }
    }
}

fun showAllData(studentItemsList: MutableList<Student>, context: Context, updateStudentCount: (Int) -> Unit) {
    val dbHandler = DatabaseHelper.getInstance(context)
    studentItemsList.clear()
    studentItemsList.addAll(dbHandler.getAllStudents())

    updateStudentCount(dbHandler.countStudents())
}


fun searchStudent(stdId: String, studentItemsList: MutableList<Student>, context: Context, updateStudentCount: (Int) -> Unit) {
    val dbHandler = DatabaseHelper.getInstance(context)
    val student = dbHandler.getStudent(stdId)

    studentItemsList.clear()
    if (student != null) {
        studentItemsList.add(student)
        updateStudentCount(1)
    } else {
        updateStudentCount(0)
        Toast.makeText(context, "Student ID is not found", Toast.LENGTH_SHORT).show() // ✅ แสดง Toast แจ้งเตือน
    }
}

