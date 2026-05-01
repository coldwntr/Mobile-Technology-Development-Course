package ru.mirea.vakhrushevra.mireaproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager

@Composable
fun BackgroundTaskScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current

    var workId by remember {
        mutableStateOf<java.util.UUID?>(null)
    }

    val workInfo by workId?.let { id ->
        WorkManager
            .getInstance(context)
            .getWorkInfoByIdLiveData(id)
            .observeAsState()
    } ?: remember {
        mutableStateOf(null)
    }

    val progress = workInfo
        ?.progress
        ?.getInt("progress", 0)
        ?: 0

    val statusText = when (workInfo?.state) {
        WorkInfo.State.ENQUEUED -> "Задача ожидает выполнения. Нужен интернет."
        WorkInfo.State.RUNNING -> "Задача выполняется. Прогресс: $progress%"
        WorkInfo.State.SUCCEEDED -> {
            workInfo
                ?.outputData
                ?.getString("result")
                ?: "Задача успешно завершена"
        }
        WorkInfo.State.FAILED -> "Задача завершилась с ошибкой"
        WorkInfo.State.CANCELLED -> "Задача отменена"
        WorkInfo.State.BLOCKED -> "Задача заблокирована"
        null -> "Задача ещё не запускалась"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Фоновая задача",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Этот экран запускает фоновую задачу через WorkManager. Задача выполняется в классе BackgroundTaskWorker.",
            textAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            onClick = {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val workRequest = OneTimeWorkRequestBuilder<BackgroundTaskWorker>()
                    .setConstraints(constraints)
                    .build()

                WorkManager
                    .getInstance(context)
                    .enqueue(workRequest)

                workId = workRequest.id
            }
        ) {
            Text(text = "Запустить фоновую задачу")
        }

        LinearProgressIndicator(
            progress = progress / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )

        Text(
            modifier = Modifier.padding(top = 24.dp),
            text = "Статус: $statusText",
            textAlign = TextAlign.Center
        )
    }
}