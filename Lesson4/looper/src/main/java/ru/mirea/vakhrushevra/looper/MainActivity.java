package ru.mirea.vakhrushevra.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.vakhrushevra.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyLooper myLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Handler mainThreadHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                String result = msg.getData().getString("RESULT");

                Log.d("MainActivity", "Результат получен в главном потоке: " + result);

                binding.textViewResult.setText(
                        "Результат получен из MyLooper:\n" +
                                result + "\n\n" +
                                "Подробности смотри в Logcat по тегам MyLooper и MainActivity."
                );
            }
        };

        myLooper = new MyLooper(mainThreadHandler);
        myLooper.start();

        binding.textViewResult.setText(
                "Введите возраст и профессию.\n" +
                        "Для быстрой проверки введи возраст 3, иначе ждать придётся долго."
        );

        binding.buttonSend.setOnClickListener(view -> sendMessageToLooper());
    }

    private void sendMessageToLooper() {
        String ageText = binding.editTextAge.getText().toString();
        String job = binding.editTextJob.getText().toString();

        if (ageText.isEmpty()) {
            binding.textViewResult.setText("Введите возраст");
            return;
        }

        if (job.isEmpty()) {
            binding.textViewResult.setText("Введите профессию или работу");
            return;
        }

        int age = Integer.parseInt(ageText);

        if (myLooper.mHandler == null) {
            binding.textViewResult.setText(
                    "Looper ещё не готов. Подождите секунду и нажмите кнопку ещё раз."
            );
            Log.d("MainActivity", "mHandler пока null. Looper ещё создаётся.");
            return;
        }

        Message message = Message.obtain();

        Bundle bundle = new Bundle();
        bundle.putInt("AGE", age);
        bundle.putString("JOB", job);

        message.setData(bundle);

        myLooper.mHandler.sendMessage(message);

        binding.textViewResult.setText(
                "Сообщение отправлено в MyLooper.\n" +
                        "Возраст: " + age + "\n" +
                        "Работа: " + job + "\n\n" +
                        "Ждём результат..."
        );

        Log.d("MainActivity", "Сообщение отправлено в MyLooper");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (myLooper != null && myLooper.mHandler != null) {
            myLooper.mHandler.getLooper().quitSafely();
            Log.d("MainActivity", "Looper остановлен");
        }
    }
}