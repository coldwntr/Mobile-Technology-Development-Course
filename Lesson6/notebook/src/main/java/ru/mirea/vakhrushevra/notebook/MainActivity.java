package ru.mirea.vakhrushevra.notebook;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    EditText editFileName;
    EditText editQuote;

    Button buttonSave;
    Button buttonLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editFileName = findViewById(R.id.editFileName);
        editQuote = findViewById(R.id.editQuote);

        buttonSave = findViewById(R.id.buttonSave);
        buttonLoad = findViewById(R.id.buttonLoad);

        buttonSave.setOnClickListener(v -> saveFile());

        buttonLoad.setOnClickListener(v -> loadFile());
    }

    private void saveFile() {

        try {

            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS);

            File file = new File(path,
                    editFileName.getText().toString());

            FileOutputStream stream =
                    new FileOutputStream(file);

            OutputStreamWriter writer =
                    new OutputStreamWriter(stream);

            writer.write(editQuote.getText().toString());

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFile() {

        try {

            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS);

            File file = new File(path,
                    editFileName.getText().toString());

            FileInputStream stream =
                    new FileInputStream(file);

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(stream));

            String text = reader.readLine();

            editQuote.setText(text);

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}