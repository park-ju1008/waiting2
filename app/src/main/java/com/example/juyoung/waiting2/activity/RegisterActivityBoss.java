package com.example.juyoung.waiting2.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.juyoung.waiting2.CustomDayDialog;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.ShopInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class RegisterActivityBoss extends AppCompatActivity {
    private final int REQUEST_ADDRESS = 1;
    TextView name, cur_click_text, phone, location_View, explanation;
    Spinner category, detail;
    ImageButton office_hour_button, location_button;
    Button ok_button;
    private int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_boss);

        final Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("매장등록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow);
        //뷰와 연결
        name = (TextView) findViewById(R.id.name_view);
        category = (Spinner) findViewById(R.id.category_spinner);
        detail = (Spinner) findViewById(R.id.detail_spinner);
        phone = (TextView) findViewById(R.id.phone_view);
        office_hour_button = (ImageButton) findViewById(R.id.office_hour_button);
        location_button = (ImageButton) findViewById(R.id.location_button);
        location_View = (TextView) findViewById(R.id.location_View);
        explanation = (TextView) findViewById(R.id.explan_View);
        ok_button = (Button) findViewById(R.id.ok_button);

        //array 어댑터 생성
        ArrayAdapter adapter_category = ArrayAdapter.createFromResource(this, R.array.category, R.layout.custom_simple_dropdown_item_line);
        category.setAdapter(adapter_category);
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
        name.setOnFocusChangeListener(onFocusChangeListener);
        office_hour_button.setOnClickListener(imageButtonOnClickListener);
        location_button.setOnClickListener(imageButtonOnClickListener);
        ok_button.setOnClickListener(buttonOnClickListener);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

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

    private final ImageButton.OnClickListener imageButtonOnClickListener = new ImageButton.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            LinearLayout mroot;
            switch (id) {
                case R.id.remove_button:
                    mroot = (LinearLayout) findViewById(R.id.office_hour);
                    mroot.removeView((View) view.getParent());
                    count--;
                    break;
                case R.id.office_hour_button:
                    LayoutInflater mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    mroot = (LinearLayout) findViewById(R.id.office_hour);
                    mroot.addView(mInflater.inflate(R.layout.office_hour, mroot, false), count++);
                    //추가한 레이아웃 찾기
                    LinearLayout d = (LinearLayout) mroot.getChildAt(count - 1);
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

    private final Button.OnClickListener buttonOnClickListener = new Button.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.ok_button:
                    String bname = name.getText().toString();
                    //상호명을 입력 했는지 확인
                    if (bname.equals("")) {
                        TextView error = (TextView) findViewById(R.id.error_name);
                        error.setVisibility(View.VISIBLE);
                        return;
                    }
                    String bcategory = category.getSelectedItem().toString();
                    String bdetail = detail.getSelectedItem().toString();
                    String office_hour = getTotalOfficeHour(count);
                    String bphone = phone.getText().toString();
                    String blocation = location_View.getText().toString();
                    if (blocation.equals("")) {
                        TextView error = (TextView) findViewById(R.id.error_location);
                        error.setVisibility(View.VISIBLE);
                        return;
                    }
                    String bexplanation = explanation.getText().toString();
                    String region_name = null;
                    double[] coordinate = new double[2];
                    try {
                        GetCoordinate task = new GetCoordinate();
                        String address = task.execute(blocation).get();
                        JsonObject jsonInfo = getInfo(address);
                        region_name = jsonInfo.get("region_2depth_name").getAsString();
                        coordinate[0] = jsonInfo.get("x").getAsDouble();
                        coordinate[1] = jsonInfo.get("y").getAsDouble();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ShopInfo place = new ShopInfo(bname, bcategory, bdetail, office_hour, region_name, blocation, bexplanation, bphone, coordinate[0], coordinate[1], null, null, 0);
                    Intent intent = new Intent(view.getContext(), ImageActivityBoss.class);
                    //인텐트에 클래스를 추가하기위해 bundle 클래스에 담아서 직렬화 해서 추가  직렬화하는 클래스는 implements Serializable 추가해야함.
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("place", place);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADDRESS && resultCode == RESULT_OK) {
            String result = data.getStringExtra("address");
            location_View.setText(result);
            TextView error = (TextView) findViewById(R.id.error_location);
            error.setVisibility(View.GONE);
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
        if(!total.equals("")) {
            total = total.substring(0, total.length() - 1);
        }
        return total;
    }


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
