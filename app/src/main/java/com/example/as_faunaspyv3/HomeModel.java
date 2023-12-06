package com.example.as_faunaspyv3;

public class HomeModel {
    String specie, location, time, date, description, img, author, specieId;

    public HomeModel() {
    }

    public HomeModel(String specie, String location, String time, String date, String description, String img, String author, String specieId) {
        this.specie = specie;
        this.location = location;
        this.time = time;
        this.date = date;
        this.description = description;
        this.img = img;
        this.author = author;
        this.specieId = specieId;
    }

    public String getSpecieId() {
        return specieId;
    }

    public void setSpecieId(String specieId) {
        this.specieId = specieId;
    }

    public String getSpecie() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie = specie;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
