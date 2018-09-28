package com.example.juyoung.waiting2.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.activity.BookMarkActivity;
import com.example.juyoung.waiting2.activity.LoginActivity;
import com.example.juyoung.waiting2.activity.MainActivity;
import com.example.juyoung.waiting2.activity.ProfileActivity;

public class SettingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar myToolbar;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private Button setting_button;
    private Button login_button;

    private ImageView nav_header_profileView;
    private TextView nav_header_nameText;
    private TextView nav_header_phoneText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_action_menu_unselected);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("설정");
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        login_button=(Button)findViewById(R.id.login_button);



        //로그인이 되어있는 상황
        if (MyApplication.user_nick != null && MyApplication.user_phoneNum != null) {
            //네비게이션뷰에 로그인 된 상황이므로 헤더를 프로필이 보이게 바꿔준다.
            navigationView.inflateHeaderView(R.layout.drawer_header);
            //네비게이션뷰 헤더에서 프로필 변경 텍스트뷰 찾아서 리스너 달아줌
            View nav_header_view = navigationView.getHeaderView(0);
            nav_header_profileView = (ImageView) nav_header_view.findViewById(R.id.profile_imageView);
            nav_header_nameText = (TextView) nav_header_view.findViewById(R.id.nickname_view);
            nav_header_phoneText = (TextView) nav_header_view.findViewById(R.id.phone_view);
            setting_button = (Button) nav_header_view.findViewById(R.id.setting_button);
            nav_header_nameText.setText(MyApplication.user_nick);
            nav_header_phoneText.setText(MyApplication.user_phoneNum);
            if (MyApplication.user_Image != null) {
                Bitmap image = StringToBitMap(MyApplication.user_Image);
                nav_header_profileView.setImageBitmap(image);
            }
//            로그인 되어있는 상황에서는 로그인 버튼은 로그아웃 표시가 되어야한다.
//            또한 계정정보에가 보이며 닉네임이 표기된다.
//            프로필 설정이 되어야하므로 리스너를 달아준다.
            login_button.setText("로그아웃");
            TextView name=(TextView)findViewById(R.id.name);
            name.setText(MyApplication.user_nick);
            TextView profile_view=(TextView)findViewById(R.id.profile_view);
            profile_view.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(view.getContext(),ProfileActivity.class);
                    startActivity(intent);
                }
            });

        } else {
            //로그아웃 되어있는 상태이므로 헤더를 프로필이 아닌 로그인이 필요합니다가 보인다.
            navigationView.inflateHeaderView(R.layout.drawer_header_login);
            View nav_header_view = navigationView.getHeaderView(0);
            setting_button = (Button) nav_header_view.findViewById(R.id.setting_button);
           /* 로그아웃 되어있는 상태이므로 로그인 버튼은 로그인으로 표시되어야한다.
            또한 계정 정보가 보이면 안된다.*/
            login_button.setText("로그인");
            LinearLayout user_info=(LinearLayout)findViewById(R.id.info_layout);
            user_info.setVisibility(View.GONE);
        }
        login_button.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent;
                Button but=(Button)view;
                if(but.getText()=="로그인"){
                    intent=new Intent(but.getContext(),LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor=pref.edit();
                    editor.remove("nickname");
                    editor.remove("phone");
                    editor.remove("profile");
                    editor.commit();
                    MyApplication.user_Image=null;
                    MyApplication.user_phoneNum=null;
                    MyApplication.user_nick=null;
                    intent=new Intent(but.getContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
        //네비게이션뷰 찾아서 리스너 달아줌
        navigationView.setNavigationItemSelectedListener(this);


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

        setting_button.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    protected void onRestart() {
        if (MyApplication.user_Image != null) {
            Bitmap image = StringToBitMap(MyApplication.user_Image);
            nav_header_profileView.setImageBitmap(image);
        }
        if(MyApplication.user_nick!=null&&MyApplication.user_phoneNum!=null) {
            nav_header_nameText.setText(MyApplication.user_nick);
            nav_header_phoneText.setText(MyApplication.user_phoneNum);
        }
        super.onRestart();
    }

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.bookMark:
                intent = new Intent(this, BookMarkActivity.class);
                startActivity(intent);
                break;
            case R.id.chat:
                intent=new Intent(this,ChatActivity.class);
                startActivity(intent);
                break;
            case R.id.game:
                intent=new Intent(this,GameActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "구현 중", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
