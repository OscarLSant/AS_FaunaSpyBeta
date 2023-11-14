package com.example.as_faunaspyv3;

public class SpeciesModel {
    private String name, gender, description, img;

    public SpeciesModel() {
    }

    public SpeciesModel(String name, String gender, String description, String img) {
        this.name = name;
        this.gender = gender;
        this.description = description;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
