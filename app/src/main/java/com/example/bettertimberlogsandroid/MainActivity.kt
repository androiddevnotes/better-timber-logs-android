package com.example.bettertimberlogsandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bettertimberlogsandroid.ui.theme.BetterTimberLogsAndroidTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("Hello MainActivity")
        val sampleJson = """
    {
        "name": "John Doe",
        "age": 30,
        "isStudent": false,
        "courses": [
            "mathematics",
            "history",
            "chemistry"
        ],
        "address": {
            "street": "123 Main St",
            "city": "New York",
            "state": "NY",
            "postalCode": "10001"
        }
    }
""".trimIndent()

        Timber.d(sampleJson)

        setContent {
            BetterTimberLogsAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BetterTimberLogsAndroidTheme {
        Greeting("Android")
    }
}