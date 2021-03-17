package com.murilob.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        //Modo escuro
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES) {
            search.setTextColor(ContextCompat.getColor(this, R.color.black));
        }

        //texto procurado
        Intent intent = getIntent();
        search.setText(intent.getExtras().getString("search"));

        //função para consumir API com o resultado da busca
        getDados(intent.getExtras().getString("search"), intent.getExtras().getString("filters"));

        //funções de clique dos botões
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FilterActivity.class);
                intent.putExtra("search", getIntent().getExtras().getString("search"));
                startActivity(intent);
                finish();
            }
        });
    }

    private void getDados(String search, String ingredients) {
        //definir layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        Context cont = this;
        listTwoRecipes.setLayoutManager(gridLayoutManager);

        //consumir API com retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.recipepuppy.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API api = retrofit.create(API.class);

        Call<ReceitaWrapper> call = api.getReceitas(search, ingredients, "1");

        call.enqueue(new Callback<ReceitaWrapper>() {
            @Override
            public void onResponse(Call<ReceitaWrapper> call, Response<ReceitaWrapper> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(cont, "Ocorreu algum erro!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //montar array list
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