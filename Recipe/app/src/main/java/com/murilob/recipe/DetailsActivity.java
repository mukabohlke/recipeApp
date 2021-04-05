package com.murilob.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {
    private TextView title;
    private ImageView image;
    private ImageView back;
    private ImageView like;
    private ImageView share;
    private ListView listView;
    private IngredientsAdapter ingredientsAdapter;
    private DataAdapter dataAdapter;
    private RecyclerView listTwoRecipes;
    private Button site;
    private TextView fixedIngredients;
    private TextView fixedMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        title = (TextView) findViewById(R.id.details_title_id);
        image = (ImageView) findViewById(R.id.details_card_image_id);
        back = (ImageView) findViewById(R.id.details_back_id);
        listView = (ListView) findViewById(R.id.details_list_ingredients);
        like = (ImageView) findViewById(R.id.details_favorite_id);
        listTwoRecipes = (RecyclerView) findViewById(R.id.details_listTwoRecipes_id);
        site = (Button) findViewById(R.id.details_acess_site_id);
        share = (ImageView) findViewById(R.id.details_share_id);
        fixedIngredients = (TextView) findViewById(R.id.details_fixed_ingredients_id);
        fixedMore = (TextView) findViewById(R.id.details_fixed_more_id);

        //Modo escuro
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES) {
            title.setTextColor(ContextCompat.getColor(this, R.color.white));
            fixedIngredients.setTextColor(ContextCompat.getColor(this, R.color.white));
            fixedMore.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        Intent intent = getIntent();

        //montar informações e mostrar na tela
        String thumb = intent.getExtras().getString("thumbnail");
        if( thumb.equals("")){
            thumb ="https://img.elo7.com.br/product/original/22565B3/adesivo-parede-prato-comida-frango-salada-restaurante-lindo-adesivo-parede.jpg";
        }
        Glide.with(this).load(thumb).into(image);

        title.setText(intent.getExtras().getString("title"));

        String resp = intent.getExtras().getString("ingredients").replaceAll("\\s","");
        String[] parts = resp.split(",");

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < parts.length; ++i) {
            list.add(parts[i]);
        }

        ingredientsAdapter = new IngredientsAdapter(this, list);
        listView.setAdapter(ingredientsAdapter);

        //função para definir a altura do listview, para evitar problemas com o scroll
        setListViewHeight(listView);
        //função para acessar a API e mostrar outras receitas
        getDados("");
        //função para definir se a receita já é favorita
        liked(this, intent.getExtras().getString("title"));

        //efeitos de cliques dos botões
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recuperar receitas favoritas
                Gson gson = new Gson();
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("dados", v.getContext().MODE_PRIVATE);
                String getJson = sharedPreferences.getString("receitas", "");
                SharedPreferences.Editor prefsEditor = sharedPreferences.edit();

                //desfavoritar
                ArrayList<Receita> receitas = new ArrayList<>();
                ArrayList<Receita> newReceitas = new ArrayList<>();

                //se existem favoritos
                if(!getJson.equals("")){
                    //recupera os dados
                    Type type = new TypeToken<List<Receita>>(){}.getType();
                    receitas = gson.fromJson(getJson, type);
                    //bool usado caso seja necessário desfavoritar
                    boolean write = false;

                    //retirar receita das favoritas
                    for (Receita obj : receitas) {
                        if(obj.getTitle().equals(intent.getExtras().getString("title"))){
                            like.setColorFilter(ContextCompat.getColor(v.getContext(), R.color.white));
                            write = true;
                        } else {
                            newReceitas.add(obj);
                        }
                    }

                    if(write){
                        //converter para json
                        String json = gson.toJson(newReceitas);
                        prefsEditor.putString("receitas", json);
                        prefsEditor.commit();
                        return;
                    }
                }

                //favoritar
                like.setColorFilter(ContextCompat.getColor(v.getContext(), R.color.red));

                //montar novo objeto
                Receita receita = new Receita();
                receita.setTitle(intent.getExtras().getString("title"));
                receita.setHref(intent.getExtras().getString("href"));
                receita.setIngredients(intent.getExtras().getString("ingredients"));
                receita.setThumbnail(intent.getExtras().getString("thumbnail"));

                //recuperar dados
                String getJson2 = sharedPreferences.getString("receitas", "");

                //primeiro favorito
                if(getJson2.equals("")){
                    receitas.add(receita);
                } else {
                    Type type = new TypeToken<List<Receita>>(){}.getType();
                    receitas = gson.fromJson(getJson2, type);

                    //verificar se a receita já não é favorita
                    for (Receita obj : receitas) {
                        if(obj.getTitle().equals(receita.getTitle())){
                            return;
                        }
                    }
                    //salvar novo favorito
                    receitas.add(receita);
                }
                String json2 = gson.toJson(receitas);
                prefsEditor.putString("receitas", json2);
                prefsEditor.commit();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Recipes");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Acess http://www.recipepuppy.com/ for more recipes! ;)");
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse( intent.getExtras().getString("href"));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void liked(Context c, String title){
        //recuperar receitas favoritas
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = c.getSharedPreferences("dados", c.MODE_PRIVATE);
        String getJson = sharedPreferences.getString("receitas", "");

        ArrayList<Receita> receitas = new ArrayList<>();

        if(getJson.equals("")){
            return;
        } else {
            Type type = new TypeToken<List<Receita>>(){}.getType();
            //converte de json para montar o array list
            receitas = gson.fromJson(getJson, type);

            for (Receita obj : receitas) {
                if(obj.getTitle().equals(title)){
                    like.setColorFilter(ContextCompat.getColor(c, R.color.red));
                    return;
                }
            }
        }
    }

    private void getDados(String search) {
        //definir layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        listTwoRecipes.setLayoutManager(gridLayoutManager);

        Context cont = this;

        //consumir API com retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.recipepuppy.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API api = retrofit.create(API.class);

        Call<ReceitaWrapper> call = api.getReceitas(search, "","2");

        call.enqueue(new Callback<ReceitaWrapper>() {
            @Override
            public void onResponse(Call<ReceitaWrapper> call, Response<ReceitaWrapper> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(cont,"Something wrong!",Toast.LENGTH_SHORT).show();
                    return;
                }

                //montar array list
                List<Receita> receitas = response.body().getReceita();

                if(receitas.size()==0){
                    Toast.makeText(cont,"Nothing found!",Toast.LENGTH_SHORT).show();
                }

                for(int i=0; i<receitas.size();i++){
                    dataAdapter = new DataAdapter((ArrayList<Receita>) receitas, R.layout.card_recipe_little);
                    listTwoRecipes.setAdapter(dataAdapter);
                    dataAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ReceitaWrapper> call, Throwable t) {
               Toast.makeText(cont,"Something wrong!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void setListViewHeight (ListView listView) {
        IngredientsAdapter listAdapter = (IngredientsAdapter) listView.getAdapter();

        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}