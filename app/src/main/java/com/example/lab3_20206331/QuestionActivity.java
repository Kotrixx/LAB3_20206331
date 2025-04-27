package com.example.lab3_20206331;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.lab3_20206331.data.model.Question;
import com.example.lab3_20206331.databinding.ActivityQuestionBinding;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    private ActivityQuestionBinding binding;
    private TriviaViewModel viewModel;
    private List<Question> preguntas;
    private int actual = 0;
    private int correctas = 0, incorrectas = 0, noRespondidas = 0;

    private Thread hiloTemporizador;
    private boolean temporizadorActivo = true;
    private int tiempoRestante; // en segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Desactivar botón hasta tener preguntas
        binding.btnSiguiente.setEnabled(false);
        binding.tvPregunta.setText("Cargando preguntas...");
        binding.opcion1.setText("");
        binding.opcion2.setText("");
        binding.opcion3.setText("");
        binding.opcion4.setText("");

        int cantidad = getIntent().getIntExtra("cantidad", 5);
        int categoria = getIntent().getIntExtra("categoria", 9);
        String dificultad = getIntent().getStringExtra("dificultad");

        viewModel = new ViewModelProvider(this).get(TriviaViewModel.class);
        viewModel.cargarPreguntas(cantidad, categoria, dificultad);

        viewModel.getPreguntas().observe(this, list -> {
            if (list == null || list.isEmpty()) {
                Toast.makeText(this, "No se pudieron obtener preguntas", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            preguntas = list;
            Log.d("QuestionActivity", "Preguntas recibidas: " + list.size());
            mostrarPregunta();

            binding.btnSiguiente.setEnabled(true);
            long tiempoTotal = calcularTiempoTotal(preguntas.size(), dificultad);
            iniciarHiloTemporizador(tiempoTotal);
        });

        binding.btnSiguiente.setOnClickListener(v -> procesarRespuesta());
    }

    private void mostrarPregunta() {
        if (actual >= preguntas.size()) {
            temporizadorActivo = false;
            irAResultados();
            return;
        }

        Question q = preguntas.get(actual);

        try {
            String pregunta = URLDecoder.decode(q.getQuestion(), "UTF-8");
            String correcta = URLDecoder.decode(q.getCorrectAnswer(), "UTF-8");
            List<String> opciones = new ArrayList<>();
            for (String opc : q.getIncorrectAnswers()) {
                opciones.add(URLDecoder.decode(opc, "UTF-8"));
            }
            opciones.add(correcta);
            Collections.shuffle(opciones);

            binding.tvPregunta.setText(pregunta);
            binding.rgOpciones.clearCheck();
            binding.opcion1.setText(opciones.get(0));
            binding.opcion2.setText(opciones.get(1));
            binding.opcion3.setText(opciones.get(2));
            binding.opcion4.setText(opciones.get(3));
        } catch (Exception e) {
            Toast.makeText(this, "Error al decodificar texto", Toast.LENGTH_SHORT).show();
        }
    }

    private void procesarRespuesta() {
        RadioButton selected = findViewById(binding.rgOpciones.getCheckedRadioButtonId());

        try {
            String correcta = URLDecoder.decode(preguntas.get(actual).getCorrectAnswer(), "UTF-8");

            if (selected == null) {
                noRespondidas++;
            } else {
                String seleccionada = selected.getText().toString();
                if (seleccionada.equals(correcta)) {
                    correctas++;
                } else {
                    incorrectas++;
                }
            }

            actual++;

            if (actual >= preguntas.size()) {
                temporizadorActivo = false;
                if (hiloTemporizador != null && hiloTemporizador.isAlive()) {
                    hiloTemporizador.interrupt();
                }
                irAResultados();
            } else {
                mostrarPregunta();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
        }
    }

    private void iniciarHiloTemporizador(long millisTotales) {
        tiempoRestante = (int) (millisTotales / 1000);
        temporizadorActivo = true;

        hiloTemporizador = new Thread(() -> {
            while (tiempoRestante >= 0 && temporizadorActivo) {
                int tiempoActual = tiempoRestante;

                runOnUiThread(() -> binding.tvContador.setText("Tiempo: " + tiempoActual + "s"));

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }

                tiempoRestante--;
            }

            if (temporizadorActivo) {
                runOnUiThread(this::irAResultados);
            }
        });

        hiloTemporizador.start();
    }

    private long calcularTiempoTotal(int cantidad, String dificultad) {
        int segundosPorPregunta;
        switch (dificultad) {
            case "easy":
                segundosPorPregunta = 5;
                break;
            case "medium":
                segundosPorPregunta = 7;
                break;
            case "hard":
                segundosPorPregunta = 10;
                break;
            default:
                segundosPorPregunta = 6;
                break;
        }
        return (long) cantidad * segundosPorPregunta * 1000;
    }

    private void irAResultados() {
        temporizadorActivo = false;

        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("correctas", correctas);
        intent.putExtra("incorrectas", incorrectas);
        intent.putExtra("noRespondidas", noRespondidas + (preguntas.size() - actual));
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        temporizadorActivo = false;
        if (hiloTemporizador != null && hiloTemporizador.isAlive()) {
            hiloTemporizador.interrupt();
        }
    }
}
