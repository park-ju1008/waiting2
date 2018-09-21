package com.example.juyoung.waiting2.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.customizing.FurnitureView;
import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.MyDataBase;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.Reply;
import com.example.juyoung.waiting2.ShopInfo;
import com.example.juyoung.waiting2.adapter.ViewPagerAdapterDetail;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BranchActivity extends AppCompatActivity {
    static final int TYPE_TABLE = 0;
    static final int TYPE_EMPTY_CHAIR = 1;
    static final int TYPE_FILL_CHAIR = 2;
    static final int REQUEST_REPLY = 11;
    private MyApplication mMyApplication;
    private MyDataBase db;
    private ShopInfo mShopInfo;
    private int id, table_counter = 0, fillchair_counter = 0, totalchair_counter = 0;
    private ArrayList<FurnitureView> fuv_list;
    private ArrayList<Reply> reply_list;
    boolean isBookMark = false;
    boolean isChange = false;
    int reply_count=0;
    TextView firstCategory, secondCategory, business_hour, location, explanation, phone, table_count, fillchair_count, totalchair_count;
    LinearLayout root;//댓글 달기위한 부모뷰
    FrameLayout mFrameLayout;
    ViewPager mViewPager;
    Button waiting_button, reply_button;
    ArrayList<Integer> bookMark_list;
    ArrayList<Uri> subImages;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);
        mMyApplication = (MyApplication) getApplication();
        db = MyDataBase.getInstance(this);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        //아이디 값이 제대로 넘어왔는지
        if (id != -1) {
            mShopInfo = db.getShopInfo(id);
        }
        initView();
        setViewPager();
        setReply();
        //좌석배치도 불러오는 과정
        fuv_list = loadFuv_list(id);
        //가구리스트에 들어있는것이 있다면 갯수를 세주어 배치시켜주어야함.
        //attachInfo.mHandler를 통하여 post하여 UI thread의 looper가 관리하는 큐에 도달한다.
        mFrameLayout.post(new Runnable() {
            @Override
            public void run() {
                mFrameLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                for (int i = 0; i < fuv_list.size(); i++) {
                    FurnitureView furnitureView = fuv_list.get(i);
                    if (furnitureView.getType() == TYPE_TABLE) {
                        table_counter++;
                    } else if (furnitureView.getType() == TYPE_EMPTY_CHAIR) {
                        totalchair_counter++;
                    } else if (furnitureView.getType() == TYPE_FILL_CHAIR) {
                        totalchair_counter++;
                        fillchair_counter++;
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
                    totalchair_count.setText("" + totalchair_counter);
                    fillchair_count.setText("" + fillchair_counter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });
    }


    private void initView() {
        //저장된 즐겨찾기 목록을 불러온다.
        bookMark_list = loadSharedPreferencesArrayList();
        isBookMark = bookMark_list.contains(id);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout mCollapseToolBar = (CollapsingToolbarLayout) findViewById(R.id.collpaseBar);
        mCollapseToolBar.setTitle(mShopInfo.getName());
        mCollapseToolBar.setCollapsedTitleTextColor(Color.parseColor("#FFFFFF"));
        mCollapseToolBar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        root = (LinearLayout) findViewById(R.id.rootLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        firstCategory = (TextView) findViewById(R.id.firstCategory);
        firstCategory.setText(mShopInfo.getFirst_category());
        secondCategory = (TextView) findViewById(R.id.secondCategory);
        secondCategory.setText(mShopInfo.getSecond_category());
        business_hour = (TextView) findViewById(R.id.business_hour);
        business_hour.setText(mShopInfo.getBusiness_hour());
        location = (TextView) findViewById(R.id.location);
        location.setText(mShopInfo.getLocation());
        explanation = (TextView) findViewById(R.id.explanation);
        explanation.setText(mShopInfo.getExplanation());
        phone = (TextView) findViewById(R.id.phone);
        phone.setText(mShopInfo.getPhone());
        waiting_button = (Button) findViewById(R.id.waiting_button);
        //좌석배치도를 위한 설정들
        table_count = (TextView) findViewById(R.id.table_count);
        fillchair_count = (TextView) findViewById(R.id.fillchair_count);
        totalchair_count = (TextView) findViewById(R.id.totalchair_count);
        mFrameLayout = (FrameLayout) findViewById(R.id.seat_layout);
        //리플버튼
        reply_button = (Button) findViewById(R.id.reply_Button);
        reply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ReplyActivity.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, REQUEST_REPLY);
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mShopInfo.getPhone()));
                startActivity(dialIntent);
            }
        });
        waiting_button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                String channelId = "channel";
                String channelName = "Channel Name";
                int importance = NotificationManager.IMPORTANCE_LOW;

                //시스템의 알림 서비스를 사용하기위해 NotificationManager 객체를 얻음
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notificationManager.createNotificationChannel(mChannel);
                }


                //푸시 알림 터치시 실행할 작업
                Intent resultIntent = new Intent(view.getContext(), WaitingActivity.class);
                resultIntent.putExtra("position", 2);
                //앞서 생성한 작업 내용을 Notification 객체에 담기 위한 PendingIntent 객체 생성
                PendingIntent contentIntent = PendingIntent.getActivity(view.getContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(view.getContext(), channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setTicker("대기번호가 등록 되었습니다.")
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("대기번호가 등록 되었습니다.")
                        .setContentText("현재 \'" + mShopInfo.getName() + "\'의 대기번호는 " + mShopInfo.getWaiting_num() + 1 + "번 입니다.")
                        .setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);
                //알림 창에 알림
                notificationManager.notify(0, builder.build());

                //웨이팅 번호 저장을 위한 SharedPreferences 객체 생성
                SharedPreferences pref = getSharedPreferences("waiting_num", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("num", mShopInfo.getWaiting_num() + 1);
                editor.putString("name", mShopInfo.getName());
                editor.commit();
                startActivity(resultIntent);
            }
        });
    }

    private void setViewPager() {
        String imageJson = mShopInfo.getSubImage();
        subImages = new ArrayList<>();
        if (!imageJson.equals("null")) {
            try {
                JSONArray subImage = new JSONArray(imageJson);
                for (int i = 0; i < subImage.length(); i++) {
                    subImages.add(Uri.parse((String) subImage.get(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ViewPagerAdapterDetail adapter = new ViewPagerAdapterDetail(this, subImages);
        mViewPager.setAdapter(adapter);
    }

    private void setReply() {
        reply_list = db.loadReply(id);
        //댓글을 3개까지만 보여주고 3개가 넘는다면 추가적인 버튼을 생성하기 위한 조건
        boolean isEnough=false;
        reply_count=reply_list.size();
        if (reply_count == 4) {
            reply_count--;
            isEnough=true;
        }
        for (int i = 0; i < reply_count; i++) {
            View layout = LayoutInflater.from(this).inflate(R.layout.reply_item, root, false);
            TextView name = (TextView) layout.findViewById(R.id.name_view);
            TextView content = (TextView) layout.findViewById(R.id.content_view);
            final TextView like = (TextView) layout.findViewById(R.id.like_count);
            TextView date = (TextView) layout.findViewById(R.id.date_view);
            ImageView like_check=(ImageView)layout.findViewById(R.id.like_check);
            name.setText(reply_list.get(i).getNickname());
            content.setText(reply_list.get(i).getContent());
            like.setText("" + reply_list.get(i).getLike());
            date.setText(reply_list.get(i).getDate());
            like_check.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    ImageView imageview=(ImageView)view;
                    imageview.setImageResource(R.drawable.like);
                    like.setText("1");
                    LottieAnimationView l=(LottieAnimationView)findViewById(R.id.lottie);
                    l.playAnimation();
                }
            });
            root.addView(layout);
        }
        if(isEnough){
            Button button=new Button(this);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity=Gravity.RIGHT;
            button.setTextColor(Color.parseColor("#FFA14048"));
            button.setText("더 보기");
            button.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.right_arrow),null);
            button.setBackgroundColor(Color.parseColor("#00000000"));
            root.addView(button,params);
            reply_count++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bookmark_menu, menu);
        if (isBookMark) {
            menu.findItem(R.id.bookMark).setIcon(R.drawable.baseline_star_white_24dp);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: //뒤로가기 버튼 클릭

                finish();
                break;
            case R.id.location:
                Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr=내 위치&daddr=" + mShopInfo.getLocation() + "&hl=ko");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                it.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(it);
                break;
            case R.id.bookMark:
                if (isBookMark) {
                    item.setIcon(R.drawable.baseline_star_border_white_24dp);
                    Toast.makeText(this, "즐겨찾기가 해제되었습니다.", Toast.LENGTH_SHORT).show();
                    isBookMark = false;
                } else {
                    item.setIcon(R.drawable.baseline_star_white_24dp);
                    Toast.makeText(this, "즐겨찾기가 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    isBookMark = true;
                }
                isChange = !isChange;
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    //
    @Override
    protected void onPause() {
        Log.v("Branch", "onPause");
        super.onPause();
        if (isChange) {
            if (isBookMark) {
                bookMark_list.add(id);
            } else {
                bookMark_list.remove(Integer.valueOf(id));
            }
            saveSharedPreferencesArrayList(bookMark_list);
        }

    }

    @Override
    public void finish() {
        Intent intent = getIntent();
        intent.putExtra("isChange", isChange);
        setResult(RESULT_OK, intent);
        super.finish();
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
    //데이터베이스로부터 저장되어있는 좌석 배치도를 가져온다.

    private ArrayList<FurnitureView> loadFuv_list(int dbKey) {
        ArrayList<FurnitureView> fur_list = new ArrayList();
        //데이터베이스로부터 저장된 jsonarray형태의 스트링 데이터를 가져옴
        String json = db.loadSeat(dbKey);
        //데이터베이스에 저장된 데이터가 있다면
        if (json != null) {
            JsonParser parser = new JsonParser();

            JsonArray furniture_Array = (JsonArray) parser.parse(json);
            for (int i = 0; i < furniture_Array.size(); i++) {
                JsonObject object = (JsonObject) furniture_Array.get(i);
                fur_list.add(new FurnitureView(this, object.get("type").getAsInt(), object.get("x").getAsFloat(), object.get("y").getAsFloat(), object.get("width").getAsFloat(), object.get("height").getAsFloat(), object.get("degree").getAsInt()));
            }

        }
        return fur_list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REPLY && resultCode == RESULT_OK) {
            if(reply_count<3) {
                Reply reply = (Reply) data.getSerializableExtra("reply");
                View layout = LayoutInflater.from(this).inflate(R.layout.reply_item, root, false);
                TextView name = (TextView) layout.findViewById(R.id.name_view);
                TextView content = (TextView) layout.findViewById(R.id.content_view);
                TextView like = (TextView) layout.findViewById(R.id.like_count);
                TextView date = (TextView) layout.findViewById(R.id.date_view);
                name.setText(reply.getNickname());
                content.setText(reply.getContent());
                like.setText("" + reply.getLike());
                date.setText(reply.getDate());
                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                root.addView(layout);
                reply_count++;
            }
            else if(reply_count==3){
                Button button=new Button(this);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity=Gravity.RIGHT;
                button.setTextColor(Color.parseColor("#FFA14048"));
                button.setText("더 보기");
                button.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.right_arrow),null);
                button.setBackgroundColor(Color.parseColor("#00000000"));
                root.addView(button,params);
                reply_count++;
            }
        }
    }
}
