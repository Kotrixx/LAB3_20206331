package com.example.lab3_20206331;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3_20206331.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean conexionValida = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Spinner: categorías
        ArrayAdapter<CharSequence> adapterCategorias = ArrayAdapter.createFromResource(
                this, R.array.categorias_array, android.R.layout.simple_spinner_item);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategoria.setAdapter(adapterCategorias);

        // Spinner: dificultad
        ArrayAdapter<CharSequence> adapterDificultad = ArrayAdapter.createFromResource(
                this, R.array.dificultades_array, android.R.layout.simple_spinner_item);
        adapterDificultad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDificultad.setAdapter(adapterDificultad);

        // Comprobar conexión
        binding.btnComprobarConexion.setOnClickListener(v -> {
            if (hayConexionInternet()) {
                Toast.makeText(this, "¡Conexión a internet disponible!", Toast.LENGTH_SHORT).show();
                conexionValida = true;
                binding.btnComenzar.setEnabled(true);
                binding.btnComenzar.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_dark));
            } else {
                Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                conexionValida = false;
            }
        });

        // Comenzar trivia
        binding.btnComenzar.setOnClickListener(v -> {
            if (!conexionValida) {
                Toast.makeText(this, "Primero comprueba la conexión", Toast.LENGTH_SHORT).show();
                return;
            }

            String cantidadStr = binding.etCantidad.getText().toString();
            if (cantidadStr.isEmpty() || Integer.parseInt(cantidadStr) <= 0) {
                Toast.makeText(this, "Ingrese una cantidad válida de preguntas", Toast.LENGTH_SHORT).show();
                return;
            }

            String categoriaNombre = binding.spinnerCategoria.getSelectedItem().toString();
            String dificultadSeleccionada = binding.spinnerDificultad.getSelectedItem().toString();

            if (categoriaNombre.equals("Seleccione una categoría")) {
                Toast.makeText(this, "Debe seleccionar una categoría", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dificultadSeleccionada.equals("Seleccione una dificultad")) {
                Toast.makeText(this, "Debe seleccionar una dificultad", Toast.LENGTH_SHORT).show();
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);

            // Traducción de dificultad
            String dificultadApi;
            switch (dificultadSeleccionada) {
                case "Fácil": dificultadApi = "easy"; break;
                case "Media": dificultadApi = "medium"; break;
                case "Difícil": dificultadApi = "hard"; break;
                default: dificultadApi = "easy"; break;
            }

            int categoriaId = obtenerIdCategoria(categoriaNombre);

            Intent intent = new Intent(this, QuestionActivity.class);
            intent.putExtra("cantidad", cantidad);
            intent.putExtra("categoria", categoriaId);
            intent.putExtra("dificultad", dificultadApi);
            startActivity(intent);
        });

    }

    private boolean hayConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private int obtenerIdCategoria(String nombre) {
        switch (nombre) {
            case "Cultura General": return 9;
            case "Libros": return 10;
            case "Películas": return 11;
            case "Música": return 12;
            case "Computación": return 18;
            case "Matemática": return 19;
            case "Deportes": return 21;
            case "Historia": return 23;
            default: return 9;
        }
    }
}
