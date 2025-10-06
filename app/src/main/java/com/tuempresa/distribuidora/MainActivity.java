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
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final double LAT_BODEGA = -33.4372;
    private static final double LON_BODEGA = -70.6506;
    private static final int PERMISO_UBICACION = 100;

    private LocationManager locationManager;
    private double latUser = 0.0, lonUser = 0.0;

    private TextView tvResult, tvTempFirebase;
    private EditText etMonto;
    private Button btnCalcular;

    // Rango de temperatura definido por el administrador
    private double minTemp = -20.0;
    private double maxTemp = -10.0;

    // Firebase
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMonto = findViewById(R.id.etMonto);
        btnCalcular = findViewById(R.id.btnCalcular);
        tvResult = findViewById(R.id.tvResult);
        tvTempFirebase = findViewById(R.id.tvTempFirebase);

        // Firebase Database
        databaseRef = FirebaseDatabase.getInstance().getReference("temperatura");

        // Escuchar cambios de temperatura en tiempo real
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double temp = snapshot.getValue(Double.class);
                    tvTempFirebase.setText("🌡 Temperatura actual: " + temp + " °C");

                    if (temp < minTemp || temp > maxTemp) {
                        reproducirAlerta();
                    } else {
                        Log.d("TEMP", "Temperatura dentro del rango permitido");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvTempFirebase.setText("Error al leer temperatura de Firebase");
            }
        });

        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_UBICACION);
        } else {
            obtenerUbicacion();
        }

        // Calcular costo de despacho
        btnCalcular.setOnClickListener(v -> {
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
                    + "Ubicación cliente: (" + latUser + ", " + lonUser + ")\n"
                    + "Distancia a bodega: " + String.format("%.2f", distancia) + " km\n"
                    + "Costo de despacho: $" + costo + "\n";

            tvResult.setText(resultado);

            Log.d("DISTANCIA", "Distancia calculada: " + distancia + " km");
            Log.d("COSTO", "Costo de despacho: $" + costo);
        });
    }

    // Obtener ubicación
    private void obtenerUbicacion() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000, // cada 5 segundos
                    5,    // cada 5 metros
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            latUser = location.getLatitude();
                            lonUser = location.getLongitude();
                            Log.d("UBICACION", "Lat: " + latUser + " Lon: " + lonUser);
                        }

                        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
                        @Override public void onProviderEnabled(@NonNull String provider) {}
                        @Override public void onProviderDisabled(@NonNull String provider) {}
                    }
            );
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // Reproducir sonido y vibrar
    private void reproducirAlerta() {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alerta);
            mediaPlayer.start();

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE));
            }

            Log.w("ALERTA", "⚠ Temperatura fuera del rango permitido. Sonido y vibración activados.");

        } catch (Exception e) {
            Log.e("ERROR_ALERTA", "Error al reproducir alerta: " + e.getMessage());
        }
    }

    // Manejar permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_UBICACION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                tvResult.setText("⚠ Permiso de ubicación denegado.");
            }
        }
    }
}
