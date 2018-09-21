package com.example.juyoung.waiting2;

import android.app.Application;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class MyApplication extends Application {
    //변수
    public static String user_nick;
    public static String user_phoneNum;
    public static String user_Image;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //px을 dp로 변환 (px을 입력받아 dp를 리턴)
    public float convertPixelsToDp(float px) {
        Resources resources = this.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        //dp=px/(dpi/default_density=160)=px/density
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
    //dp를 px로 변환 (dp를 입력받아 px을 리턴)

    public int convertDpToPixel(float dp) {
        Resources resources = this.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        //px=db*density
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }
}
