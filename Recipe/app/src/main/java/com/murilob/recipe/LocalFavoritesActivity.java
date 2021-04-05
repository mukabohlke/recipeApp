package com.murilob.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocalFavoritesActivity extends AppCompatActivity {

    private RecyclerView listTwoRecipes;
    private DataAdapter dataAdapter;
    private EditText input;
    private ImageView search;
    private ImageView home;
    private LinearLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_favorites);

        listTwoRecipes = (RecyclerView) findViewById(R.id.local_favorites_recycler_id);
        input = (EditText) findViewById(R.id.local_favorites_input_id);
        search = (ImageView) findViewById(R.id.local_favorites_search_id);
        linear = (LinearLayout) findViewById(R.id.local_favorites_linear_id);
        home = (ImageView) findViewById(R.id.local_favorites_home_id);

        //Modo escuro
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)== Configuration.UI_MODE_NIGHT_YES) {
            linear.setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_style_dark) );
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = input.getText().toString();
                if (searchText.equals("")){
                    Toast.makeText(v.getContext(),"Blank text!",Toast.LENGTH_SHORT).show();
                } else{
                    input.setText("");

                    //recuperar receitas favoritas
                    Gson gson = new Gson();
                    SharedPreferences mPrefs = v.getContext().getSharedPreferences("dados", v.getContext().MODE_PRIVATE);
                    String getJson = mPrefs.getString("receitas", "");
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();

                    if(getJson.equals("")){
                        Toast.makeText(v.getContext(),"No favorites!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<Receita> receitas = new ArrayList<>();
                    ArrayList<Receita> newReceitas = new ArrayList<>();
                    Type type = new TypeToken<List<Receita>>(){}.getType();
                    receitas = gson.fromJson(getJson, type);

                    for (Receita obj : receitas) {
                        if (obj.getTitle().toLowerCase().contains(searchText)) {
                            newReceitas.add(obj);
                        }
                        dataAdapter = new DataAdapter(newReceitas, R.layout.card_recipe_little);
                        listTwoRecipes.setAdapter(dataAdapter);
                    }
                    //nenhuma receita encontrada com o nome inserido
                    if(newReceitas.size()==0){
                        Toast.makeText(v.getContext(),"Nothing found!",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recuperar receitas favoritas
                Gson gson = new Gson();
                SharedPreferences mPrefs = v.getContext().getSharedPreferences("dados", v.getContext().MODE_PRIVATE);
                String getJson = mPrefs.getString("receitas", "");
                SharedPreferences.Editor prefsEditor = mPrefs.edit();

                if(!getJson.equals("")){
                    ArrayList<Receita> receitas = new ArrayList<>();
                    Type type = new TypeToken<List<Receita>>(){}.getType();
                    receitas = gson.fromJson(getJson, type);

                    dataAdapter = new DataAdapter(receitas, R.layout.card_recipe_little);
                    listTwoRecipes.setAdapter(dataAdapter);
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        //definir layout do recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        listTwoRecipes.setLayoutManager(gridLayoutManager);

        //recuperar receitas favoritas
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = this.getSharedPreferences("dados", this.MODE_PRIVATE);
        String json = sharedPreferences.getString("receitas", "");

        Type type = new TypeToken<List<Receita>>(){}.getType();
        List<Receita> receitas = gson.fromJson(json, type);

        if(json.equals("")){
            Toast.makeText(this,"No favorites!",Toast.LENGTH_SHORT).show();
        } else {
            dataAdapter = new DataAdapter((ArrayList<Receita>) receitas, R.layout.card_recipe_little);
            listTwoRecipes.setAdapter(dataAdapter);
        }
    }
}