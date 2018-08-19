package com.example.cjh.aboutball.listview;

/**
 * Created by cjh on 2018/7/31.
 */

public class UserListItem {

    private int imageIdLeft;

    private String name;

    private int imageIdRight;

    public UserListItem(int imageIdLeft, String name, int imageIdRight){
        this.name = name;
        this.imageIdLeft = imageIdLeft;
        this.imageIdRight = imageIdRight;
    }

    public int getImageIdLeft() {
        return imageIdLeft;
    }

    public String getName() {
        return name;
    }

    public int getImageIdRight() {
        return imageIdRight;
    }
}
