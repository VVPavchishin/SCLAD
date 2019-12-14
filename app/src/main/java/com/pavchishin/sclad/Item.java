package com.pavchishin.sclad;

import java.io.Serializable;

public class Item implements Serializable {
    private int folderRecourse;
    private String name;
    private String date;

    public Item(int folderRecourse, String name, String date) {
        this.folderRecourse = folderRecourse;
        this.name = name;
        this.date = date;
    }

    public int getFolderRecourse() {
        return folderRecourse;
    }

    public void setFolderRecourse(int folderRecourse) {
        this.folderRecourse = folderRecourse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
