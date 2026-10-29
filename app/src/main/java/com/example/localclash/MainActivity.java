package com.example.localclash;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.localclash.database.DatabaseHelper;
import com.example.localclash.models.Sucursal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.maplibre.android.MapLibre;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.location.LocationComponent;
import org.maplibre.android.location.LocationComponentActivationOptions;
import org.maplibre.android.location.modes.CameraMode;
import org.maplibre.android.location.modes.RenderMode;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private MapLibreMap mapLibreMap;
    private DatabaseHelper dbHelper;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    // Coordenadas del centro de Gran Santiago, Chile
    private static final double SANTIAGO_LAT = -33.4489;
    private static final double SANTIAGO_LON = -70.6693;
    private static final double DEFAULT_ZOOM = 11.5;

    // URL de Estilo de MapLibre
    private static final String MAPLIBRE_STYLE_URL = "https://demotiles.maplibre.org/style.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar MapLibre y Helper de Base de Datos
        MapLibre.getInstance(this);
        dbHelper = new DatabaseHelper(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mapView = findViewById(R.id.mapView);
        FloatingActionButton fabLocation = findViewById(R.id.fabLocation);
        ImageButton btnBackToMenu = findViewById(R.id.btnBackToMenu);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Botón para regresar al menú principal
        btnBackToMenu.setOnClickListener(v -> finish());

        // Botón flotante para activar la geolocalización GPS
        fabLocation.setOnClickListener(v -> centerOnUserLocation());
    }

    @Override
    public void onMapReady(@NonNull MapLibreMap map) {
        this.mapLibreMap = map;

        map.setStyle(MAPLIBRE_STYLE_URL, style -> {
            // Posicionar la cámara inicialmente abarcando el Gran Santiago
            LatLng santiago = new LatLng(SANTIAGO_LAT, SANTIAGO_LON);
            map.setCameraPosition(new CameraPosition.Builder()
                    .target(santiago)
                    .zoom(DEFAULT_ZOOM)
                    .build());

            // Cargar marcadores de Sucursales desde la Base de Datos SQLite
            loadBranchMarkersFromDatabase(map);

            // Activar componente de localización del usuario
            enableLocationComponent(style);
        });
    }

    @SuppressWarnings("deprecation")
    private void loadBranchMarkersFromDatabase(MapLibreMap map) {
        List<Sucursal> sucursales = dbHelper.getAllSucursales();
        for (Sucursal s : sucursales) {
            LatLng pos = new LatLng(s.getLatitud(), s.getLongitud());
            map.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(s.getNombre())
                    .snippet(s.getDireccion()));
        }
    }

    private void enableLocationComponent(@NonNull Style style) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationComponent locationComponent = mapLibreMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, style).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void centerOnUserLocation() {
        if (mapLibreMap == null) return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationComponent locationComponent = mapLibreMap.getLocationComponent();
            if (locationComponent.isLocationComponentActivated() && locationComponent.getLastKnownLocation() != null) {
                Location lastLocation = locationComponent.getLastKnownLocation();
                LatLng userLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

                mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15.0));
                Toast.makeText(this, "Centrado en tu ubicación actual", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Obteniendo posición GPS...", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.location_permission_required, Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mapLibreMap != null && mapLibreMap.getStyle() != null) {
                    enableLocationComponent(mapLibreMap.getStyle());
                }
            } else {
                Toast.makeText(this, R.string.location_permission_required, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }
}