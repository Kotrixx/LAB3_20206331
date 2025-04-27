package com.example.lab3_20206331.data.repository;

import androidx.annotation.NonNull;

import com.example.lab3_20206331.data.api.RetrofitClient;
import com.example.lab3_20206331.data.api.TriviaApiService;
import com.example.lab3_20206331.data.model.Question;
import com.example.lab3_20206331.data.model.TriviaResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TriviaRepository {

    public interface OnPreguntasRecibidas {
        void onSuccess(List<Question> result);
        void onFailure(Throwable t);
    }

    public void obtenerPreguntas(int cantidad, int categoria, String dificultad, OnPreguntasRecibidas callback) {
        TriviaApiService api = RetrofitClient.getInstance();

        Call<TriviaResponse> call = api.getQuestions(cantidad, categoria, dificultad, "multiple", "url3986");
        call.enqueue(new Callback<TriviaResponse>() {
            @Override
            public void onResponse(Call<TriviaResponse> call, Response<TriviaResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    callback.onSuccess(response.body().getResults());
                } else {
                    callback.onFailure(new Exception("Respuesta de la API vacía o incorrecta"));
                }
            }

            @Override
            public void onFailure(Call<TriviaResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


}

