package com.example.juyoung.waiting2;

import android.net.Uri;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ShopInfo extends Shop implements Serializable {
    private String first_category;
    private String second_category;
    private String business_hour;
    private String location;
    private String phone;
    private String subImage;

    public ShopInfo(String name, String first_category, String second_category, String business_hour, String region_name, String location, String explanation, String phone, double x, double y, String mainImage, String subImage, int waiting_num) {
        super(name, region_name, explanation,x,y,waiting_num,mainImage);
        this.first_category = first_category;
        this.second_category = second_category;
        this.business_hour = business_hour;
        this.location = location;
        this.phone = phone;
        this.subImage = subImage;
    }



    @Override
    public boolean equals(Object obj) {
        ShopInfo a = (ShopInfo) obj;
        if (this.name.equals(a.name) && this.location.equals(a.location)) {
            return true;
        } else
            return false;
    }

    public String getFirst_category() {
        return first_category;
    }

    public void setFirst_category(String first_category) {
        this.first_category = first_category;
    }

    public String getSecond_category() {
        return second_category;
    }

    public void setSecond_category(String second_category) {
        this.second_category = second_category;
    }

    public String getBusiness_hour() {
        return business_hour;
    }

    public void setBusiness_hour(String business_hour) {
        this.business_hour = business_hour;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSubImage() {
        return subImage;
    }

    public void setSubImage(String subImage) {
        this.subImage = subImage;
    }
}
