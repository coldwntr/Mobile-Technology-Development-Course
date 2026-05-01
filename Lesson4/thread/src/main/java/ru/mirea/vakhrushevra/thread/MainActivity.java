package ru.mirea.vakhrushevra.thread;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import ru.mirea.vakhrushevra.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Thread mainThread = Thread.currentThread();

        binding.textViewThreadInfo.setText("Имя текущего потока: " + mainThread.getName());

        mainThread.setName("Группа: БСБО-08-23, номер по списку: 5, любимый фильм: Молчание Ягнят");

        binding.textViewThreadInfo.append("\nНовое имя потока: " + mainThread.getName());

        Log.d(MainActivity.class.getSimpleName(), "Stack: " + Arrays.toString(mainThread.getStackTrace()));
        Log.d(MainActivity.class.getSimpleName(), "Group: " + mainThread.getThreadGroup());

        binding.buttonCalculate.setOnClickListener(view -> {
            String lessonsText = binding.editTextLessons.getText().toString();
            String daysText = binding.editTextDays.getText().toString();

            if (lessonsText.isEmpty() || daysText.isEmpty()) {
                binding.textViewResult.setText("Введите оба значения");
                return;
            }

            double lessons = Double.parseDouble(lessonsText);
            double days = Double.parseDouble(daysText);

            if (days == 0) {
                binding.textViewResult.setText("Количество дней не может быть 0");
                return;
            }

            binding.textViewResult.setText("Идёт вычисление в фоновом потоке...");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    int numberThread = counter++;

                    Log.d("ThreadProject", "Запущен поток № " + numberThread);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    double average = lessons / days;

                    Log.d("ThreadProject", "Выполнен поток № " + numberThread);
                    Log.d("ThreadProject", "Среднее количество пар в день: " + average);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.textViewResult.setText(
                                    String.format("Среднее количество пар в день: %.2f", average)
                            );
                        }
                    });
                }
            }).start();
        });
    }
}