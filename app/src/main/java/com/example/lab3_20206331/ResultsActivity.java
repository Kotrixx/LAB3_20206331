package com.example.lab3_20206331;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3_20206331.databinding.ActivityResultsBinding;

public class ResultsActivity extends AppCompatActivity {

    private ActivityResultsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener datos del intent
        int correctas = getIntent().getIntExtra("correctas", 0);
        int incorrectas = getIntent().getIntExtra("incorrectas", 0);
        int noRespondidas = getIntent().getIntExtra("noRespondidas", 0);

        // Mostrar estadísticas
        binding.tvCorrectas.setText(String.valueOf(correctas));
        binding.tvIncorrectas.setText(String.valueOf(incorrectas));
        binding.tvNoRespondidas.setText(String.valueOf(noRespondidas));

        // Botón volver
        binding.btnVolverAJugar.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}

