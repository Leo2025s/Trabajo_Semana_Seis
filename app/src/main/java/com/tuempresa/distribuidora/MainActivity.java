package com.tuempresa.distribuidora;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final double LAT_BODEGA = -33.4372;
    private static final double LON_BODEGA = -70.6506;
    private static final int PERMISO_UBICACION = 100;

    private LocationManager locationManager;
    private double latUser = 0.0, lonUser = 0.0;

    private TextView tvResult;
    private EditText etMonto;
    private Button btnCalcular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMonto = findViewById(R.id.etMonto);
        btnCalcular = findViewById(R.id.btnCalcular);
        tvResult = findViewById(R.id.tvResult);

        // Verificar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_UBICACION);
        } else {
            obtenerUbicacion();
        }

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

            // Calcular distancia usando tu clase Haversine
            double distancia = Haversine.calcularDistancia(latUser, lonUser, LAT_BODEGA, LON_BODEGA);

            // Calcular costo de despacho usando tu clase CalculoDespacho
            double costo = CalculoDespacho.calcularCosto(montoCompra, distancia);

            // Verificar temperatura (simulada)
            boolean ok = ControlTemperatura.verificarTemperatura(-15.0);

            String resultado = "Monto compra: $" + montoCompra + "\n"
                    + "Ubicación cliente: (" + latUser + ", " + lonUser + ")\n"
                    + "Distancia a bodega: " + String.format("%.2f", distancia) + " km\n"
                    + "Costo de despacho: $" + costo + "\n";

            resultado += ok ? "✔ Temperatura dentro del rango.\n" : "⚠ Alerta: Cadena de frío comprometida.\n";

            tvResult.setText(resultado);

            Log.d("DISTANCIA", "Distancia calculada: " + distancia + " km");
            Log.d("COSTO", "Costo de despacho: $" + costo);
        });
    }

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

