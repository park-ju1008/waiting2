package com.example.juyoung.waiting2;

public class MultiInfo {
    public static final int FILTER_TYPE=0;
    public static final int SHOP_TYPE=1;

    public int type;
    public Shop data;

    public MultiInfo(int type, Shop data) {
        this.type = type;
        this.data = data;
    }
}
