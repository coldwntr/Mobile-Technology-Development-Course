package ru.mirea.vakhrushevra.data_thread;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import ru.mirea.vakhrushevra.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final StringBuilder resultText = new StringBuilder();

    private void addLine(String text) {
        resultText.append(text).append("\n");
        binding.textViewInfo.setText(resultText.toString());
        Log.d("DataThread", text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textViewInfo.setText(
                "Этот пример показывает разницу между:\n" +
                        "1. runOnUiThread\n" +
                        "2. View.post\n" +
                        "3. View.postDelayed\n\n" +
                        "Нажмите кнопку."
        );

        binding.buttonStart.setOnClickListener(view -> startThreadExample());
    }

    private void startThreadExample() {
        resultText.setLength(0);

        addLine("Старт. Сейчас мы в главном UI-потоке.");
        addLine("Имя текущего потока: " + Thread.currentThread().getName());

        final Runnable runn1 = new Runnable() {
            @Override
            public void run() {
                addLine("runn1: выполнен через runOnUiThread");
            }
        };

        final Runnable runn2 = new Runnable() {
            @Override
            public void run() {
                addLine("runn2: выполнен через textView.post");
            }
        };

        final Runnable runn3 = new Runnable() {
            @Override
            public void run() {
                addLine("runn3: выполнен через textView.postDelayed через 2 секунды");
            }
        };

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("DataThread", "Фоновый поток запущен: " + Thread.currentThread().getName());

                try {
                    TimeUnit.SECONDS.sleep(2);

                    runOnUiThread(runn1);

                    TimeUnit.SECONDS.sleep(1);

                    binding.textViewInfo.postDelayed(runn3, 2000);

                    binding.textViewInfo.post(runn2);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        addLine("Фоновый поток запущен. UI не завис.");
    }
}