package com.murilob.recipe;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    private RecyclerView listRecipes;
    private DataAdapter dataAdapter;
    private ImageView search;
    private EditText input;
    private ImageView logout;
    private TextView user;
    String searchText = "";
    GoogleSignInClient mGoogleSignInClient;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
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


        //textViewResult = (TextView) view.findViewById(R.id.text_view_result);
        listRecipes = (RecyclerView) view.findViewById(R.id.results_recycler_id);
        search = (ImageView) view.findViewById(R.id.favorites_search_id);
        input = (EditText) view.findViewById(R.id.favorites_textview_id);
        logout = (ImageView) view.findViewById(R.id.favorites_logout_id);
        user = (TextView) view.findViewById(R.id.home_user_id);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(view.getContext(), gso);


        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
        if (acct != null) {
            String userName ="Bem vindo, " + acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

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
                searchText = input.getText().toString();
                if (searchText.equals("")){
                    Toast.makeText(getContext(),"Texto em branco!",Toast.LENGTH_SHORT).show();
                } else{
                    input.setText("");
                    Intent intent = new Intent(getContext(), ResultsActivity.class);
                    intent.putExtra("search", searchText);
                    getContext().startActivity(intent);
                }

            }
        });

        getDados(searchText);

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

    private void getDados(String search) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        listRecipes.setLayoutManager(layoutManager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.recipepuppy.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<ReceitaWrapper> call = jsonPlaceHolderApi.getReceitas(searchText);

        call.enqueue(new Callback<ReceitaWrapper>() {
            @Override
            public void onResponse(Call<ReceitaWrapper> call, Response<ReceitaWrapper> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(),"Ocorreu algum erro!",Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Receita> receitas = response.body().getReceita();

                if(receitas.size()==0){
                    Toast.makeText(getContext(),"Nada encontrado!",Toast.LENGTH_SHORT).show();
                }

                for(int i=0; i<receitas.size();i++){
                    dataAdapter = new DataAdapter((ArrayList<Receita>) receitas, R.layout.card_recipe);
                    listRecipes.setAdapter(dataAdapter);
                    dataAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onFailure(Call<ReceitaWrapper> call, Throwable t) {
                Toast.makeText(getContext(),"Ocorreu algum erro!",Toast.LENGTH_SHORT).show();
            }

        });

    }
}