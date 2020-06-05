package com.mawodu.levram.entities;

public class Hero {
    private int id;
    private String name;
    private String description;
    private Thumbnail thumbnail;

    public Hero(int id, String name, String description, Thumbnail thumbnail) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }
}
