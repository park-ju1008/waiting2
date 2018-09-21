package com.example.juyoung.waiting2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.MyDataBase;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.Shop;
import com.example.juyoung.waiting2.adapter.ManageAdapter;

import java.util.ArrayList;

public class ManageActivityBoss extends AppCompatActivity {
    private final static int INTENT_REQUEST_GET_ITEM = 1;
    private ArrayList<Shop> mShopList;
    private MyDataBase db;
    Toolbar myToolbar;
    RecyclerView mRecyclerView;
    ManageAdapter listAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_boss);
        db = MyDataBase.getInstance(this);
        initView();
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("매장관리");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow);
        setRecyclerView();

    }


    private void initView() {
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

    }

    private void setRecyclerView() {
        //어뎁터에 넘겨줄 배열 생성 및 추가
        mShopList = db.getShopList(MyApplication.user_nick);
        //레이아웃 매니저 생성
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //레이아웃 방향 설정
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //어댑터 생성 및 데이터 전달
        listAdapter = new ManageAdapter(this, mShopList);
        mRecyclerView.setAdapter(listAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_REQUEST_GET_ITEM && resultCode == RESULT_OK) {
            int position=data.getIntExtra("position",-1);

            Shop shop=(Shop)data.getSerializableExtra("shopInfo");
            mShopList.set(position,shop); ///여기 문제 있는거 같음
            listAdapter.notifyItemChanged(position);
        }
    }
}
