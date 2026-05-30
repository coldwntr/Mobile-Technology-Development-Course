package ru.mirea.vakhrushevra.yandexmaps;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;

import ru.mirea.vakhrushevra.yandexmaps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final Point MOSCOW = new Point(55.751225, 37.62954);

    private ActivityMainBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapKitDiagnostics.log("MainActivity.onCreate(): App.isApiKeySet() = " + App.isApiKeySet());
        MapKitDiagnostics.log("MainActivity.onCreate(): интернет = " + MapKitDiagnostics.hasInternet(this));

        if (!App.isApiKeySet()) {
            MapKitDiagnostics.toast(this, "Ошибка: setApiKey() не был вызван в App.onCreate()");
            MapKitDiagnostics.logError("MapKitFactory.setApiKey() должен вызываться в App до initialize()");
            return;
        }

        if (!MapKitDiagnostics.hasInternet(this)) {
            MapKitDiagnostics.toast(this, "Нет интернета. Тайлы карты не загрузятся.");
        }

        MapKitFactory.initialize(this);
        MapKitDiagnostics.log("MainActivity.onCreate(): MapKitFactory.initialize() выполнен");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MapKitDiagnostics.toast(
                this,
                "MapKit init OK. Package: " + getPackageName()
        );

        scheduleTileLoadCheck();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        binding.mapview.onStart();
        MapKitDiagnostics.log("MainActivity.onStart(): MapKitFactory и MapView onStart()");

        binding.mapview.getMap().move(
                new CameraPosition(MOSCOW, 15.0f, 0.0f, 0.0f)
        );
        MapKitDiagnostics.log("MainActivity.onStart(): камера перемещена на Москву");
    }

    @Override
    protected void onStop() {
        binding.mapview.onStop();
        MapKitFactory.getInstance().onStop();
        MapKitDiagnostics.log("MainActivity.onStop(): MapKitFactory и MapView onStop()");
        super.onStop();
    }

    private void scheduleTileLoadCheck() {
        handler.postDelayed(() -> {
            MapKitDiagnostics.toast(
                    this,
                    "Если видна только сетка — проверьте API-ключ MapKit Mobile SDK "
                            + "в developer.tech.yandex.ru (Logcat: Invalid api key)"
            );
            MapKitDiagnostics.logError(
                    "Если тайлы не загружаются, в Logcat ищите: "
                            + "yandex.maps: Forbidden. Body :Invalid api key"
            );
        }, 5000L);
    }
}
