package ru.mirea.vakhrushevra.timeservice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TimeService";

    private TextView textViewDate;
    private TextView textViewTime;
    private Button buttonGetTime;

    private final String host = "time.nist.gov";
    private final int port = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewDate = findViewById(R.id.textViewDate);
        textViewTime = findViewById(R.id.textViewTime);
        buttonGetTime = findViewById(R.id.buttonGetTime);

        buttonGetTime.setOnClickListener(v -> {
            GetTimeTask task = new GetTimeTask();
            task.execute();
        });
    }

    private class GetTimeTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textViewDate.setText("Загружаем...");
            textViewTime.setText("");
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";

            try {
                Socket socket = new Socket(host, port);

                BufferedReader reader = SocketUtils.getReader(socket);

                reader.readLine();
                result = reader.readLine();

                socket.close();

            } catch (IOException e) {
                Log.e(TAG, "Ошибка подключения", e);
                result = "error";
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("error") || result.isEmpty()) {
                textViewDate.setText("Ошибка получения времени");
                textViewTime.setText("Проверь интернет");
                return;
            }

            String[] parts = result.split(" ");

            if (parts.length >= 3) {
                String date = parts[1];
                String time = parts[2];

                textViewDate.setText("Дата: " + date);
                textViewTime.setText("Время UTC: " + time);
            } else {
                textViewDate.setText("Ответ сервера:");
                textViewTime.setText(result);
            }
        }
    }
}