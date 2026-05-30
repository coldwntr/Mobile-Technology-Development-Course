package ru.mirea.vakhrushevra.osmmaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 300;

    private MapView mapView;
    private MyLocationNewOverlay locationNewOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);

        setupMap();
        checkLocationPermission();
        addCompass();
        addScaleBar();
        addMarkers();
    }

    private void setupMap() {
        mapView.setZoomRounding(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(12.0);

        GeoPoint startPoint = new GeoPoint(55.751574, 37.573856);
        mapController.setCenter(startPoint);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            addUserLocation();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void addUserLocation() {
        locationNewOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(getApplicationContext()),
                mapView
        );

        locationNewOverlay.enableMyLocation();
        mapView.getOverlays().add(locationNewOverlay);
    }

    private void addCompass() {
        CompassOverlay compassOverlay = new CompassOverlay(
                getApplicationContext(),
                new InternalCompassOrientationProvider(getApplicationContext()),
                mapView
        );

        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
    }

    private void addScaleBar() {
        Context context = getApplicationContext();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        mapView.getOverlays().add(scaleBarOverlay);
    }

    private void addMarkers() {
        addMarker(
                new GeoPoint(55.760186, 37.618711),
                "Большой театр",
                "Известный театр в центре Москвы."
        );

        addMarker(
                new GeoPoint(55.753930, 37.620795),
                "Красная площадь",
                "Главная площадь Москвы."
        );

        addMarker(
                new GeoPoint(55.751244, 37.618423),
                "Кремль",
                "Исторический центр Москвы."
        );
    }

    private void addMarker(GeoPoint point, String title, String description) {
        Marker marker = new Marker(mapView);

        marker.setPosition(point);
        marker.setTitle(title);
        marker.setSubDescription(description);

        marker.setOnMarkerClickListener((marker1, mapView1) -> {
            Toast.makeText(
                    getApplicationContext(),
                    marker1.getTitle() + "\n" + marker1.getSubDescription(),
                    Toast.LENGTH_LONG
            ).show();

            marker1.showInfoWindow();
            return true;
        });

        mapView.getOverlays().add(marker);
    }

    @Override
    public void onResume() {
        super.onResume();

        Configuration.getInstance().load(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );

        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Configuration.getInstance().save(
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );

        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addUserLocation();
            } else {
                Toast.makeText(this, "Разрешение на геолокацию не выдано", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
