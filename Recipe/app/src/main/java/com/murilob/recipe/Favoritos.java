package com.murilob.recipe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Favoritos extends Fragment {

    private RecyclerView listTwoRecipes;
    private DataAdapter dataAdapter;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView logout;
    private TextView user;
    private EditText input;
    private ImageView search;
    private LinearLayout linear;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Favoritos() {
        // Required empty public constructor
    }

    public static Favoritos newInstance(String param1, String param2) {
        Favoritos fragment = new Favoritos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_favoritos, container, false);

        listTwoRecipes = (RecyclerView) view.findViewById(R.id.favorites_recycler_id);
        logout = (ImageView) view.findViewById(R.id.favorites_logout_id);
        user = (TextView) view.findViewById(R.id.favorites_user_id);
        input = (EditText) view.findViewById(R.id.favorites_input_id);
        search = (ImageView) view.findViewById(R.id.favorites_search_id);
        linear = (LinearLayout) view.findViewById(R.id.favorites_linear_id);

        //Modo escuro
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)== Configuration.UI_MODE_NIGHT_YES) {
            linear.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.edit_text_style_dark) );
        }

        //Google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(view.getContext(), gso);

        //pegar dados do Google
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
        if (acct != null) {
            String userName ="Bem vindo, " + acct.getDisplayName();
            user.setText(userName);
        }

        //evento de clique dos botões
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.favorites_logout_id:
                        signOut();
                        break;
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = input.getText().toString();
                if (searchText.equals("")){
                    Toast.makeText(getContext(),"Texto em branco!",Toast.LENGTH_SHORT).show();
                } else{
                    input.setText("");

                    //recuperar receitas favoritas
                    Gson gson = new Gson();
                    SharedPreferences mPrefs = v.getContext().getSharedPreferences("dados", v.getContext().MODE_PRIVATE);
                    String getJson = mPrefs.getString("receitas", "");
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();

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
                        Toast.makeText(getContext(),"Nada encontrado!",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        //definir layout do recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        listTwoRecipes.setLayoutManager(gridLayoutManager);

        //recuperar receitas favoritas
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("dados", view.getContext().MODE_PRIVATE);
        String json = sharedPreferences.getString("receitas", "");

        Type type = new TypeToken<List<Receita>>(){}.getType();
        List<Receita> receitas = gson.fromJson(json, type);

        if(receitas.size()==0){
            Toast.makeText(view.getContext(),"Sem favoritos!",Toast.LENGTH_SHORT).show();
        } else {
            dataAdapter = new DataAdapter((ArrayList<Receita>) receitas, R.layout.card_recipe_little);
            listTwoRecipes.setAdapter(dataAdapter);
        }

        return view;
    }

    //logout Google
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                });
    }
}