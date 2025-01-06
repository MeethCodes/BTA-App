package com.example.my_bta_app


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp { enteredData ->
                // You can access enteredData here
                // For example, you can print it to Logcat
                Log.d("MainActivity", "Entered data: $enteredData")
                // Call the function to send data to API
                sendToApi(enteredData)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataEntryScreen(onSubmitClicked: (Map<String, Int>) -> Unit) {
    var highBP by remember { mutableStateOf("") }
    var highChol by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf("") }
    var stroke by remember { mutableStateOf("") }
    var heartDiseaseOrAttack by remember { mutableStateOf("") }
    var physActivity by remember { mutableStateOf("") }
    var hvyAlcoholConsump by remember { mutableStateOf("") }
    var genHlth by remember { mutableStateOf("") }
    var diffWalk by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    val submitEnabled = listOf(
        highBP, highChol, bmi, stroke, heartDiseaseOrAttack,
        physActivity, hvyAlcoholConsump, genHlth, diffWalk, age
    ).all { it.isNotEmpty() }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = highBP,
            onValueChange = { if (it.isDigitsOnly()) highBP = it else highBP = "" },
            label = { Text("HighBP") }
        )

        TextField(
            value = highChol,
            onValueChange = { if (it.isDigitsOnly()) highChol = it else highChol = "" },
            label = { Text("HighChol") }
        )

        TextField(
            value = bmi,
            onValueChange = { if (it.isDigitsOnly()) bmi = it else bmi = "" },
            label = { Text("BMI") }
        )

        TextField(
            value = stroke,
            onValueChange = { if (it.isDigitsOnly()) stroke = it else stroke = "" },
            label = { Text("Stroke") }
        )

        TextField(
            value = heartDiseaseOrAttack,
            onValueChange = { if (it.isDigitsOnly()) heartDiseaseOrAttack = it else heartDiseaseOrAttack = "" },
            label = { Text("HeartDiseaseorAttack") }
        )

        TextField(
            value = physActivity,
            onValueChange = { if (it.isDigitsOnly()) physActivity = it else physActivity = "" },
            label = { Text("PhysActivity") }
        )

        TextField(
            value = hvyAlcoholConsump,
            onValueChange = { if (it.isDigitsOnly()) hvyAlcoholConsump = it else hvyAlcoholConsump = "" },
            label = { Text("HvyAlcoholConsump") }
        )

        TextField(
            value = genHlth,
            onValueChange = { if (it.isDigitsOnly()) genHlth = it else genHlth = "" },
            label = { Text("GenHlth") }
        )

        TextField(
            value = diffWalk,
            onValueChange = { if (it.isDigitsOnly()) diffWalk = it else diffWalk = "" },
            label = { Text("DiffWalk") }
        )

        TextField(
            value = age,
            onValueChange = { if (it.isDigitsOnly()) age = it else age = "" },
            label = { Text("Age") }
        )


        Button(
            onClick = {
                onSubmitClicked(
                    mapOf(
                        "HighBP" to highBP.toInt(),
                        "HighChol" to highChol.toInt(),
                        "BMI" to bmi.toInt(),
                        "Stroke" to stroke.toInt(),
                        "HeartDiseaseorAttack" to heartDiseaseOrAttack.toInt(),
                        "PhysActivity" to physActivity.toInt(),
                        "HvyAlcoholConsump" to hvyAlcoholConsump.toInt(),
                        "GenHlth" to genHlth.toInt(),
                        "DiffWalk" to diffWalk.toInt(),
                        "Age" to age.toInt()
                    )
                )
            },
            enabled = submitEnabled
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun MyApp(onSubmitClicked: (Map<String, Int>) -> Unit) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DataEntryScreen { data ->
                onSubmitClicked(data)
            }
        }
    }
}

fun sendToApi(enteredData: Map<String, Int>) {
    val url = URL("https://checkdiabetes.onrender.com/predict")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-Type", "application/json; utf-8")
    connection.setRequestProperty("Accept", "application/json")
    connection.doOutput = true

    val jsonInputString = """
        {
            "HighBP": ${enteredData["HighBP"]},
            "HighChol": ${enteredData["HighChol"]},
            "BMI": ${enteredData["BMI"]},
            "Stroke": ${enteredData["Stroke"]},
            "HeartDiseaseorAttack": ${enteredData["HeartDiseaseorAttack"]},
            "PhysActivity": ${enteredData["PhysActivity"]},
            "HvyAlcoholConsump": ${enteredData["HvyAlcoholConsump"]},
            "GenHlth": ${enteredData["GenHlth"]},
            "DiffWalk": ${enteredData["DiffWalk"]},
            "Age": ${enteredData["Age"]}
        }
    """.trimIndent()

    val os = connection.outputStream
    val input = jsonInputString.toByteArray(Charset.defaultCharset())
    os.write(input, 0, input.size)

    val responseCode = connection.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
        val response = connection.inputStream.bufferedReader().readText()
        Log.d("APIResponse", response)
    } else {
        val errorResponse = connection.errorStream.bufferedReader().readText()
        Log.e("APIResponse", "HTTP Error: $responseCode, $errorResponse")
    }
    os.close()
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewDataEntryScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        DataEntryScreen { }
    }
}