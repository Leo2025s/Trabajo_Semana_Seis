package com.tuempresa.distribuidora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final double LAT_BODEGA = -33.4372;
    private static final double LON_BODEGA = -70.6506;
    private static final int PERMISO_UBICACION = 100;

    private LocationManager locationManager;
    private double latUser = 0.0, lonUser = 0.0;

    private TextView tvResult;
    private EditText etMonto;
    private Button btnCalcular;

    private GoogleMap mMap;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMonto = findViewById(R.id.etMonto);
        btnCalcular = findViewById(R.id.btnCalcular);
        tvResult = findViewById(R.id.tvResult);

        // Iniciar mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Configurar Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("temperatura");

        // Configurar alerta
        mediaPlayer = MediaPlayer.create(this, R.raw.alerta);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Escuchar temperatura desde Firebase
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double temperatura = snapshot.getValue(Double.class);
                    Log.d("FIREBASE", "Temperatura actual: " + temperatura);

                    if (temperatura > -10) { // si supera el límite de frío
                        reproducirAlerta();
                        Toast.makeText(MainActivity.this, "⚠ Temperatura fuera de rango: " + temperatura + "°C", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", "Error al leer temperatura: " + error.getMessage());
            }
        });

        // Verificar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_UBICACION);
        } else {
            obtenerUbicacion();
        }

        btnCalcular.setOnClickListener(v -> calcularDespacho());
    }

    private void reproducirAlerta() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        if (vibrator != null) {
            vibrator.vibrate(1000); // 1 segundo
        }
    }

    private void calcularDespacho() {
        if (latUser == 0.0 && lonUser == 0.0) {
            tvResult.setText("⚠ Esperando ubicación GPS...");
            return;
        }

        String montoStr = etMonto.getText().toString();
        if (montoStr.isEmpty()) {
            tvResult.setText("⚠ Por favor, ingrese un monto válido.");
            return;
        }

        double montoCompra = Double.parseDouble(montoStr);
        double distancia = Haversine.calcularDistancia(latUser, lonUser, LAT_BODEGA, LON_BODEGA);
        double costo = CalculoDespacho.calcularCosto(montoCompra, distancia);

        String resultado = "Monto compra: $" + montoCompra + "\n"
                + "Ubicación: (" + latUser + ", " + lonUser + ")\n"
                + "Distancia a bodega: " + String.format("%.2f", distancia) + " km\n"
                + "Costo de despacho: $" + String.format("%.2f", costo);

        tvResult.setText(resultado);
    }

    private void obtenerUbicacion() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    5,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            latUser = location.getLatitude();
                            lonUser = location.getLongitude();
                            actualizarMapa();
                        }
                    }
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void actualizarMapa() {
        if (mMap != null) {
            LatLng ubicacion = new LatLng(latUser, lonUser);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(ubicacion).title("Tu ubicación"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 14f));
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}
