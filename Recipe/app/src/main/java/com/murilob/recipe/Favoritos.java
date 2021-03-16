package com.murilob.recipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Favoritos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Favoritos extends Fragment {

    private RecyclerView listTwoRecipes;
    private DataAdapter dataAdapter;
    GoogleSignInClient mGoogleSignInClient;
    private ImageView logout;
    private TextView user;
    private EditText input;
    private ImageView search;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Favoritos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Favoritos.
     */
    // TODO: Rename and change types and number of parameters
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_favoritos, container, false);

        listTwoRecipes = (RecyclerView) view.findViewById(R.id.favorites_recycler_id);
        logout = (ImageView) view.findViewById(R.id.favorites_logout_id);
        user = (TextView) view.findViewById(R.id.favorites_user_id);
        input = (EditText) view.findViewById(R.id.favorites_input_id);
        search = (ImageView) view.findViewById(R.id.favorites_search_id);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(view.getContext(), gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
        if (acct != null) {
            String userName ="Bem vindo, " + acct.getDisplayName();
            user.setText(userName);
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    // ...
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

                    Gson gson = new Gson();
                    SharedPreferences mPrefs = v.getContext().getSharedPreferences("dados", v.getContext().MODE_PRIVATE);
                    String getJson = mPrefs.getString("receitas", "");
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();

                    ArrayList<Receita> receitas = new ArrayList<>();
                    ArrayList<Receita> newReceitas = new ArrayList<>();

                    if(getJson.equals("")){

                    } else {
                        Type type = new TypeToken<List<Receita>>(){}.getType();
                        receitas = gson.fromJson(getJson, type);

                        for (Receita obj : receitas) {
                            if (obj.getTitle().toLowerCase().contains(searchText)) {
                                newReceitas.add(obj);
                            }
                        }
                        dataAdapter = new DataAdapter(newReceitas, R.layout.card_recipe_little);
                        listTwoRecipes.setAdapter(dataAdapter);
                    }
                }

            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        listTwoRecipes.setLayoutManager(gridLayoutManager);

        Gson gson = new Gson();
        SharedPreferences mPrefs = view.getContext().getSharedPreferences("dados", view.getContext().MODE_PRIVATE);
        String json = mPrefs.getString("receitas", "");

        if(json.equals("")){
            Toast.makeText(view.getContext(),"Sem favoritos!",Toast.LENGTH_SHORT).show();
        } else {
            Type type = new TypeToken<List<Receita>>(){}.getType();
            List<Receita> students = gson.fromJson(json, type);

            dataAdapter = new DataAdapter((ArrayList<Receita>) students, R.layout.card_recipe_little);
            listTwoRecipes.setAdapter(dataAdapter);
        }

        return view;
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                });
    }
}