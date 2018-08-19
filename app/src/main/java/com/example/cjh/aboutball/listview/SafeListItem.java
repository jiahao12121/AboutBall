package com.example.cjh.aboutball.listview;

/**
 * Created by cjh on 2018/8/1.
 */

public class SafeListItem {

    private String name;

    private int imageIdRight;

    public SafeListItem(String name, int imageIdRight){
        this.name = name;
        this.imageIdRight = imageIdRight;
    }

    public String getName() {
        return name;
    }

    public int getImageIdRight() {
        return imageIdRight;
    }
}
