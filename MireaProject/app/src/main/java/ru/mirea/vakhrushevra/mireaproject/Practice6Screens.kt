package ru.mirea.vakhrushevra.mireaproject

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private const val PROFILE_PREFERENCES = "profile_preferences"
private const val KEY_NAME = "name"
private const val KEY_GROUP = "group"
private const val KEY_NUMBER = "number"
private const val KEY_MOVIE = "movie"

@Composable
fun ProfileScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var movie by remember { mutableStateOf("") }
    var savedText by remember { mutableStateOf("Сохранённых данных пока нет") }

    LaunchedEffect(Unit) {
        val preferences = context.getSharedPreferences(
            PROFILE_PREFERENCES,
            Context.MODE_PRIVATE
        )

        name = preferences.getString(KEY_NAME, "") ?: ""
        group = preferences.getString(KEY_GROUP, "") ?: ""
        number = preferences.getString(KEY_NUMBER, "") ?: ""
        movie = preferences.getString(KEY_MOVIE, "") ?: ""
        savedText = buildProfileText(name, group, number, movie)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Профиль",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Экран сохраняет данные пользователя в SharedPreferences и автоматически восстанавливает их при повторном открытии.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        FeatureCard(title = "Данные пользователя") {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Имя")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = group,
                onValueChange = { group = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Группа")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = number,
                onValueChange = { number = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Номер по списку")
                },
                singleLine = true
            )

            OutlinedTextField(
                value = movie,
                onValueChange = { movie = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Любимый фильм или сериал")
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val preferences = context.getSharedPreferences(
                        PROFILE_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                    preferences.edit()
                        .putString(KEY_NAME, name)
                        .putString(KEY_GROUP, group)
                        .putString(KEY_NUMBER, number)
                        .putString(KEY_MOVIE, movie)
                        .apply()

                    savedText = buildProfileText(name, group, number, movie)
                    Toast.makeText(context, "Профиль сохранён", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text(text = "Сохранить")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        FeatureCard(title = "Сохранённые данные") {
            Text(
                text = savedText,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun FileWorkScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val files = remember { mutableStateListOf<String>() }
    var showDialog by remember { mutableStateOf(false) }

    fun refreshFiles() {
        files.clear()
        files.addAll(context.fileList().sorted())
    }

    LaunchedEffect(Unit) {
        refreshFiles()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Работа с файлами",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "По кнопке FloatingActionButton открывается диалог для создания заметки. Файл сохраняется во внутреннем хранилище приложения.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureCard(title = "Созданные файлы") {
                if (files.isEmpty()) {
                    Text(
                        text = "Файлы пока не созданы",
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Text(
                        text = files.joinToString(separator = "\n"),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(
                text = "+",
                modifier = Modifier.size(24.dp),
                textAlign = TextAlign.Center
            )
        }
    }

    if (showDialog) {
        CreateFileDialog(
            onDismiss = {
                showDialog = false
            },
            onSave = { fileName, noteText ->
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use { stream ->
                    stream.write(noteText.toByteArray())
                }

                showDialog = false
                refreshFiles()
                Toast.makeText(context, "Файл сохранён", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun CreateFileDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Создание заметки")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Название файла")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "Текст заметки")
                    },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fileName.isNotBlank()) {
                        onSave(fileName.trim(), noteText)
                    }
                }
            ) {
                Text(text = "Сохранить")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(text = "Отмена")
            }
        }
    )
}

fun buildProfileText(
    name: String,
    group: String,
    number: String,
    movie: String
): String {
    if (name.isBlank() && group.isBlank() && number.isBlank() && movie.isBlank()) {
        return "Сохранённых данных пока нет"
    }

    return "Имя: $name\n" +
            "Группа: $group\n" +
            "Номер по списку: $number\n" +
            "Любимый фильм или сериал: $movie"
}
