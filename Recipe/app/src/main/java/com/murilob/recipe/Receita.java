package com.murilob.recipe;

public class Receita {
    private String title;
    private String href;
    private String ingredients;
    private String thumbnail;

    public String getTitle() {
        return title;
    }

    public String getHref() {
        return href;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
