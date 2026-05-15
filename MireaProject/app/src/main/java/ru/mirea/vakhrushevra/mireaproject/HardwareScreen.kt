package ru.mirea.vakhrushevra.mireaproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HardwareScreen(innerPadding: PaddingValues) {
    var selectedSection by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Аппаратная часть",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Здесь собраны задания практики №5: датчик устройства, работа с камерой и запись звука через микрофон.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionButton(
                title = "Датчик",
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                modifier = Modifier.weight(1f)
            )

            SectionButton(
                title = "Камера",
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                modifier = Modifier.weight(1f)
            )

            SectionButton(
                title = "Микрофон",
                selected = selectedSection == 2,
                onClick = { selectedSection = 2 },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (selectedSection) {
            0 -> SensorSection()
            1 -> CameraSection()
            2 -> MicrophoneSection()
        }
    }
}

@Composable
fun SectionButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text = title)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text = title)
        }
    }
}

@Composable
fun SensorSection() {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    var xValue by remember { mutableFloatStateOf(0f) }
    var yValue by remember { mutableFloatStateOf(0f) }
    var zValue by remember { mutableFloatStateOf(0f) }

    DisposableEffect(accelerometer) {
        if (accelerometer == null) {
            onDispose { }
        } else {
            val sensorListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    xValue = event.values[0]
                    yValue = event.values[1]
                    zValue = event.values[2]
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                }
            }

            sensorManager.registerListener(
                sensorListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )

            onDispose {
                sensorManager.unregisterListener(sensorListener)
            }
        }
    }

    val phonePosition = when {
        accelerometer == null -> "Акселерометр не найден"
        zValue > 7f -> "Телефон лежит экраном вверх"
        zValue < -7f -> "Телефон лежит экраном вниз"
        xValue > 4f -> "Телефон наклонён влево"
        xValue < -4f -> "Телефон наклонён вправо"
        yValue > 4f -> "Телефон наклонён вниз"
        yValue < -4f -> "Телефон наклонён вверх"
        else -> "Телефон почти в ровном положении"
    }

    FeatureCard(title = "Акселерометр") {
        Text(
            text = "Экран получает значения датчика ускорения и определяет примерное положение телефона.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (accelerometer == null) {
            Text(
                text = "На этом устройстве акселерометр отсутствует, поэтому данные датчика недоступны.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(text = "X: ${formatSensorValue(xValue)}")
            Text(text = "Y: ${formatSensorValue(yValue)}")
            Text(text = "Z: ${formatSensorValue(zValue)}")

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Определение положения: $phonePosition",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Для акселерометра отдельное разрешение не требуется.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CameraSection() {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoMessage by remember {
        mutableStateOf("Фото ещё не сделано")
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSaved ->
        photoMessage = if (isSaved && photoUri != null) {
            "Фото сохранено в папку приложения"
        } else {
            photoUri = null
            "Съёмка отменена или фото не сохранилось"
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val imageFile = createImageFile(context)

            if (imageFile == null) {
                photoMessage = "Не удалось подготовить файл для фотографии"
            } else {
                val imageUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    imageFile
                )
                photoUri = imageUri
                takePictureLauncher.launch(imageUri)
            }
        } else {
            photoMessage = "Разрешение на камеру не выдано"
        }
    }

    FeatureCard(title = "Камера") {
        Text(
            text = "Фото делается через системное приложение камеры, а файл сохраняется в директорию приложения.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Статус разрешения: ${permissionText(context, Manifest.permission.CAMERA)}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (hasPermission(context, Manifest.permission.CAMERA)) {
                    val imageFile = createImageFile(context)

                    if (imageFile == null) {
                        photoMessage = "Не удалось подготовить файл для фотографии"
                    } else {
                        val imageUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            imageFile
                        )
                        photoUri = imageUri
                        takePictureLauncher.launch(imageUri)
                    }
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        ) {
            Text(text = "Сделать фото")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = photoMessage,
            style = MaterialTheme.typography.bodyMedium
        )

        if (photoUri != null) {
            Spacer(modifier = Modifier.height(16.dp))

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                factory = { imageContext ->
                    ImageView(imageContext).apply {
                        adjustViewBounds = true
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                },
                update = { imageView ->
                    imageView.setImageURI(photoUri)
                }
            )
        }
    }
}

@Composable
fun MicrophoneSection() {
    val context = LocalContext.current
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var audioMessage by remember {
        mutableStateOf("Запись ещё не создана")
    }

    fun stopPlayback() {
        player?.apply {
            stop()
            release()
        }
        player = null
        isPlaying = false
    }

    fun stopRecording() {
        try {
            recorder?.stop()
        } catch (_: Exception) {
        }

        recorder?.release()
        recorder = null
        isRecording = false
    }

    val microphonePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val audioFile = createAudioFile(context)

            if (audioFile == null) {
                audioMessage = "Не удалось создать файл для записи"
                return@rememberLauncherForActivityResult
            }

            stopPlayback()

            try {
                val mediaRecorder = createMediaRecorder(context)
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mediaRecorder.setOutputFile(audioFile.absolutePath)
                mediaRecorder.prepare()
                mediaRecorder.start()

                recorder = mediaRecorder
                audioFilePath = audioFile.absolutePath
                isRecording = true
                audioMessage = "Идёт запись звука"
            } catch (e: Exception) {
                recorder?.release()
                recorder = null
                isRecording = false
                audioMessage = "Не удалось начать запись: ${e.message}"
            }
        } else {
            audioMessage = "Разрешение на микрофон не выдано"
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isRecording) {
                stopRecording()
            }

            if (isPlaying) {
                stopPlayback()
            }
        }
    }

    FeatureCard(title = "Микрофон") {
        Text(
            text = "Экран записывает звук через MediaRecorder и воспроизводит последнюю запись через MediaPlayer.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Статус разрешения: ${permissionText(context, Manifest.permission.RECORD_AUDIO)}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (isRecording) {
                        stopRecording()
                        audioMessage = "Запись остановлена и сохранена в папку приложения"
                    } else {
                        if (!hasPermission(context, Manifest.permission.RECORD_AUDIO)) {
                            microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            return@Button
                        }

                        val audioFile = createAudioFile(context)

                        if (audioFile == null) {
                            audioMessage = "Не удалось создать файл для записи"
                            return@Button
                        }

                        stopPlayback()

                        try {
                            val mediaRecorder = createMediaRecorder(context)
                            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                            mediaRecorder.setOutputFile(audioFile.absolutePath)
                            mediaRecorder.prepare()
                            mediaRecorder.start()

                            recorder = mediaRecorder
                            audioFilePath = audioFile.absolutePath
                            isRecording = true
                            audioMessage = "Идёт запись звука"
                        } catch (e: Exception) {
                            recorder?.release()
                            recorder = null
                            isRecording = false
                            audioMessage = "Не удалось начать запись: ${e.message}"
                        }
                    }
                }
            ) {
                Text(
                    text = if (isRecording) {
                        "Остановить запись"
                    } else {
                        "Записать"
                    }
                )
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val path = audioFilePath

                    if (path.isNullOrEmpty()) {
                        audioMessage = "Сначала нужно записать аудио"
                        return@Button
                    }

                    if (isRecording) {
                        audioMessage = "Сначала остановите запись"
                        return@Button
                    }

                    if (isPlaying) {
                        stopPlayback()
                        audioMessage = "Воспроизведение остановлено"
                        return@Button
                    }

                    try {
                        val mediaPlayer = MediaPlayer()
                        mediaPlayer.setDataSource(path)
                        mediaPlayer.prepare()
                        mediaPlayer.setOnCompletionListener {
                            it.release()
                            player = null
                            isPlaying = false
                            audioMessage = "Воспроизведение завершено"
                        }
                        mediaPlayer.start()

                        player = mediaPlayer
                        isPlaying = true
                        audioMessage = "Идёт воспроизведение записи"
                    } catch (e: Exception) {
                        player?.release()
                        player = null
                        isPlaying = false
                        audioMessage = "Не удалось воспроизвести запись: ${e.message}"
                    }
                }
            ) {
                Text(
                    text = if (isPlaying) {
                        "Остановить"
                    } else {
                        "Воспроизвести"
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = audioMessage,
            style = MaterialTheme.typography.bodyMedium
        )

        if (!audioFilePath.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Последний файл: ${File(audioFilePath!!).name}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    content: @Composable () -> Unit
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

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                content()
            }
        }
    }
}

fun hasPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun permissionText(context: Context, permission: String): String {
    return if (hasPermission(context, permission)) {
        "разрешено"
    } else {
        "не разрешено"
    }
}

fun formatSensorValue(value: Float): String {
    return String.format(Locale.getDefault(), "%.2f", value)
}

fun createImageFile(context: Context): File? {
    val picturesDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        ?: return null
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    return File(picturesDirectory, "camera_$timeStamp.jpg")
}

fun createAudioFile(context: Context): File? {
    val musicDirectory = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        ?: return null
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

    return File(musicDirectory, "record_$timeStamp.m4a")
}

fun createMediaRecorder(context: Context): MediaRecorder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
    }
}
