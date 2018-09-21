package com.example.juyoung.waiting2.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.customizing.FurnitureView;
import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.MyDataBase;
import com.example.juyoung.waiting2.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LiveSeatActivityBoss extends AppCompatActivity {
    static final int TYPE_TABLE = 0;
    static final int TYPE_EMPTY_CHAIR = 1;
    static final int TYPE_FILL_CHAIR=2;
    private MyApplication mMyApplication;
    private MyDataBase db;
    private int id;
    private String sname;
    private ArrayList<FurnitureView> fuv_list;
    private int table_counter = 0, total_chair_counter = 0,fill_chair_counter=0;
    FrameLayout mFrameLayout;
    TextView table_count, total_chair_count,fill_chair_count,shopName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_live_boss);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        sname=intent.getStringExtra("name");
        initView();
        mMyApplication=(MyApplication)getApplication();
        db = MyDataBase.getInstance(this);
        fuv_list = loadFuv_list(id);
        //가구리스트에 들어있는것이 있다면 리스너와 갯수를 세주어 배치시켜주어야함.
        //attachInfo.mHandler를 통하여 post하여 UI thread의 looper가 관리하는 큐에 도달한다.
        mFrameLayout.post(new Runnable() {
            @Override
            public void run() {
                mFrameLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                for (int i = 0; i < fuv_list.size(); i++) {
                    FurnitureView furnitureView = fuv_list.get(i);
                    if (furnitureView.getType() == TYPE_TABLE) {
                        table_counter++;
                    } else if(furnitureView.getType()==TYPE_EMPTY_CHAIR){
                        total_chair_counter++;
                        furnitureView.setOnClickListener(mOnClickListener);
                    }
                    else if(furnitureView.getType()==TYPE_FILL_CHAIR){
                        total_chair_counter++;
                        fill_chair_counter++;
                        furnitureView.setOnClickListener(mOnClickListener);
                    }
                    furnitureView.setLayoutParams(new FrameLayout.LayoutParams(mMyApplication.convertDpToPixel(furnitureView.getdWidth() * mFrameLayout.getWidth()), mMyApplication.convertDpToPixel(furnitureView.getdHeight() * mFrameLayout.getHeight())));
                    mFrameLayout.addView(furnitureView);
                    furnitureView.setRotation(furnitureView.getDegree());
                }
                try {
                    Thread.sleep(100);
                    //뷰 위치를 결정하는것이 너무 빠르게 지나가서 위치지정이 먹히므로 다른 쓰레드에서 텀을 두고 셋팅함
                    for (int i = 0; i < fuv_list.size(); i++) {
                        fuv_list.get(i).setX(fuv_list.get(i).getX() * mFrameLayout.getWidth());//+mFrameLayout.getX());
                        fuv_list.get(i).setY(fuv_list.get(i).getY() * mFrameLayout.getHeight());//+mFrameLayout.getY());
                    }
                    table_count.setText("" + table_counter);
                    total_chair_count.setText("" + total_chair_counter);
                    fill_chair_count.setText(""+fill_chair_counter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    private final FurnitureView.OnClickListener mOnClickListener=new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            FurnitureView furnitureView=(FurnitureView)view;
            switch (furnitureView.getType()){
                case TYPE_EMPTY_CHAIR:
                    furnitureView.setType(TYPE_FILL_CHAIR);
                    fill_chair_count.setText(""+(++fill_chair_counter));
                    break;
                case TYPE_FILL_CHAIR:
                    furnitureView.setType(TYPE_EMPTY_CHAIR);
                    fill_chair_count.setText(""+(--fill_chair_counter));
                    break;
                    default:
            }
            furnitureView.invalidate();
            saveFuv_list(id,fuv_list);
            Toast.makeText(view.getContext(),"상태저장",Toast.LENGTH_SHORT).show();
        }
    };

    private void initView() {
        table_count = (TextView) findViewById(R.id.table_count);
        total_chair_count = (TextView) findViewById(R.id.totalchair_count);
        fill_chair_count=(TextView)findViewById(R.id.fillchair_count);
        mFrameLayout = (FrameLayout) findViewById(R.id.seat_layout);
        shopName=(TextView)findViewById(R.id.shopname);
        shopName.setText(sname);
    }


    private ArrayList<FurnitureView> loadFuv_list(int dbKey) {
        ArrayList<FurnitureView> fur_list = new ArrayList();
        //데이터베이스로부터 저장된 jsonarray형태의 스트링 데이터를 가져옴
        String json = db.loadSeat(dbKey);
        //데이터베이스에 저장된 데이터가 있다면
        if (json != null) {
            com.google.gson.JsonParser parser = new com.google.gson.JsonParser();

            JsonArray furniture_Array = (JsonArray) parser.parse(json);
            for (int i = 0; i < furniture_Array.size(); i++) {
                JsonObject object = (JsonObject) furniture_Array.get(i);
                fur_list.add(new FurnitureView(this, object.get("type").getAsInt(), object.get("x").getAsFloat(), object.get("y").getAsFloat(), object.get("width").getAsFloat(), object.get("height").getAsFloat(),object.get("degree").getAsInt()));
            }

        }
        return fur_list;
    }

    private void saveFuv_list(int dbKey, ArrayList<FurnitureView> list) {
        //JSONArray 객체를 생성하여 배치된 가구 리스트를 JSONArray에 넣는다.
        JSONArray furniture_Array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONObject furnitureInfo = new JSONObject();
            try {
                furnitureInfo.put("type", list.get(i).getType());
                furnitureInfo.put("x", list.get(i).getX());
                furnitureInfo.put("y", list.get(i).getY());
                furnitureInfo.put("width", list.get(i).getdWidth());
                furnitureInfo.put("height", list.get(i).getdHeight());
                furnitureInfo.put("degree", list.get(i).getDegree());
                furniture_Array.put(furnitureInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //디비에 저장
        db.saveSeat(dbKey, furniture_Array.toString());
    }



}
