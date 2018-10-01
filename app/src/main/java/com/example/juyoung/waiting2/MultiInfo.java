package com.example.juyoung.waiting2;

public class MultiInfo {
    public static final int FILTER_TYPE=0;
    public static final int SHOP_TYPE=1;

    public int type;
    public Shop data;
    public int check_count;
    public int reply_count;
    public int distance;

    public MultiInfo(int type, Shop data) {
        this.type = type;
        this.data = data;
    }
    public MultiInfo(int type, Shop data,int check_count,int reply_count) {
        this.type = type;
        this.data = data;
        this.check_count=check_count;
        this.reply_count=reply_count;
    }
}
