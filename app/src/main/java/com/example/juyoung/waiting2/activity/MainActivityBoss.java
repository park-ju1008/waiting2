package com.example.juyoung.waiting2.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivityBoss extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    Toolbar myToolbar;
    NavigationView navigationView;
    DrawerLayout mDrawerLayout;
    ImageView nav_header_profileView;
    TextView nav_header_nameText;
    TextView nav_header_phoneText;
    Button setting_button;
    Button register_button;
    Button manage_button;
    Button temp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_boss);
        initView();

        //로그인 정보를 불러온다.
        //프로필 뷰에 값들을 설정한다.
        nav_header_nameText.setText(MyApplication.user_nick);
        nav_header_phoneText.setText(MyApplication.user_phoneNum);
        if (MyApplication.user_Image != null) {
            Bitmap image = StringToBitMap(MyApplication.user_Image);
            nav_header_profileView.setImageBitmap(image);
        }

    }

    private void initView() {
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_action_menu_unselected);
        setSupportActionBar(myToolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        View nav_header_view = navigationView.getHeaderView(0);
        nav_header_profileView = (ImageView) nav_header_view.findViewById(R.id.profile_imageView);
        nav_header_nameText = (TextView) nav_header_view.findViewById(R.id.nickname_view);
        nav_header_phoneText = (TextView) nav_header_view.findViewById(R.id.phone_view);
        setting_button = (Button) nav_header_view.findViewById(R.id.setting_button);
        register_button = (Button) findViewById(R.id.register_button);
        manage_button = (Button) findViewById(R.id.manage_button);
        //버튼들에 클릭리스너를 달아준다.
        setting_button.setOnClickListener(buttonClickListener);
        register_button.setOnClickListener(buttonClickListener);
        manage_button.setOnClickListener(buttonClickListener);

        temp=findViewById(R.id.temp);
        temp.setOnClickListener(buttonClickListener);
        //네비게이션아이콘 변경을 위한 DrawerLayout의 리스너 추가
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                myToolbar.setNavigationIcon(R.drawable.ic_action_menu_selected);

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                myToolbar.setNavigationIcon(R.drawable.ic_action_menu_unselected);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        //네비게이션클릭 리스너 등록 아이콘 변경
        myToolbar.setNavigationOnClickListener(new Toolbar.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    //    myToolbar.setNavigationIcon(R.drawable.ic_action_menu_unselected);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    //    myToolbar.setNavigationIcon(R.drawable.ic_action_menu_selected);
                }
            }


        });
        //네비게이션뷰 찾아서 리스너 달아줌
        navigationView.setNavigationItemSelectedListener(this);
    }

    //문자를 Bitmap으로 변경
    public Bitmap StringToBitMap(String encodedString) {

        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }

    }


    private final Button.OnClickListener buttonClickListener = new Button.OnClickListener() {

        @Override
        public void onClick(View view) {
            Intent intent;
            int id = view.getId();
            switch (id) {
                case R.id.setting_button:
                    intent = new Intent(view.getContext(), SettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.register_button:
                    intent = new Intent(view.getContext(), RegisterActivityBoss.class);
                    startActivity(intent);
                    break;
                case R.id.manage_button:
                    intent = new Intent(view.getContext(), ManageActivityBoss.class);
                    startActivity(intent);
                    break;
                case R.id.temp:
                    intent=new Intent(view.getContext(),SeatActivityBoss.class);
                    startActivity(intent);
            }
        }
    };


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        int id = item.getItemId();
        switch (id) {
            case R.id.register:
                intent = new Intent(getApplicationContext(), RegisterActivityBoss.class);
                break;
            case R.id.manage:
                intent= new Intent(getApplicationContext(),ManageActivityBoss.class);
                break;
        }
        return false;
    }
}
