package com.example.lab3_20206331.data.api;

// TriviaApiService.java
import com.example.lab3_20206331.data.model.TriviaResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TriviaApiService {

    @GET("api.php")
    Call<TriviaResponse> getQuestions(
            @Query("amount") int amount,
            @Query("category") int category,
            @Query("difficulty") String difficulty,
            @Query("type") String type,
            @Query("encode") String encode
    );

}
