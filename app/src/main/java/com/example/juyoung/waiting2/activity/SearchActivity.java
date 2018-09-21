package com.example.juyoung.waiting2.activity;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    final static int TOT_PAGE_COUNT=4;
    private int currentPage=0;
    private ArrayList<MultiInfo> items;
    private ArrayList<Shop> itemArrayList; //리사이클러뷰에 들어가는 데이터
    private MyDataBase db;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
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
//        구글 마지막 위치 구하ㄴ는거
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

//        Intent intent = getIntent();
//        double longitude = intent.getDoubleExtra("longitude", 126.978371);
//        double latitude = intent.getDoubleExtra("latitude", 37.5666091);

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
        mViewPager=(ViewPager)findViewById(R.id.viewPager);
    }

    private void setRecyclerView() {
        itemArrayList = db.getRegoinShopList("강서구");
        items = new ArrayList<>();
        for (Shop a : itemArrayList) {
            items.add(new MultiInfo(2, a));
        }
        items.add(0, new MultiInfo(1, null));
        ArrayList img = new ArrayList();

        img.add(R.drawable.ad1);
        img.add(R.drawable.ad2);
        img.add(R.drawable.ad5);
        img.add(R.drawable.ad4);

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
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
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
        super.onResume();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            Log.v("loc", "" + mLastLocation.getLatitude());
            mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
