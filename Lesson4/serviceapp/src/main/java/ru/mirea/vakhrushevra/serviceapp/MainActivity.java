package ru.mirea.vakhrushevra.serviceapp;


import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ru.mirea.vakhrushevra.serviceapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int PERMISSION_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkNotificationPermission();

        binding.buttonStartService.setOnClickListener(view -> {
            Intent serviceIntent = new Intent(MainActivity.this, PlayerService.class);

            ContextCompat.startForegroundService(MainActivity.this, serviceIntent);

            binding.textViewStatus.setText("Статус: сервис запущен, музыка играет");
            Log.d("MainActivity", "Service started");
        });

        binding.buttonStopService.setOnClickListener(view -> {
            Intent serviceIntent = new Intent(MainActivity.this, PlayerService.class);

            stopService(serviceIntent);

            binding.textViewStatus.setText("Статус: сервис остановлен");
            Log.d("MainActivity", "Service stopped");
        });
    }

    private void checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Разрешение на уведомления уже получено");
            } else {
                Log.d("MainActivity", "Запрашиваем разрешение на уведомления");

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{POST_NOTIFICATIONS},
                        PERMISSION_CODE
                );
            }
        }
    }
}