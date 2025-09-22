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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MenuActivity extends AppCompatActivity {

    private static final int PERMISO_UBICACION = 100;
    private LocationManager locationManager;
    private TextView tvInfo;

    private DatabaseReference dbRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tvInfo = findViewById(R.id.tvInfo);

        // Obtener usuario autenticado
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("usuarios").child(userId);

        // Permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_UBICACION);
        } else {
            obtenerUbicacion();
        }
    }

    private void obtenerUbicacion() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,  // cada 5 segundos
                    5,     // cada 5 metros
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();

                            tvInfo.setText("Lat: " + lat + "\nLon: " + lon);

                            // Guardar en Firebase
                            dbRef.child("ubicacion").setValue(lat + "," + lon);

                            Log.d("FIREBASE", "Ubicación enviada: " + lat + ", " + lon);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_UBICACION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                tvInfo.setText("⚠ Permiso de ubicación denegado.");
            }
        }
    }
}
