package com.murilob.recipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private ArrayList<Receita> arrayList;
    private Context context;
    private int layout;

    public DataAdapter(ArrayList<Receita> arrayList, int layout) {
        this.arrayList = arrayList;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String image = arrayList.get(position).getThumbnail();
        if( image.equals("")){
            image ="https://img.elo7.com.br/product/original/22565B3/adesivo-parede-prato-comida-frango-salada-restaurante-lindo-adesivo-parede.jpg";
        }
        holder.titleRecipe.setText(arrayList.get(position).getTitle());
        Glide.with(context).load(image).into(holder.imgRecipe);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("title", arrayList.get(holder.getAdapterPosition()).getTitle());
                intent.putExtra("href", arrayList.get(holder.getAdapterPosition()).getHref());
                intent.putExtra("ingredients", arrayList.get(holder.getAdapterPosition()).getIngredients());
                intent.putExtra("thumbnail", arrayList.get(holder.getAdapterPosition()).getThumbnail());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgRecipe;
        TextView titleRecipe;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecipe = (ImageView)  itemView.findViewById(R.id.card_image_id);
            titleRecipe = (TextView) itemView.findViewById(R.id.title_card_id);
        }
    }
}
