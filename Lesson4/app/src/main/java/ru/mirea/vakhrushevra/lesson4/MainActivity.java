package ru.mirea.vakhrushevra.lesson4;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.vakhrushevra.lesson4.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonPlay.setOnClickListener(view -> {
            isPlaying = true;
            binding.textViewStatus.setText("Статус: воспроизведение");
            binding.seekBarMusic.setProgress(60);
        });

        binding.buttonPause.setOnClickListener(view -> {
            if (isPlaying) {
                binding.textViewStatus.setText("Статус: пауза");
            } else {
                binding.textViewStatus.setText("Статус: сначала нажмите Play");
            }
        });

        binding.buttonStop.setOnClickListener(view -> {
            isPlaying = false;
            binding.textViewStatus.setText("Статус: остановлено");
            binding.seekBarMusic.setProgress(0);
        });
    }
}