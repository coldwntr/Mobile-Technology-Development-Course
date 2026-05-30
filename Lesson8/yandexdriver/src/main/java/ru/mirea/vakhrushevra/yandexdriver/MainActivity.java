package ru.mirea.vakhrushevra.yandexdriver;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DrivingSession.DrivingRouteListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 200;

    private MapView mapView;

    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;

    // Координаты любимого заведения.
    // Сейчас стоит Большой театр.
    private final Point FAVORITE_PLACE = new Point(55.760186, 37.618711);

    private final String FAVORITE_PLACE_NAME = "Большой театр";
    private final String FAVORITE_PLACE_INFO = "Большой театр — известное культурное место Москвы.";

    private final int[] colors = {
            0xFFFF0000,
            0xFF00AA00,
            0xFF0000FF,
            0xFFFF8800
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapKitFactory.initialize(this);
        DirectionsFactory.initialize(this);

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);

        mapView.getMap().setRotateGesturesEnabled(false);

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        checkPermissionAndBuildRoute();
    }

    private void checkPermissionAndBuildRoute() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Point userPoint = getUserLocationPoint();

            buildRoute(userPoint, FAVORITE_PLACE);
            addFavoritePlaceMarker();

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

    private Point getUserLocationPoint() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return new Point(55.751574, 37.573856);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null) {
            return new Point(location.getLatitude(), location.getLongitude());
        }

        Toast.makeText(this, "Геолокация не найдена. Использую центр Москвы.", Toast.LENGTH_LONG).show();

        return new Point(55.751574, 37.573856);
    }

    private void buildRoute(Point startPoint, Point endPoint) {
        Point screenCenter = new Point(
                (startPoint.getLatitude() + endPoint.getLatitude()) / 2,
                (startPoint.getLongitude() + endPoint.getLongitude()) / 2
        );

        mapView.getMap().move(
                new CameraPosition(screenCenter, 11.0f, 0.0f, 0.0f)
        );

        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();

        drivingOptions.setRoutesCount(4);

        ArrayList<RequestPoint> requestPoints = new ArrayList<>();

        requestPoints.add(new RequestPoint(
                startPoint,
                RequestPointType.WAYPOINT,
                null
        ));

        requestPoints.add(new RequestPoint(
                endPoint,
                RequestPointType.WAYPOINT,
                null
        ));

        drivingSession = drivingRouter.requestRoutes(
                requestPoints,
                drivingOptions,
                vehicleOptions,
                this
        );
    }

    private void addFavoritePlaceMarker() {
        PlacemarkMapObject marker = mapView.getMap().getMapObjects().addPlacemark(
                FAVORITE_PLACE,
                ImageProvider.fromResource(this, android.R.drawable.star_big_on)
        );

        marker.addTapListener(new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                Toast.makeText(
                        getApplicationContext(),
                        FAVORITE_PLACE_NAME + "\n" + FAVORITE_PLACE_INFO,
                        Toast.LENGTH_LONG
                ).show();

                return true;
            }
        });
    }

    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> routes) {
        for (int i = 0; i < routes.size(); i++) {
            int color = colors[i % colors.length];

            mapObjects.addPolyline(routes.get(i).getGeometry())
                    .setStrokeColor(color);
        }
    }

    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
        String errorMessage = "Неизвестная ошибка";

        if (error instanceof RemoteError) {
            errorMessage = "Ошибка сервера";
        } else if (error instanceof NetworkError) {
            errorMessage = "Ошибка сети";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Point userPoint = getUserLocationPoint();

                buildRoute(userPoint, FAVORITE_PLACE);
                addFavoritePlaceMarker();

            } else {
                Toast.makeText(this, "Нет разрешения на геолокацию", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        MapKitFactory.getInstance().onStart();

        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    protected void onStop() {
        if (mapView != null) {
            mapView.onStop();
        }

        MapKitFactory.getInstance().onStop();

        super.onStop();
    }
}