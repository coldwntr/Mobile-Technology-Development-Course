package ru.mirea.vakhrushevra.cryptoloader;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class CryptoLoader extends AsyncTaskLoader<String> {

    private final Bundle args;

    public CryptoLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        Log.d("CryptoLoader", "onStartLoading: запускаем forceLoad");

        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        Log.d("CryptoLoader", "loadInBackground: работа началась в фоновом потоке");

        if (args == null) {
            return "Ошибка: данные не переданы";
        }

        String inputText = args.getString("TEXT", "");

        if (inputText.isEmpty()) {
            return "Ошибка: пустой текст";
        }

        SystemClock.sleep(3000);

        String encryptedText = encryptText(inputText);

        Log.d("CryptoLoader", "Исходный текст: " + inputText);
        Log.d("CryptoLoader", "Зашифрованный текст: " + encryptedText);

        return encryptedText;
    }

    private String encryptText(String text) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char symbol = text.charAt(i);
            char encryptedSymbol = (char) (symbol + 3);
            result.append(encryptedSymbol);
        }

        return result.toString();
    }
}