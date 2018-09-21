package com.example.juyoung.waiting2.activity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.customizing.FurnitureView;
import com.example.juyoung.waiting2.CustomDialogListener;
import com.example.juyoung.waiting2.CustomSeatDialog;
import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.MyDataBase;
import com.example.juyoung.waiting2.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SeatActivityBoss extends AppCompatActivity {
    static final int TYPE_TABLE = 0;
    static final int TYPE_EMPTY_CHAIR = 1;
    private MyApplication mMyApplication;
    private MyDataBase db;
    private int dbkey;
    private String sname;
    private ArrayList<FurnitureView> fuv_list;
    private int table_counter = 0, chair_counter = 0;
    FurnitureView type1, type2, type3, chair, custom_type;
    FrameLayout mFrameLayout;
    ImageView trash_view;
    TextView table_count, chair_count,shopName;
    ImageButton checkButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_setting_boss);
        Intent intent = getIntent();
        dbkey = intent.getIntExtra("id", -1);
        sname=intent.getStringExtra("name");
        initView();
        mMyApplication = (MyApplication) getApplication();
        db = MyDataBase.getInstance(this);
        fuv_list = loadFuv_list(dbkey);
        //가구리스트에 들어있는것이 있다면 리스너와 갯수를 세주어 배치시켜주어야함.
        //attachInfo.mHandler를 통하여 post하여 UI thread의 looper가 관리하는 큐에 도달한다.
        //뷰가 attach 될때까지 연기된다. 따라서 view의 크기를 알아내기위해서 여기서 하는것이 맞다.
        mFrameLayout.post(new Runnable() {
            @Override
            public void run() {
                mFrameLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                for (int i = 0; i < fuv_list.size(); i++) {
                    FurnitureView furnitureView = fuv_list.get(i);
                    if (furnitureView.getType() == TYPE_TABLE) {
                        table_counter++;
                    } else {
                        chair_counter++;
                    }
                    furnitureView.setLayoutParams(new FrameLayout.LayoutParams(mMyApplication.convertDpToPixel(furnitureView.getdWidth() * mFrameLayout.getWidth()), mMyApplication.convertDpToPixel(furnitureView.getdHeight() * mFrameLayout.getHeight())));
                    mFrameLayout.addView(furnitureView);
                    furnitureView.setRotation(furnitureView.getDegree());
                    furnitureView.setOnLongClickListener(mOnLongClickListener);
                }
                try {
                    Thread.sleep(100);
                    //뷰 위치를 결정하는것이 너무 빠르게 지나가서 위치지정이 먹히므로 다른 쓰레드에서 텀을 두고 셋팅함
                    for (int i = 0; i < fuv_list.size(); i++) {
                        fuv_list.get(i).setX(fuv_list.get(i).getX() * mFrameLayout.getWidth());
                        fuv_list.get(i).setY(fuv_list.get(i).getY() * mFrameLayout.getHeight());
                    }
                    table_count.setText("" + table_counter);
                    chair_count.setText("" + chair_counter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initView() {
        type1 = (FurnitureView) findViewById(R.id.type1);
        type2 = (FurnitureView) findViewById(R.id.type2);
        type3 = (FurnitureView) findViewById(R.id.type3);
        chair = (FurnitureView) findViewById(R.id.chair);
        custom_type = (FurnitureView) findViewById(R.id.custom_type);
        shopName=(TextView)findViewById(R.id.shopname);
        shopName.setText(sname);
        table_count = (TextView) findViewById(R.id.table_count);
        table_count.setText("" + table_counter);
        chair_count = (TextView) findViewById(R.id.chair_count);
        chair_count.setText("" + chair_counter);
        mFrameLayout = (FrameLayout) findViewById(R.id.seat_layout);
        trash_view = (ImageView) findViewById(R.id.trashView);
        checkButton = (ImageButton) findViewById(R.id.checkButton);
        checkButton.setOnClickListener(mOnClickListener);
        custom_type.setOnClickListener(mOnClickListener);
        type1.setOnLongClickListener(mOnLongClickListener);
        type2.setOnLongClickListener(mOnLongClickListener);
        type3.setOnLongClickListener(mOnLongClickListener);
        chair.setOnLongClickListener(mOnLongClickListener);
        mFrameLayout.setOnDragListener(mOnDragListener);
        trash_view.setOnDragListener(mOnDragListener);
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.checkButton:
                    saveFuv_list(dbkey, fuv_list);
                    Toast.makeText(view.getContext(), "변경사항이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.custom_type:
                    CustomSeatDialog dialog = new CustomSeatDialog(view.getContext());
                    dialog.setDialogListener(new CustomDialogListener() {
                        @Override
                        public void OnPositiveClicked(int width, int height, int degree) {
                            float pWidth=mMyApplication.convertDpToPixel(30 * width);
                            float pHeight=mMyApplication.convertDpToPixel(30*height);
                            FurnitureView furnitureView = new FurnitureView(SeatActivityBoss.this, 0, 0, 0, mMyApplication.convertPixelsToDp(pWidth/mFrameLayout.getWidth()), mMyApplication.convertPixelsToDp(pHeight/mFrameLayout.getHeight()), degree);
                            furnitureView.setLayoutParams(new FrameLayout.LayoutParams(mMyApplication.convertDpToPixel(30 * width), mMyApplication.convertDpToPixel(30 * height)));
                            furnitureView.setRotation(degree);
                            mFrameLayout.addView(furnitureView);
                            table_count.setText("" + (++table_counter));
                            furnitureView.setOnLongClickListener(mOnLongClickListener);
                            fuv_list.add(furnitureView);
                        }

                        @Override
                        public void OnNegativeClicked() {

                        }
                    });
                    dialog.show();
                    break;
                default:
            }
        }
    };
    private final FurnitureView.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            //선택목록중에 있는것들 중에서 롱클릭을 할시에 클립데이터를 생성해주고
            //선택목록이 아니라 생성되어있는것을 롱클릭시 클립데이터를 null해서 드래그를 시작한다. 구별하기위한 수단
            int id = view.getId();
            ClipData clip = null;
            switch (id) {
                case R.id.type1:
                case R.id.type2:
                case R.id.type3:
                case R.id.chair:
                    clip = ClipData.newPlainText("new", "new");
                    break;
                default:
                    view.setVisibility(View.INVISIBLE);

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(clip, new View.DragShadowBuilder(view), view, 0);
            } else {
                view.startDrag(clip, new View.DragShadowBuilder(view), view, 0);
            }


            return false;
        }
    };
    private final View.OnDragListener mOnDragListener = new View.OnDragListener() {

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            int id = view.getId();
            //드래그가 좌석 배치도 위로 갔을때
            if (id == R.id.seat_layout) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.v("좌석", "" + dragEvent.getX() + "parentY" + view.getY() + "Y:" + dragEvent.getY());
                        break;
                    case DragEvent.ACTION_DROP:
                        ClipData clipData = dragEvent.getClipData();
                        //startDrag에서 넘어온 view를 가져옴
                        FurnitureView e = (FurnitureView) dragEvent.getLocalState();
                        //드롭된 위치를 사각형의 중심이라 생각하고 사각형의 왼쪽 상단의 점을 정한다.
                        float x = dragEvent.getX() - e.getWidth() / 2;
                        float y = dragEvent.getY() - e.getHeight() / 2;
                        if (clipData != null) {
                            //처음 생성할때 여기로 들어옴
                            //새로운 가구뷰를 하나 생성한다. 위치는 부모뷰로부터의 비율적 위치를 구한다.
                            FurnitureView newFurniture = new FurnitureView(view.getContext(), e.getType(), x / view.getWidth(), y / view.getHeight(), mMyApplication.convertPixelsToDp(e.getWidth()) / view.getWidth(), mMyApplication.convertPixelsToDp(e.getHeight()) / view.getHeight(), 0);

                            //높이와 너비를 목록에서 클릭한 뷰와 똑같은 크기로 지정한다.
                            newFurniture.setLayoutParams(new FrameLayout.LayoutParams(e.getWidth(), e.getHeight()));
                            newFurniture.setOnLongClickListener(mOnLongClickListener);
                            mFrameLayout.addView(newFurniture);
                            newFurniture.setX(x);
                            newFurniture.setY(y);
                            if (e.getType() == 0) {
                                table_count.setText("" + (++table_counter));
                            } else if (e.getType() == 1) {
                                chair_count.setText("" + (++chair_counter));
                            }
                            fuv_list.add(newFurniture);

                        } else {
                            //이미 생성된 가구를 옮길때 여기로 들어옴
                            e.setX(x);
                            e.setY(y);
                            //리스트에 저장되어있는 뷰의 좌표 위치를 다시 지정
                            int index = fuv_list.indexOf(e);
                            fuv_list.get(index).setmX(x / view.getWidth());
                            fuv_list.get(index).setmY(y / view.getHeight());
                            e.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }
            //드래그가 휴지통 위로 갔을때
            else if (id == R.id.trashView) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_ENTERED:
                        view.setBackgroundColor(Color.RED);
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.v("휴지통", "" + dragEvent.getX() + "Y:" + dragEvent.getY());
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.v("AFF", "버려버리기");
                        FurnitureView e = (FurnitureView) dragEvent.getLocalState();
                        ((FrameLayout) e.getParent()).removeView(e);
                        fuv_list.remove(e);
                        if (e.getType() == 0) {
                            table_count.setText("" + (--table_counter));
                        } else if (e.getType() == 1) {
                            chair_count.setText("" + (--chair_counter));
                        }
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                    case DragEvent.ACTION_DRAG_ENDED:
                        view.setBackgroundResource(R.color.colorPrimary);
                        break;
                }
            }
            return true;
        }
    };


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
                fur_list.add(new FurnitureView(this, object.get("type").getAsInt(), object.get("x").getAsFloat(), object.get("y").getAsFloat(), object.get("width").getAsFloat(), object.get("height").getAsFloat(), object.get("degree").getAsInt()));
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
