package ru.mirea.vakhrushevra.internalfilestorage;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button buttonSave;

    String fileName = "history.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(v -> {

            String text = editText.getText().toString();

            try {

                FileOutputStream outputStream =
                        openFileOutput(fileName, Context.MODE_PRIVATE);

                outputStream.write(text.getBytes());

                outputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}