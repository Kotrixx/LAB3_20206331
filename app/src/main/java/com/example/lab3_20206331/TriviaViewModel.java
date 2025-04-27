package com.example.lab3_20206331;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.lab3_20206331.data.model.Question;
import com.example.lab3_20206331.data.repository.TriviaRepository;

import java.util.List;

public class TriviaViewModel extends ViewModel {

    private final MutableLiveData<List<Question>> preguntas = new MutableLiveData<>();
    private final TriviaRepository repository = new TriviaRepository();

    public LiveData<List<Question>> getPreguntas() {
        return preguntas;
    }

    public void cargarPreguntas(int cantidad, int categoria, String dificultad) {
        repository.obtenerPreguntas(cantidad, categoria, dificultad, new TriviaRepository.OnPreguntasRecibidas() {
            @Override
            public void onSuccess(List<Question> result) {
                Log.d("TriviaViewModel", "Preguntas recibidas: " + result.size());
                preguntas.postValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("TriviaViewModel", "Error al obtener preguntas: " + t.getMessage());
                preguntas.postValue(null);
            }
        });
    }

}

