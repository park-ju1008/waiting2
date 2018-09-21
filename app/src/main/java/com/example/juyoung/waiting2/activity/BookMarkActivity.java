package com.example.juyoung.waiting2.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.customizing.EmptyRecyclerView;
import com.example.juyoung.waiting2.MyDataBase;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.Shop;
import com.example.juyoung.waiting2.adapter.BookMarkAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BookMarkActivity extends AppCompatActivity {
    final static int INTENT_REQUEST_BOOKMARK=5;
    MyDataBase db;
    private ArrayList<Shop> list_BookMarkList; //리사이클러뷰에 들어가는 데이터
    private ArrayList<Integer> list_id;
    EmptyRecyclerView recyclerView;
    ImageView empty;
    LinearLayoutManager layoutManager; //리사이클러뷰에서 필요한 레이아웃 매니저
    BookMarkAdapter bookMarkAdapter; //리사이클러뷰 어댑터


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v("BookMarkActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        initView();
        setRecyclerView();

    }

    private void initView() {
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("즐겨찾기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (EmptyRecyclerView) findViewById(R.id.recyclerView);
        empty = (ImageView) findViewById(R.id.emptyView);
    }

    private void setRecyclerView() {
        db = MyDataBase.getInstance(this);
        //저장되있는 즐겨찾기한 상점의 데이터베이스상의 식별 가능한 id 리스트를 가져온다.
        list_id = new ArrayList<>();
        list_id = loadSharedPreferencesArrayList();
        list_BookMarkList = new ArrayList<>();
        for (int i = 0; i < list_id.size(); i++) {
            Shop shop = db.getShop(list_id.get(i));
            if (shop != null)
                list_BookMarkList.add(shop);
        }

        //어댑터에 데이터 넘겨주면서 생성
        bookMarkAdapter = new BookMarkAdapter(this, list_BookMarkList,list_id);
        //레이아웃 매니저 생성
        layoutManager = new LinearLayoutManager(this);
        //레이아웃 배치방식 설정
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //리사이클러뷰와 어댑터 연결
        recyclerView.setAdapter(bookMarkAdapter);
        //리사이클러뷰에 레이아웃 매니저 설정
        recyclerView.setLayoutManager(layoutManager);
        //비였을때 보여줄 뷰를 등록한다.
        recyclerView.setEmptyView(empty);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("BookMarkActivity", "onPause");

        saveSharedPreferencesArrayList(list_id);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
//        ArrayList<Integer> newList = loadSharedPreferencesArrayList();
//        if (list_id.size() != newList.size()) {
//            list_id = newList;
//            bookMarkAdapter.setItems(list_BookMarkList);
//            bookMarkAdapter.notifyDataSetChanged();
//        }
//
//        Log.v("BookMarkActivity", "onRestart");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==INTENT_REQUEST_BOOKMARK&&resultCode==RESULT_OK){
            boolean isChange=data.getBooleanExtra("isChange",false);
            if(isChange){
                int position=data.getIntExtra("position",-1);
                list_id.remove(position);
                list_BookMarkList.remove(position);
                bookMarkAdapter.notifyItemRemoved(position);
            }
        }
    }


    private ArrayList<Integer> loadSharedPreferencesArrayList() {
        ArrayList<Integer> list;
        SharedPreferences pref = getSharedPreferences("bookmark", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("list", "");
        if (json.isEmpty()) {
            list = new ArrayList();
        } else {
            Type type = new TypeToken<ArrayList<Integer>>() {
            }.getType();
            list = gson.fromJson(json, type);
        }
        return list;
    }

    //
    private void saveSharedPreferencesArrayList(ArrayList<Integer> idList) {
        SharedPreferences pref = getSharedPreferences("bookmark", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(idList);
        Log.v("asdff", json);
        editor.putString("list", json);
        editor.commit();
    }

}
