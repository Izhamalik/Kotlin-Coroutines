package com.example.coroutines.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.coroutines.examples.CoroutineCatalog
import com.example.coroutines.examples.CoroutineExample
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val LOG_TAG = "CoroutineDemo"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoroutineDemoScreen() {
    val scope = rememberCoroutineScope()
    var selected by remember { mutableStateOf<CoroutineExample?>(null) }
    var logs by remember { mutableStateOf(listOf<String>()) }
    var running by remember { mutableStateOf(false) }
    var activeJob by remember { mutableStateOf<Job?>(null) }

    fun append(line: String) {
        Log.d(LOG_TAG, line)
        logs = logs + line
    }

    fun runExample(example: CoroutineExample) {
        activeJob?.cancel()
        selected = example
        logs = emptyList()
        running = true
        activeJob = scope.launch {
            append("── ${example.title} ──")
            try {
                example.run { msg -> append(msg) }
                append("── completed ──")
            } catch (e: Exception) {
                append("── failed: ${e.message} ──")
            } finally {
                running = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kotlin Coroutines Lab") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = "Tap Run on any concept. Output appears below and in Logcat ($LOG_TAG).",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(CoroutineCatalog.all, key = { it.id }) { example ->
                    ExampleCard(
                        example = example,
                        selected = selected?.id == example.id,
                        enabled = !running,
                        onRun = { runExample(example) }
                    )
                }
            }

            Text(
                text = "Output",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp, max = 220.dp)
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                val scroll = rememberScrollState()
                Text(
                    text = if (logs.isEmpty()) "Run an example to see logs…" else logs.joinToString("\n"),
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scroll)
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun ExampleCard(
    example: CoroutineExample,
    selected: Boolean,
    enabled: Boolean,
    onRun: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(example.title, style = MaterialTheme.typography.titleSmall)
                Text(
                    example.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = onRun, enabled = enabled) {
                Text("Run")
            }
        }
    }
}
