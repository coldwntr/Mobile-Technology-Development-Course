package ru.mirea.vakhrushevra.mireaproject

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import ru.mirea.vakhrushevra.mireaproject.ui.theme.MireaProjectTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MireaProjectTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Text(text = "ℹ")
                    },
                    label = {
                        Text(text = "Отрасль")
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Text(text = "🌐")
                    },
                    label = {
                        Text(text = "Браузер")
                    }
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Text(text = "⚙")
                    },
                    label = {
                        Text(text = "Worker")
                    }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> DataScreen(innerPadding)
            1 -> BrowserScreen(innerPadding)
            2 -> BackgroundTaskScreen(innerPadding)
        }
    }
}

@Composable
fun DataScreen(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Кибербезопасность",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Кибербезопасность — это отрасль, которая занимается защитой данных, приложений, сетей и устройств от атак, утечек информации и несанкционированного доступа.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        InfoCard(
            title = "Почему эта отрасль важна",
            text = "С каждым годом всё больше сервисов переходит в цифровую среду. Банки, университеты, государственные порталы, медицинские организации и компании хранят огромные объёмы данных. Поэтому защита информации становится одной из ключевых задач современной IT-сферы."
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoCard(
            title = "Основные направления",
            text = "• защита компьютерных сетей\n" +
                    "• безопасность мобильных приложений\n" +
                    "• криптография\n" +
                    "• анализ вредоносного программного обеспечения\n" +
                    "• защита персональных данных\n" +
                    "• расследование инцидентов информационной безопасности"
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoCard(
            title = "Навыки специалиста",
            text = "Специалисту по кибербезопасности нужны знания операционных систем, сетевых протоколов, языков программирования, баз данных, принципов шифрования и методов поиска уязвимостей."
        )
    }
}

@Composable
fun InfoCard(
    title: String,
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun BrowserScreen(innerPadding: PaddingValues) {
    var urlText by remember { mutableStateOf("https://www.mirea.ru") }
    var currentUrl by remember { mutableStateOf("https://www.mirea.ru") }
    var webView: WebView? by remember { mutableStateOf(null) }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text(
            text = "Простой браузер",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Введите адрес сайта или используйте страницу по умолчанию.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = urlText,
            onValueChange = { urlText = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text(text = "Адрес сайта")
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    currentUrl = prepareUrl(urlText)
                }
            ) {
                Text(text = "Открыть")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (webView?.canGoBack() == true) {
                        webView?.goBack()
                    }
                }
            ) {
                Text(text = "Назад")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    loadUrl(currentUrl)
                    webView = this
                }
            },
            update = { view ->
                if (view.url != currentUrl) {
                    view.loadUrl(currentUrl)
                }
            }
        )
    }
}

@Composable
fun BackgroundTaskScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current

    var workId by remember {
        mutableStateOf<UUID?>(null)
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
        WorkInfo.State.ENQUEUED -> "Задача ожидает выполнения. Для запуска нужен интернет."
        WorkInfo.State.RUNNING -> "Задача выполняется. Прогресс: $progress%"
        WorkInfo.State.SUCCEEDED -> {
            workInfo
                ?.outputData
                ?.getString("result")
                ?: "Фоновая задача успешно завершена"
        }
        WorkInfo.State.FAILED -> "Задача завершилась с ошибкой"
        WorkInfo.State.CANCELLED -> "Задача отменена"
        WorkInfo.State.BLOCKED -> "Задача заблокирована"
        null -> "Задача ещё не запускалась"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Фоновая задача",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Этот экран демонстрирует выполнение фоновой задачи через WorkManager. Задача выполняется в отдельном классе BackgroundTaskWorker.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
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

        Spacer(modifier = Modifier.height(24.dp))

        LinearProgressIndicator(
            progress = {
                progress / 100f
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Статус: $statusText",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun prepareUrl(url: String): String {
    val trimmedUrl = url.trim()

    return if (trimmedUrl.startsWith("http://") || trimmedUrl.startsWith("https://")) {
        trimmedUrl
    } else {
        "https://$trimmedUrl"
    }
}