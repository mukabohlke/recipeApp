package com.murilob.recipe;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    @GET("?")
    Call<ReceitaWrapper> getReceitas(@Query("q") String latlng);
}
