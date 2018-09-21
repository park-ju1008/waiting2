package com.example.juyoung.waiting2.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.juyoung.waiting2.CustomDayDialog;
import com.example.juyoung.waiting2.MyDataBase;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.ShopInfo;
import com.example.juyoung.waiting2.activity.SearchAddressActivity;
import com.example.juyoung.waiting2.adapter.ImageAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UpdateActivityBoss extends AppCompatActivity {
    private final int REQUEST_ADDRESS = 1;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int INTENT_REQUEST_GET_SINGLE_IMAGE = 12;
    private MyDataBase db;
    private int dataBaseId;
    private ShopInfo mShopInfo;
    private ArrayList<Uri> images;
    private Uri mMian_image;
    int count = 0;
    boolean isChange;
    TextView name, cur_click_text, phone, location_View, explanation;
    Spinner category, detail;
    ImageButton office_hour_button, location_button;
    Button ok_button;
    RecyclerView rc_imageList;
    ImageAdapter listAdapter;
    ImageView mainView;
    LinearLayout office_hour_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_boss);
        Intent intent = getIntent();
        dataBaseId = intent.getIntExtra("id", -1);
        if (dataBaseId != -1) {
            db = MyDataBase.getInstance(this);
            mShopInfo = db.getShopInfo(dataBaseId);
        }
        try {
            initView();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setImageRecyclerView();


    }


    private void initView() throws InterruptedException {
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("수정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow);
        name = (TextView) findViewById(R.id.name_view);
        name.setText(mShopInfo.getName());
        category = (Spinner) findViewById(R.id.category_spinner);
        detail = (Spinner) findViewById(R.id.detail_spinner);
        //가져온 카테고리 설정
        setCategory();
        office_hour_view = (LinearLayout) findViewById(R.id.office_hour);
        //저장한 운영시간을 요일,오픈,마감으로 나누어서 배열을 만든다.
        String[] date = mShopInfo.getBusiness_hour().split(" |~|\n");
        setOffice_hour_view(date);
        phone = (TextView) findViewById(R.id.phone_view);
        phone.setText(mShopInfo.getPhone());
        office_hour_button = (ImageButton) findViewById(R.id.office_hour_button);
        location_button = (ImageButton) findViewById(R.id.location_button);
        location_View = (TextView) findViewById(R.id.location_View);
        location_View.setText(mShopInfo.getLocation());
        explanation = (TextView) findViewById(R.id.explan_View);
        explanation.setText(mShopInfo.getExplanation());
        mainView = (ImageView) findViewById(R.id.main_view);
        mainView.setImageURI(Uri.parse(mShopInfo.getMainImage()));
        rc_imageList = (RecyclerView) findViewById(R.id.recyclerView);
        ok_button = (Button) findViewById(R.id.ok_button);


        //리스너 달아줌
        //필수입력값이므로 입력되었는지 체크
        name.setOnFocusChangeListener(onFocusChangeListener);
        //운영시간 추가버튼 리스너
        office_hour_button.setOnClickListener(imageButtonOnClickListener);
        //주소 선택하는 버튼 리스너
        location_button.setOnClickListener(imageButtonOnClickListener);
        ok_button.setOnClickListener(buttonOnClickListener);

        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Config config = new Config();
                config.setSelectionLimit(1);
                ImagePickerActivity.setConfig(config);
                Intent intent = new Intent(view.getContext(), ImagePickerActivity.class);
                startActivityForResult(intent, INTENT_REQUEST_GET_SINGLE_IMAGE);
            }
        });

        Thread thread=new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        ArrayAdapter adapter_detail = null;
                        switch (position) {
                            case 0:
                                adapter_detail = ArrayAdapter.createFromResource(view.getContext(), R.array.restaurant, R.layout.custom_simple_dropdown_item_line);
                                break;
                            case 1:
                                adapter_detail = ArrayAdapter.createFromResource(view.getContext(), R.array.foodtruck, R.layout.custom_simple_dropdown_item_line);
                                break;
                            case 2:
                                adapter_detail = ArrayAdapter.createFromResource(view.getContext(), R.array.cafe, R.layout.custom_simple_dropdown_item_line);
                                break;
                            case 3:
                                adapter_detail = ArrayAdapter.createFromResource(view.getContext(), R.array.dessert, R.layout.custom_simple_dropdown_item_line);
                                break;
                            default:
                        }
                        detail.setAdapter(adapter_detail);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }
        };
        thread.start();

    }

    private void setImageRecyclerView() {
        //어뎁터에 넘겨줄 배열 데이터베이스에 저장된게 없다면 만들어서 넘기기
        String imageJson = mShopInfo.getSubImage();
        images = new ArrayList<>();
        if (!imageJson.equals("null")) {
            try {
                JSONArray subImage = new JSONArray(imageJson);
                for (int i = 0; i < subImage.length(); i++) {
                    images.add(Uri.parse((String) subImage.get(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            images.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image));
            images.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image));
            images.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image));
            images.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image));
        }
        //레이아웃 매니저 생성
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //레이아웃 방향 설정
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rc_imageList.setLayoutManager(layoutManager);
        //어댑터 생성 및 데이터 전달
        listAdapter = new ImageAdapter(this, images);
        rc_imageList.setAdapter(listAdapter);


    }

    private void setCategory() {
        //array 어댑터 생성
        ArrayAdapter adapter_category = ArrayAdapter.createFromResource(this, R.array.category, R.layout.custom_simple_dropdown_item_line);
        category.setAdapter(adapter_category);
        String[] temp = getResources().getStringArray(R.array.category);
        String shopCategory = mShopInfo.getFirst_category();
        int select = 0;
        for (int i = 0; i < temp.length; i++) {
            if (shopCategory.equals(temp[i])) {
                category.setSelection(i);
                select = i;
                break;
            }
        }
        int textArrayResId = 0;
        switch (select) {
            case 0:
                temp = getResources().getStringArray(R.array.restaurant);
                textArrayResId = R.array.restaurant;
                break;
            case 1:
                temp = getResources().getStringArray(R.array.foodtruck);
                textArrayResId = R.array.foodtruck;
                break;
            case 2:
                temp = getResources().getStringArray(R.array.cafe);
                textArrayResId = R.array.cafe;
                break;
            case 3:
                temp = getResources().getStringArray(R.array.dessert);
                textArrayResId = R.array.dessert;
                break;
            default:
        }
        ArrayAdapter adapter_detail = ArrayAdapter.createFromResource(this, textArrayResId, R.layout.custom_simple_dropdown_item_line);
        detail.setAdapter(adapter_detail);
        String shopDetail = mShopInfo.getSecond_category();
        for (int i = 0; i < temp.length; i++) {
            if (shopDetail.equals(temp[i])) {
                detail.setSelection(i);
                break;
            }
        }

    }

    private void setOffice_hour_view(String[] date) {
        //날짜,오픈,마감 이게 한 셋트 이므로 줄수를 구한다.
        int line = date.length / 3;
        LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < line; i++) {
            office_hour_view.addView(mInflater.inflate(R.layout.office_hour, office_hour_view, false), count++);
            //추가한 레이아웃 찾기
            LinearLayout d = (LinearLayout) office_hour_view.getChildAt(count - 1);
            //추가한 레이아웃 안에 뷰 연결
            TextView day = (TextView) d.findViewById(R.id.day);
            TextView open = (TextView) d.findViewById(R.id.open);
            TextView close = (TextView) d.findViewById(R.id.close);
            ImageButton remove = (ImageButton) d.findViewById(R.id.remove_button);
            day.setText(date[i * 3]);
            open.setText(date[i * 3 + 1]);
            close.setText(date[i * 3 + 2]);
            day.setOnClickListener(textOnClickListener);
            open.setOnClickListener(textOnClickListener);
            close.setOnClickListener(textOnClickListener);
            remove.setOnClickListener(imageButtonOnClickListener);
        }
    }

    private String getTotalOfficeHour(int count) {
        LinearLayout mroot = (LinearLayout) findViewById(R.id.office_hour);
        String total = new String();
        for (int i = 0; i < count; i++) {
            //추가한 레이아웃 찾기
            LinearLayout d = (LinearLayout) mroot.getChildAt(i);
            //추가한 레이아웃 안에 뷰 연결
            TextView day = (TextView) d.findViewById(R.id.day);
            TextView open = (TextView) d.findViewById(R.id.open);
            TextView close = (TextView) d.findViewById(R.id.close);
            total = total + day.getText().toString() + " " + open.getText().toString() + "~" + close.getText().toString() + "\n";
        }
        if (!total.equals("")) {
            total = total.substring(0, total.length() - 1);
        }
        return total;
    }

    //파싱해온 데이터에서 하나의 주소정보를 가져오기위한 작업 메소드
    private JsonObject getInfo(String data) {


        JsonParser jsonParser = new JsonParser();
        //JSON데이터를 넣어 JSONObjcet로 만들어준다.
        JsonObject jsonObject = (JsonObject) jsonParser.parse(data);
        JsonArray listObject = (JsonArray) jsonObject.get("documents");

        //JsonArray에서 JsonObject 뽑아냄
        JsonObject place = (JsonObject) listObject.get(0);
        jsonObject = (JsonObject) place.get("road_address");
        return jsonObject;
    }

    private final ImageButton.OnClickListener imageButtonOnClickListener = new ImageButton.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.remove_button:
                    //선택된 지우기 버튼을 클릭시 지우기 버튼의 부모뷰(LinearLayout)을 선택하여 지운다.
                    office_hour_view.removeView((View) view.getParent());
                    count--;
                    break;
                case R.id.office_hour_button:
                    LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    office_hour_view.addView(mInflater.inflate(R.layout.office_hour, office_hour_view, false), count++);
                    //추가한 레이아웃 찾기
                    LinearLayout d = (LinearLayout) office_hour_view.getChildAt(count - 1);
                    //추가한 레이아웃 안에 뷰 연결
                    TextView day = (TextView) d.findViewById(R.id.day);
                    TextView open = (TextView) d.findViewById(R.id.open);
                    TextView close = (TextView) d.findViewById(R.id.close);
                    ImageButton remove = (ImageButton) d.findViewById(R.id.remove_button);
                    //TextView day=a.findViewById(R.id.day);

                    day.setOnClickListener(textOnClickListener);
                    open.setOnClickListener(textOnClickListener);
                    close.setOnClickListener(textOnClickListener);
                    remove.setOnClickListener(imageButtonOnClickListener);
                    break;
                case R.id.location_button:
                    Intent intent = new Intent(view.getContext(), SearchAddressActivity.class);
                    startActivityForResult(intent, REQUEST_ADDRESS);
                    break;
            }
        }
    };

    private final Button.OnClickListener buttonOnClickListener = new Button.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.ok_button:
                    mShopInfo.setName(name.getText().toString());
                    //상호명을 입력 했는지 확인
                    if (mShopInfo.getName().equals("")) {
                        TextView error = (TextView) findViewById(R.id.error_name);
                        error.setVisibility(View.VISIBLE);
                        return;
                    }
                    mShopInfo.setFirst_category(category.getSelectedItem().toString());
                    mShopInfo.setSecond_category(detail.getSelectedItem().toString());
                    mShopInfo.setBusiness_hour(getTotalOfficeHour(count));
                    mShopInfo.setPhone(phone.getText().toString());
                    mShopInfo.setLocation(location_View.getText().toString());
                    if (mShopInfo.getLocation().equals("")) {
                        TextView error = (TextView) findViewById(R.id.error_location);
                        error.setVisibility(View.VISIBLE);
                        return;
                    }
                    mShopInfo.setExplanation(explanation.getText().toString());
                    try {
                        GetCoordinate task = new GetCoordinate();
                        String address = task.execute(mShopInfo.getLocation()).get();
                        JsonObject jsonInfo = getInfo(address);
                        mShopInfo.setRegion(jsonInfo.get("region_2depth_name").getAsString());
                        mShopInfo.setX(jsonInfo.get("x").getAsDouble());
                        mShopInfo.setY(jsonInfo.get("y").getAsDouble());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mMian_image != null)
                        mShopInfo.setMainImage(mMian_image.toString());
                    if (isChange) {
                        JSONArray subImage = new JSONArray();
                        for (Uri i : images)
                            subImage.put(i);
                        mShopInfo.setSubImage(subImage.toString());
                    }
                    db.update(dataBaseId, mShopInfo);
                    Intent intent = getIntent();
                    //인텐트에 클래스를 추가하기위해 bundle 클래스에 담아서 직렬화 해서 추가  직렬화하는 클래스는 implements Serializable 추가해야함.
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("shopInfo", mShopInfo);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    };

    private final TextView.OnClickListener textOnClickListener = new TextView.OnClickListener() {

        @Override
        public void onClick(View view) {
            TimePickerDialog dialog;
            int id = view.getId();
            int hour, min;
            String time;
            switch (id) {
                case R.id.day:
                    TextView text = (TextView) view;
                    CustomDayDialog customDayDialog = new CustomDayDialog(view.getContext());
                    customDayDialog.callFunction(text);
                    break;
                case R.id.open:
                    cur_click_text = (TextView) view;
                    time = cur_click_text.getText().toString();
                    hour = 0;
                    min = 0;
                    if (!time.equals("오픈 시간")) {
                        String shour = time.substring(0, time.lastIndexOf(":"));
                        String smin = time.substring(time.lastIndexOf(":") + 1);
                        hour = Integer.parseInt(shour);
                        min = Integer.parseInt(smin);
                    }
                    dialog = new TimePickerDialog(view.getContext(), timeSetListener, hour, min, true);
                    dialog.show();
                    break;
                case R.id.close:
                    cur_click_text = (TextView) view;
                    time = cur_click_text.getText().toString();
                    hour = 0;
                    min = 0;
                    if (!time.equals("마감 시간")) {
                        String shour = time.substring(0, time.lastIndexOf(":"));
                        String smin = time.substring(time.lastIndexOf(":") + 1);
                        hour = Integer.parseInt(shour);
                        min = Integer.parseInt(smin);
                    }
                    dialog = new TimePickerDialog(view.getContext(), timeSetListener, hour, min, true);
                    dialog.show();
            }
        }
    };

    private final TextView.OnFocusChangeListener onFocusChangeListener = new TextView.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            int id = view.getId();
            switch (id) {
                case R.id.name_view:
                    if (hasFocus != true) {
                        TextView name = (TextView) view;
                        TextView error_text = (TextView) findViewById(R.id.error_name);
                        if (name.getText().toString().equals("")) {
                            error_text.setVisibility(View.VISIBLE);
                        } else {
                            error_text.setVisibility(View.GONE);
                        }
                    }
                    break;
            }
        }
    };

    private final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            if (i1 < 10) {
                cur_click_text.setText(i + ":" + "0" + i1);
            } else {
                cur_click_text.setText(i + ":" + i1);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //주소 반환
        if (requestCode == REQUEST_ADDRESS && resultCode == RESULT_OK) {
            String result = data.getStringExtra("address");
            location_View.setText(result);
            TextView error = (TextView) findViewById(R.id.error_location);
            error.setVisibility(View.GONE);
        }
        //서브 이미지 반환
        else if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == RESULT_OK) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            listAdapter.setImage_uris(images);
            listAdapter.notifyDataSetChanged();
            isChange = true;
        }
        //메인 이미지 반환
        else if (requestCode == INTENT_REQUEST_GET_SINGLE_IMAGE && resultCode == RESULT_OK) {
            ArrayList<Uri> temp = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            mMian_image = temp.get(0);
            mainView.setImageURI(mMian_image);
            mainView.setScaleType(ImageView.ScaleType.FIT_XY);
            TextView error = (TextView) findViewById(R.id.error_main);
            error.setVisibility(View.GONE);
        }
    }

    private class GetCoordinate extends AsyncTask<String, Void, String> {
        String authorization = "be01df1931e10cf8bcfc55ad8d2ab2a7";
        StringBuilder sb;
        String data;

        @Override
        protected String doInBackground(String... address) {
            try {
                //주소를 저장하기위한 변수
                String apiURL = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address[0];
                //String을 URL화 한다.
                URL url = new URL(apiURL);
                //URL을 연결한 객체 생성.
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //해더 추가
                con.setRequestProperty("Authorization", "KakaoAK " + authorization);
                //GET 방식의 통신을 함.
                con.setRequestMethod("GET");


                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) {
                    //input스트림 개방하여 문자열을 담는다.
                    br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                sb = new StringBuilder();
                String line;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                con.disconnect();

                data = sb.toString();


            } catch (Exception e) {
                System.out.println(e);
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
