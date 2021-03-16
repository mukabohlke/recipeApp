package com.murilob.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultsActivity extends AppCompatActivity {
    private TextView search;
    private ImageView back;
    GoogleSignInClient mGoogleSignInClient;
    private RecyclerView listTwoRecipes;
    private DataAdapter dataAdapter;
    private ImageView filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        search = (TextView) findViewById(R.id.favorites_textview_id);
        back = (ImageView) findViewById(R.id.results_back_id);
        listTwoRecipes = (RecyclerView) findViewById(R.id.results_recycler_id);
        filter = (ImageView) findViewById(R.id.results_filter_id);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = getIntent();
        search.setText(intent.getExtras().getString("search"));
        getDados(intent.getExtras().getString("search"));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void getDados(String search) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        Context cont = this;
        listTwoRecipes.setLayoutManager(gridLayoutManager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.recipepuppy.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<ReceitaWrapper> call = jsonPlaceHolderApi.getReceitas(search);

        call.enqueue(new Callback<ReceitaWrapper>() {
            @Override
            public void onResponse(Call<ReceitaWrapper> call, Response<ReceitaWrapper> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(cont, "Ocorreu algum erro!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Receita> receitas = response.body().getReceita();

                if (receitas.size() == 0) {
                    Toast.makeText(cont, "Nada encontrado!", Toast.LENGTH_SHORT).show();
                }

                for (int i = 0; i < receitas.size(); i++) {
                    dataAdapter = new DataAdapter((ArrayList<Receita>) receitas, R.layout.card_recipe_little);
                    listTwoRecipes.setAdapter(dataAdapter);
                    dataAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ReceitaWrapper> call, Throwable t) {
                Toast.makeText(cont, "Ocorreu algum erro!", Toast.LENGTH_SHORT).show();
            }

        });
    }
}