package ru.mirea.vakhrushevra.mireaproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.mirea.vakhrushevra.mireaproject.network.NetworkApi
import ru.mirea.vakhrushevra.mireaproject.network.NetworkInfo

@Composable
fun NetworkScreen(innerPadding: PaddingValues) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var networkInfo by remember { mutableStateOf<NetworkInfo?>(null) }
    val coroutineScope = rememberCoroutineScope()

    fun loadData() {
        isLoading = true
        errorMessage = null

        coroutineScope.launch {
            try {
                networkInfo = NetworkApi.loadNetworkInfo()
            } catch (exception: Exception) {
                errorMessage = exception.localizedMessage ?: "Ошибка загрузки данных"
                networkInfo = null
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Сетевые данные",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Экран получает IP и местоположение через ipwho.is, а погоду по координатам через Open-Meteo.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Загрузка данных...")
                }
            }

            errorMessage != null -> {
                FeatureCard(title = "Ошибка") {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { loadData() }
                ) {
                    Text(text = "Повторить")
                }
            }

            networkInfo != null -> {
                val info = networkInfo!!

                FeatureCard(title = "Местоположение по IP") {
                    NetworkInfoRow(label = "IP", value = info.ip)
                    NetworkInfoRow(label = "Город", value = info.city)
                    NetworkInfoRow(label = "Регион", value = info.region)
                    NetworkInfoRow(label = "Страна", value = info.country)
                }

                Spacer(modifier = Modifier.height(16.dp))

                FeatureCard(title = "Погода по координатам") {
                    NetworkInfoRow(label = "Температура", value = info.temperature)
                    NetworkInfoRow(label = "Скорость ветра", value = info.windSpeed)
                    NetworkInfoRow(label = "Описание", value = info.weatherDescription)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { loadData() }
                ) {
                    Text(text = "Обновить")
                }
            }
        }
    }
}

@Composable
private fun NetworkInfoRow(
    label: String,
    value: String
) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyLarge
    )
}
