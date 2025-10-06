package com.tuempresa.distribuidora;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigActivity extends AppCompatActivity {

    private EditText etMin, etMax;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        etMin = findViewById(R.id.etMin);
        etMax = findViewById(R.id.etMax);
        btnGuardar = findViewById(R.id.btnGuardar);

        SharedPreferences prefs = getSharedPreferences("RANGOS", MODE_PRIVATE);
        etMin.setText(String.valueOf(prefs.getInt("MIN", 0)));
        etMax.setText(String.valueOf(prefs.getInt("MAX", 30)));

        btnGuardar.setOnClickListener(v -> {
            int min = Integer.parseInt(etMin.getText().toString());
            int max = Integer.parseInt(etMax.getText().toString());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("MIN", min);
            editor.putInt("MAX", max);
            editor.apply();

            Toast.makeText(this, "Rangos guardados correctamente", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
