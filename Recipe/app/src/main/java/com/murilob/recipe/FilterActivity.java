package com.murilob.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class FilterActivity extends AppCompatActivity {
    private ImageView back;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private CheckBox checkBox4;
    private CheckBox checkBox5;
    private CheckBox checkBox6;
    private TextView ingredients;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FilterActivity.this, ResultsActivity.class);
        intent.putExtra("search", getIntent().getExtras().getString("search"));
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        back = (ImageView) findViewById(R.id.filter_back_id);
        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
        checkBox6 = (CheckBox) findViewById(R.id.checkBox6);
        ingredients = (TextView) findViewById(R.id.filter_fixed_ingredients_id);

        //Modo escuro
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES) {
            ingredients.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //juntar checks selecionados em uma string para passar para a outra tela
                String filters="";

                if(checkBox1.isChecked()){
                   filters = checkBox1.getText().toString().toLowerCase() + ",";
                }
                if(checkBox2.isChecked()){
                    filters = filters + checkBox2.getText().toString().toLowerCase() + ",";
                }
                if(checkBox3.isChecked()){
                    filters = filters + checkBox3.getText().toString().toLowerCase() + ",";
                }
                if(checkBox4.isChecked()){
                    filters = filters + checkBox4.getText().toString().toLowerCase() + ",";
                }
                if(checkBox5.isChecked()){
                    filters = filters + checkBox5.getText().toString().toLowerCase() + ",";
                }
                if(checkBox6.isChecked()){
                    filters = filters + checkBox6.getText().toString().toLowerCase() + ",";
                }

                Intent intent = new Intent(v.getContext(), ResultsActivity.class);
                intent.putExtra("search", getIntent().getExtras().getString("search"));
                intent.putExtra("filters", (filters.substring(0, filters.length() - 1)));
                startActivity(intent);
                finish();
            }
        });
    }
}