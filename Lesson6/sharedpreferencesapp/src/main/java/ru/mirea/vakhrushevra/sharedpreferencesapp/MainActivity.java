package ru.mirea.vakhrushevra.sharedpreferencesapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText editGroup;
    EditText editNumber;
    EditText editFilm;
    Button buttonSave;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editGroup = findViewById(R.id.editGroup);
        editNumber = findViewById(R.id.editNumber);
        editFilm = findViewById(R.id.editFilm);
        buttonSave = findViewById(R.id.buttonSave);

        preferences = getSharedPreferences("mirea_settings", MODE_PRIVATE);

        editGroup.setText(preferences.getString("GROUP", ""));
        editNumber.setText(preferences.getString("NUMBER", ""));
        editFilm.setText(preferences.getString("FILM", ""));

        buttonSave.setOnClickListener(v -> {

            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("GROUP", editGroup.getText().toString());
            editor.putString("NUMBER", editNumber.getText().toString());
            editor.putString("FILM", editFilm.getText().toString());

            editor.apply();
        });
    }
}