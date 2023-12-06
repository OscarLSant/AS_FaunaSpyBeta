package com.example.as_faunaspyv3;

public class AccountModel {
    String specie, location, time, date, description, img, autor;
    private int position;



    public AccountModel() {
    }

    public AccountModel(String specie, String location, String time, String date, String description, String img, String autor, int position) {
        this.specie = specie;
        this.location = location;
        this.time = time;
        this.date = date;
        this.description = description;
        this.img = img;
        this.autor = autor;
        this.position = position;
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

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
