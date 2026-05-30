package ru.mirea.vakhrushevra.yandexmaps;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class App extends Application {
    static final String MAPKIT_API_KEY = "28b441e5-7232-46aa-a671-e2a716147b38";

    private static boolean apiKeySet;

    static boolean isApiKeySet() {
        return apiKeySet;
    }

    @Override
    public void onCreate() {
        MapKitFactory.setLocale("ru_RU");
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        apiKeySet = true;

        super.onCreate();

        MapKitDiagnostics.log("App.onCreate(): setApiKey() выполнен");
        MapKitDiagnostics.log("Package name: " + getPackageName());
        MapKitDiagnostics.log("ApplicationId: ru.mirea.vakhrushevra.yandexmaps");
    }
}
