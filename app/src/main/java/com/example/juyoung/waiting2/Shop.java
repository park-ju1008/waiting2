package com.example.juyoung.waiting2;

import android.net.Uri;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Shop implements Serializable {
    protected int id;
    protected String name;
    protected String region_name;
    protected String explanation;
    protected double x;
    protected double y;
    protected int waiting_num;
    protected String mainImage;

    public Shop(int id,String name, String region, String explanation, double x, double y, int waiting_num, String mainImage) {
        this.id=id;
        this.name = name;
        this.region_name = region;
        this.explanation = explanation;
        this.x = x;
        this.y = y;
        this.waiting_num = waiting_num;
        this.mainImage = mainImage;
    }

    public Shop(String name, String region, String explanation, double x, double y, int waiting_num, String mainImage) {
        this.name = name;
        this.region_name = region;
        this.explanation = explanation;
        this.x = x;
        this.y = y;
        this.waiting_num = waiting_num;
        this.mainImage = mainImage;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region_name;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getMainImage() {
        return mainImage;
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWaiting_num() {
        return waiting_num;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegion(String region) {
        this.region_name = region;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWaiting_num(int waiting_num) {
        this.waiting_num = waiting_num;
    }


}
