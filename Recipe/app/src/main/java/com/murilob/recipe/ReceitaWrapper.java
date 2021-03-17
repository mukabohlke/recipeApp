package com.murilob.recipe;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReceitaWrapper {
    @SerializedName("results")
    @Expose
    private List<Receita> receita = null;

    public List<Receita> getReceita() {
        return receita;
    }

    public void setReceita(List<Receita> receita) {
        this.receita = receita;
    }
}

