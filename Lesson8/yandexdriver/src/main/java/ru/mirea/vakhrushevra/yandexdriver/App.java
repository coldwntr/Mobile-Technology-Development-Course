package ru.mirea.vakhrushevra.yandexdriver;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class App extends Application {

    private static final String MAPKIT_API_KEY = "28b441e5-7232-46aa-a671-e2a716147b38";

    @Override
    public void onCreate() {
        super.onCreate();

        MapKitFactory.setApiKey(MAPKIT_API_KEY);
    }
}