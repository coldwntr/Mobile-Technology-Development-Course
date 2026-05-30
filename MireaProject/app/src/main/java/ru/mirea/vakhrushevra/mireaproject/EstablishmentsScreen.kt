package ru.mirea.vakhrushevra.mireaproject

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.mirea.vakhrushevra.mireaproject.establishments.Establishment
import ru.mirea.vakhrushevra.mireaproject.establishments.EstablishmentsData

/**
 * Экран «Заведения» для практики №8.
 * В проекте используется Compose, поэтому реализован как @Composable вместо Fragment.
 */
@Composable
fun EstablishmentsScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val establishments = remember { EstablishmentsData.establishments }

    var mapView by remember { mutableStateOf<MapView?>(null) }
    var myLocationOverlay by remember { mutableStateOf<MyLocationNewOverlay?>(null) }
    var selectedEstablishmentId by remember { mutableIntStateOf(-1) }
    var markerDialogEstablishment by remember { mutableStateOf<Establishment?>(null) }
    var locationMessage by remember { mutableStateOf<String?>(null) }

    remember {
        Configuration.getInstance().load(
            context,
            PreferenceManager.getDefaultSharedPreferences(context)
        )
        Configuration.getInstance().userAgentValue = context.packageName
        true
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            enableMyLocation(mapView, myLocationOverlay, context) { message ->
                locationMessage = message
            }
        } else {
            locationMessage = "Разрешение на геолокацию не выдано. Местоположение недоступно."
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val map = mapView

        if (map == null) {
            onDispose { }
        } else {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> map.onResume()
                    Lifecycle.Event.ON_PAUSE -> map.onPause()
                    else -> Unit
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
            map.onResume()

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                map.onPause()
            }
        }
    }

    if (markerDialogEstablishment != null) {
        val establishment = markerDialogEstablishment!!

        AlertDialog(
            onDismissRequest = { markerDialogEstablishment = null },
            title = { Text(text = establishment.name) },
            text = {
                Text(
                    text = "Адрес: ${establishment.address}\n\n${establishment.description}"
                )
            },
            confirmButton = {
                TextButton(onClick = { markerDialogEstablishment = null }) {
                    Text(text = "Закрыть")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Заведения",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Список заведений Москвы на карте OpenStreetMap. Нажмите на элемент списка или маркер, чтобы увидеть подробности.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (hasLocationPermission(context)) {
                        enableMyLocation(mapView, myLocationOverlay, context) { message ->
                            locationMessage = message
                        }
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            ) {
                Text(text = "Моё местоположение")
            }
        }

        if (locationMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = locationMessage!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(establishments, key = { it.id }) { establishment ->
                EstablishmentListItem(
                    establishment = establishment,
                    selected = establishment.id == selectedEstablishmentId,
                    onClick = {
                        selectedEstablishmentId = establishment.id
                        moveMapToEstablishment(mapView, establishment)
                        Toast.makeText(
                            context,
                            "Карта перемещена: ${establishment.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { viewContext ->
                MapView(viewContext).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    zoomController.setVisibility(
                        org.osmdroid.views.CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT
                    )

                    controller.setZoom(12.0)
                    controller.setCenter(
                        GeoPoint(
                            establishments.first().latitude,
                            establishments.first().longitude
                        )
                    )

                    val locationOverlay = MyLocationNewOverlay(
                        GpsMyLocationProvider(viewContext),
                        this
                    )
                    myLocationOverlay = locationOverlay
                    overlays.add(locationOverlay)

                    establishments.forEach { establishment ->
                        val marker = Marker(this)
                        marker.position = GeoPoint(
                            establishment.latitude,
                            establishment.longitude
                        )
                        marker.title = establishment.name
                        marker.snippet = "${establishment.address}\n${establishment.description}"
                        marker.setOnMarkerClickListener { clickedMarker, _ ->
                            markerDialogEstablishment = establishments.firstOrNull {
                                it.name == clickedMarker.title
                            }
                            Toast.makeText(
                                viewContext,
                                clickedMarker.title,
                                Toast.LENGTH_SHORT
                            ).show()
                            true
                        }
                        overlays.add(marker)
                    }

                    mapView = this
                }
            },
            update = { map ->
                if (selectedEstablishmentId != -1) {
                    establishments.firstOrNull { it.id == selectedEstablishmentId }?.let { establishment ->
                        moveMapToEstablishment(map, establishment)
                    }
                }
            }
        )
    }
}

@Composable
private fun EstablishmentListItem(
    establishment: Establishment,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        FeatureCard(title = establishment.name) {
            Text(
                text = establishment.address,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = establishment.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (selected) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Выбрано на карте",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun moveMapToEstablishment(mapView: MapView?, establishment: Establishment) {
    mapView?.controller?.animateTo(
        GeoPoint(establishment.latitude, establishment.longitude)
    )
    mapView?.controller?.setZoom(16.0)
}

private fun hasLocationPermission(context: Context): Boolean {
    val fineGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    val coarseGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    return fineGranted || coarseGranted
}

private fun enableMyLocation(
    mapView: MapView?,
    myLocationOverlay: MyLocationNewOverlay?,
    context: Context,
    onMessage: (String) -> Unit
) {
    val overlay = myLocationOverlay

    if (overlay == null || mapView == null) {
        onMessage("Карта ещё не готова. Попробуйте снова через секунду.")
        return
    }

    val enabled = overlay.enableMyLocation()

    if (!enabled) {
        onMessage("Не удалось включить отображение местоположения. Проверьте GPS и разрешения.")
        return
    }

    val location = overlay.myLocation

    if (location != null) {
        mapView.controller.animateTo(location)
        mapView.controller.setZoom(16.0)
        onMessage("")
        Toast.makeText(context, "Местоположение показано на карте", Toast.LENGTH_SHORT).show()
    } else {
        onMessage("")
        Toast.makeText(
            context,
            "Ожидание GPS-сигнала. Местоположение появится на карте автоматически.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
