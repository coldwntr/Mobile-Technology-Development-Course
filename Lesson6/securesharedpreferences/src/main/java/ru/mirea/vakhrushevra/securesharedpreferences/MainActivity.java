package ru.mirea.vakhrushevra.securesharedpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class MainActivity extends AppCompatActivity {

    EditText editPoet;
    Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editPoet = findViewById(R.id.editPoet);
        buttonSave = findViewById(R.id.buttonSave);

        try {

            MasterKey masterKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences securePreferences =
                    EncryptedSharedPreferences.create(
                            this,
                            "secret_shared_prefs",
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );

            editPoet.setText(
                    securePreferences.getString("POET", "")
            );

            buttonSave.setOnClickListener(v -> {

                securePreferences.edit()
                        .putString("POET",
                                editPoet.getText().toString())
                        .apply();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}