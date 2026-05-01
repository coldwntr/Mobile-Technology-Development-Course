package ru.mirea.vakhrushevra.favoritebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {

    private TextView textViewDeveloperBook;
    private EditText editTextUserBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        textViewDeveloperBook = findViewById(R.id.textViewDeveloperBook);
        editTextUserBook = findViewById(R.id.editTextUserBook);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String developerBook = extras.getString(MainActivity.KEY);

            textViewDeveloperBook.setText("Любимая книга разработчика – " + developerBook);
        }
    }

    public void sendBookToMainActivity(View view) {
        String text = editTextUserBook.getText().toString();

        Intent data = new Intent();

        data.putExtra(MainActivity.USER_MESSAGE, text);

        setResult(Activity.RESULT_OK, data);

        finish();
    }
}