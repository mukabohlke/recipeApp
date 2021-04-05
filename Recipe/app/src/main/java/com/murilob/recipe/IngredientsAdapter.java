package com.murilob.recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class IngredientsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;

    public IngredientsAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.list_ingredient, values);
        this.context = context;
        this.values = values;
    }
    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = null;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.list_ingredient, parent, false);

        // Displaying a textview
        TextView textView = (TextView) rowView.findViewById(R.id.ingredient_id);
        textView.setText(values.get(position));

        return rowView;
    }
}
