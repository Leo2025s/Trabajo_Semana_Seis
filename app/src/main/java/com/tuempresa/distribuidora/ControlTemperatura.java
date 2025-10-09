package com.tuempresa.distribuidora;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ControlTemperatura {

    private static final double LIMITE_MAXIMO = -10.0; // Temperatura máxima permitida (ejemplo: -10°C)
    private static MediaPlayer mediaPlayer;
    private static Vibrator vibrator;

    /**
     * Verifica la temperatura actual y genera alerta si se sale del rango permitido.
     */
    public static void verificarTemperatura(Context context) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("temperatura");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double tempActual = snapshot.getValue(Double.class);

                    if (tempActual > LIMITE_MAXIMO) {
                        reproducirAlerta(context);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Puedes agregar logs si quieres depurar
            }
        });
    }

    /**
     * Reproduce el sonido de alerta y hace vibrar el teléfono.
     */
    public static void reproducirAlerta(Context context) {
        try {
            // Reproduce el sonido
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.alerta);
            }

            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }

            // Vibra durante 1 segundo
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Detiene el sonido si está activo.
     */
    public static void detenerAlerta() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
