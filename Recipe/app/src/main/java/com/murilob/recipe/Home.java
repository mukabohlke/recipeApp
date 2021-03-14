package com.murilob.recipe;

import android.media.Image;
import android.os.Bundle;

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
    String searchText = "garlic";

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
        listRecipes = (RecyclerView) view.findViewById(R.id.recycler_id);
        search = (ImageView) view.findViewById(R.id.search_id);
        input = (EditText) view.findViewById(R.id.input_id);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = input.getText().toString();
                input.setText("");
                getDados(searchText);
            }
        });

        getDados(searchText);

        return view;
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
                    dataAdapter = new DataAdapter((ArrayList<Receita>) receitas);
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