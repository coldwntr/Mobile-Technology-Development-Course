package ru.mirea.vakhrushevra.cryptoloader;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import ru.mirea.vakhrushevra.cryptoloader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final int LOADER_ID = 1;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonEncrypt.setOnClickListener(view -> startEncryption());
    }

    private void startEncryption() {
        String inputText = binding.editTextInput.getText().toString();

        if (inputText.isEmpty()) {
            binding.textViewResult.setText("Введите текст для шифрования");
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("TEXT", inputText);

        binding.textViewResult.setText("Идёт шифрование через AsyncTaskLoader...");
        Log.d("MainActivity", "Запускаем Loader");

        LoaderManager.getInstance(this).restartLoader(LOADER_ID, bundle, this);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d("MainActivity", "onCreateLoader");

        return new CryptoLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Log.d("MainActivity", "onLoadFinished: " + data);

        binding.textViewResult.setText(
                "Результат шифрования:\n" +
                        data + "\n\n" +
                        "Смотри Logcat по тегам MainActivity и CryptoLoader."
        );
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        Log.d("MainActivity", "onLoaderReset");
    }
}