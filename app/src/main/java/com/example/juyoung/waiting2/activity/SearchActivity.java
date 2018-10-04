package com.example.juyoung.waiting2.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.juyoung.waiting2.MultiInfo;
import com.example.juyoung.waiting2.MyDataBase;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.Shop;
import com.example.juyoung.waiting2.adapter.SearchAdapter;
import com.example.juyoung.waiting2.adapter.ViewPagerAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsStates;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback mLocationCallback = null;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private ArrayList<MultiInfo> items;
    private ArrayList<Shop> itemArrayList; //리사이클러뷰에 들어가는 데이터
    private MyDataBase db;
    double latitude;
    double longitude;
    RecyclerView recyclerView;  //리사이클러뷰
    LinearLayoutManager layoutManager; //리사이클러뷰에서 필요한 레이아웃 매니저
    SearchAdapter recyclerAdpter; //리사이클러뷰 어댑터
    ImageView ImageView;
    ViewPager mViewPager;
    TextView name, region, explanation, count, distance, waiting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        db = MyDataBase.getInstance(this);
        //저장되있는 마지막 위치
        SharedPreferences pref = getSharedPreferences("location", Context.MODE_PRIVATE);
        latitude = Double.longBitsToDouble(pref.getLong("latitude", 0));
        longitude = Double.longBitsToDouble(pref.getLong("longitude", 0));

        initView();
        setRecyclerView();

    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        ImageView = (ImageView) findViewById(R.id.mainImage_View);
        name = (TextView) findViewById(R.id.name_View);
        region = (TextView) findViewById(R.id.region_View);
        explanation = (TextView) findViewById(R.id.explanation_View);
        count = (TextView) findViewById(R.id.count_View);
        distance = (TextView) findViewById(R.id.distance_View);
        waiting = (TextView) findViewById(R.id.waiting_View);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    private void setRecyclerView() {
        itemArrayList = db.getRegoinShopList("강서구");
        items = new ArrayList<>();
        for (Shop a : itemArrayList) {
            items.add(new MultiInfo(2, a, db.getLookCount(a.getId()), db.getReplyCount(a.getId())));
        }
        items.add(0, new MultiInfo(1, null));
        ArrayList img = new ArrayList();

        img.add(R.drawable.ad1);
        img.add(R.drawable.ad2);
        img.add(R.drawable.ad5);
        img.add(R.drawable.ad4);
        setDistance(items, latitude, longitude);
        items.add(0, new MultiInfo(0, null));
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this, img);
        recyclerAdpter = new SearchAdapter(this, pagerAdapter, items);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(recyclerAdpter);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.v("asadf", "onresume");

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //위치 설정이 켜져있지 않았을때 위치 허용 다이얼로그에서 선택된 결과가 넘어옴.
        LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {

            getCurrentLocation();
            SharedPreferences pref = getSharedPreferences("location", Context.MODE_PRIVATE);
            double latitude = Double.longBitsToDouble(pref.getLong("latitude", 0));
            double longitude = Double.longBitsToDouble(pref.getLong("longitude", 0));
            setDistance(items, latitude, longitude);
            recyclerAdpter.sortDistance(items);
            recyclerAdpter.setSort_curIndex(3);
            recyclerAdpter.setSort_view("거리순");
            recyclerAdpter.notifyDataSetChanged();
        }
    }


    @SuppressWarnings("MissingPermission")
    public void getCurrentLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                    return;
                }
                SharedPreferences pref = getSharedPreferences("location", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putLong("latitude", Double.doubleToRawLongBits(locationResult.getLastLocation().getLatitude()));
                editor.putLong("longitude", Double.doubleToRawLongBits(locationResult.getLastLocation().getLongitude()));
                editor.commit();
                fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            }
        };
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setNumUpdates(1);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);


    }

    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(degTorad(lat1)) * Math.sin(degTorad(lat2)) + Math.cos(degTorad(lat1)) * Math.cos(degTorad(lat2)) * Math.cos(degTorad(theta));

        dist = Math.acos(dist);
        dist = radTodeg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if (unit == "meter") {
            dist = dist * 1609.344;
        }

        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double degTorad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double radTodeg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public void setDistance(ArrayList<MultiInfo> list, double latitude, double longitude) {
        for (MultiInfo item : list) {
            if (item.data != null) {
                item.distance = (int) distance(item.data.getY(), item.data.getX(), latitude, longitude, "meter");
            }
        }
    }

}
