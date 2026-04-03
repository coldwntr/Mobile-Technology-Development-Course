package ru.mirea.vakhrushevra.toastapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextToast = findViewById(R.id.editTextToast);
    }

    public void onClickCountSymbols(View view) {
        String text = editTextToast.getText().toString();
        int count = text.length();

        Toast.makeText(
                getApplicationContext(),
                "СТУДЕНТ № 4 ГРУППА БСБО-08-23 Количество символов - " + count,
                Toast.LENGTH_SHORT
        ).show();
    }
}