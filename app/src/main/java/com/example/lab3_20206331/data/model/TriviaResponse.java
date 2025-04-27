package com.example.lab3_20206331.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TriviaResponse {

    @SerializedName("response_code")
    private int responseCode;

    @SerializedName("results")
    private List<Question> results;

    public int getResponseCode() {
        return responseCode;
    }

    public List<Question> getResults() {
        return results;
    }
}
