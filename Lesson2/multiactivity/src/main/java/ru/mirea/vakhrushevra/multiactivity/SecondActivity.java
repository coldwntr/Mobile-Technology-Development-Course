package ru.mirea.vakhrushevra.multiactivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private final String TAG = SecondActivity.class.getSimpleName();
    private TextView textViewReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        textViewReceived = findViewById(R.id.textViewReceived);

        String text = getIntent().getStringExtra("key");
        if (text == null || text.isEmpty()) {
            text = "Данные не переданы";
        }
        textViewReceived.setText(text);

        Log.i(TAG, "SecondActivity onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "SecondActivity onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "SecondActivity onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "SecondActivity onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "SecondActivity onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "SecondActivity onDestroy()");
    }
}