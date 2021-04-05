package com.murilob.recipe;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends Fragment {
    private RecyclerView listRecipes;
    private DataAdapter dataAdapter;
    private ImageView search;
    private EditText input;
    private ImageView logout;
    private TextView user;
    private String searchText = "";
    private GoogleSignInClient mGoogleSignInClient;
    private LinearLayout linear;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    public Home() {
        // Required empty public constructor
    }

    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        listRecipes = (RecyclerView) view.findViewById(R.id.home_recycler_id);
        search = (ImageView) view.findViewById(R.id.home_search_id);
        input = (EditText) view.findViewById(R.id.home_textview_id);
        logout = (ImageView) view.findViewById(R.id.home_logout_id);
        user = (TextView) view.findViewById(R.id.home_user_id);
        linear = (LinearLayout) view.findViewById(R.id.home_linear_layout_id);

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
            String userName ="Welcome, " + acct.getDisplayName();
            Uri personPhoto = acct.getPhotoUrl();

            user.setText(userName);
        }

        getDados(searchText);

        //evento de clique dos botões
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = input.getText().toString();
                if (searchText.equals("")){
                    Toast.makeText(getContext(),"Blank text!",Toast.LENGTH_SHORT).show();
                } else{
                    input.setText("");
                    Intent intent = new Intent(getContext(), ResultsActivity.class);
                    intent.putExtra("search", searchText);
                    intent.putExtra("filters", "");
                    getContext().startActivity(intent);
                }
            }
        });

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

    private void getDados(String search) {
        //definir layout do recyler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        listRecipes.setLayoutManager(layoutManager);

        //consumir API com retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.recipepuppy.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API api = retrofit.create(API.class);

        Call<ReceitaWrapper> call = api.getReceitas(search,"","1");

        call.enqueue(new Callback<ReceitaWrapper>() {
            @Override
            public void onResponse(Call<ReceitaWrapper> call, Response<ReceitaWrapper> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(),"Something wrong!",Toast.LENGTH_SHORT).show();
                    return;
                }

                //montar array list
                List<Receita> receitas = response.body().getReceita();

                if(receitas.size()==0){
                    Toast.makeText(getContext(),"Nothing found!",Toast.LENGTH_SHORT).show();
                }

                for(int i=0; i<receitas.size();i++){
                    dataAdapter = new DataAdapter((ArrayList<Receita>) receitas, R.layout.card_recipe);
                    listRecipes.setAdapter(dataAdapter);
                    dataAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ReceitaWrapper> call, Throwable t) {
                Toast.makeText(getContext(),"Something wrong!",Toast.LENGTH_SHORT).show();
            }

        });
    }
}