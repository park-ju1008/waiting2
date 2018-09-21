package com.example.juyoung.waiting2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.juyoung.waiting2.R;

import java.util.ArrayList;

public class ViewPagerAdapterDetail extends PagerAdapter {
    Context mContext;
    ArrayList<Uri>mImage;

    public ViewPagerAdapterDetail(Context context,ArrayList image ) {
        mContext = context;
        mImage = image;
    }

    @Override
    public int getCount() {
        return mImage.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //ViewPager에서 사용할 뷰객체 생성 및 등록
        View view = LayoutInflater.from(mContext).inflate(R.layout.viewpager_image, null);
        ImageView img = (ImageView) view.findViewById(R.id.page_image);
        Uri image=mImage.get(position);
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        img.setImageURI(image);
        img.setColorFilter(Color.parseColor("#FFD7D7D7"), PorterDuff.Mode.MULTIPLY);
        container.addView(view);
        return view;

    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        //instantiateItem에서 생성한 객체를 이용할 것인가 여부 반환
        return view==((View)object);
    }
}
