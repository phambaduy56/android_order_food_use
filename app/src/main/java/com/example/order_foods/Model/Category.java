package com.example.order_foods.Model;

public class Category {
    private String name;
    private String Images;

    public Category() {

    }

    public Category(String name, String images) {
        this.name = name;
        Images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }
}
