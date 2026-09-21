package com.example.coroutines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.coroutines.ui.CoroutineDemoScreen
import com.example.coroutines.ui.theme.KotlinCoroutinesTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinCoroutinesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CoroutineDemoScreen()
                }
            }
        }
    }
}
