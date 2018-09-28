package com.example.juyoung.waiting2.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;


import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //백버튼 두번 터치를 위한 변수 2초안에 두번 누르면 종료.
    private final long FINISH_INTERVAL_TIME = 2000;
    private final int PERMISSON_LOCATION = 1;
    //지도를 위한 변수들
    private static final int MENU_REVERSE_GEO = Menu.FIRST;
    private static final String LOG_TAG = "MapViewActivity";
    private static final String API_KEY = "c4b609e0329b71fbdd41e2a91f0d7a16";

    private MapReverseGeoCoder mReverseGeoCoder = null;
    private long backPressedTime = 0;
    private Toolbar myToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ImageView nav_header_profileView;
    private TextView nav_header_nameText;
    private TextView nav_header_phoneText;
    private Button setting_button;
    private MapView mapView;
    private ViewGroup mapViewContainer;

    //플로팅 액션 버튼을 위한 변수들
    private Animation fab_open, fab_close, fade_away_right, fade_in_right, fade_away_up, fade_in_up, fade_in_left, fade_away_left;
    private Boolean isFabOpen = false;
    private Boolean isFade = false;
    private FloatingActionButton fab, fab1, fab2, navigation_button;
    private boolean isCompass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("qq", "onCreate~");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        myToolbar = (Toolbar) findViewById(R.id.toolbar2);
        myToolbar.setNavigationIcon(R.drawable.ic_action_menu_unselected);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("기다림");
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.mapView);
        mapViewContainer.addView(mapView);
        //지도 초기 설정
        mapView.setMapType(MapView.MapType.Standard);
        //mapView 이벤트 감지
        mapView.setMapViewEventListener(mapViewEventListener);
        mapView.setPOIItemEventListener(poiItemEventListener);
        mapView.setCurrentLocationEventListener(currentLocationEventListener);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fade_away_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_away_right);
        fade_in_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_right);
        fade_away_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_away_up);
        fade_in_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_up);
        fade_in_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_left);
        fade_away_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_away_left);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        navigation_button = (FloatingActionButton) findViewById(R.id.navigation_button);

        fab.setOnClickListener(onClickListener);
        fab1.setOnClickListener(onClickListener);
        fab2.setOnClickListener(onClickListener);
        navigation_button.setOnClickListener(onClickListener);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        if (!mapView.isMapTilePersistentCacheEnabled()) {
            mapView.setMapTilePersistentCacheEnabled(true);
        }

        //로그인 정보를 불러온다.
        if (MyApplication.user_nick != null && MyApplication.user_phoneNum != null) {
            navigationView.inflateHeaderView(R.layout.drawer_header);
            //네비게이션뷰 헤더에서 프로필 변경 텍스트뷰 찾아서 리스너 달아줌
            View nav_header_view = navigationView.getHeaderView(0);
            nav_header_profileView = (ImageView) nav_header_view.findViewById(R.id.profile_imageView);
            nav_header_nameText = (TextView) nav_header_view.findViewById(R.id.nickname_view);
            nav_header_phoneText = (TextView) nav_header_view.findViewById(R.id.phone_view);
            setting_button=(Button)nav_header_view.findViewById(R.id.setting_button);
            nav_header_nameText.setText(MyApplication.user_nick);
            nav_header_phoneText.setText(MyApplication.user_phoneNum);
            if (MyApplication.user_Image != null) {
                Bitmap image = StringToBitMap(MyApplication.user_Image);
                nav_header_profileView.setImageBitmap(image);
            }

        } else {
            navigationView.inflateHeaderView(R.layout.drawer_header_login);
            View nav_header_view = navigationView.getHeaderView(0);
            setting_button=(Button)nav_header_view.findViewById(R.id.setting_button);
        }
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

        //네비게이션뷰의 헤더의 셋팅 버튼 리스너
        setting_button.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent=new Intent(setting_button.getContext(),SettingActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onStart() {
        Log.v("qq", "onStart~");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.v("qq", "onResume~");
        //위치 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        }
        super.onPause();
    }

    @Override
    protected void onRestart() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (MyApplication.user_Image != null) {
            Bitmap image = StringToBitMap(MyApplication.user_Image);
            nav_header_profileView.setImageBitmap(image);
        }
        if(MyApplication.user_nick!=null&&MyApplication.user_phoneNum!=null) {
            nav_header_nameText.setText(MyApplication.user_nick);
            nav_header_phoneText.setText(MyApplication.user_phoneNum);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (isCompass) {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                navigation_button.setImageResource(R.drawable.round_near_me_white_36dp);
                isCompass = false;

            } else {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                navigation_button.setImageResource(R.drawable.round_near_me_black_36dp);
                isCompass = true;
            }
        }
        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.v("qq", "onStop~");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v("qq", "onDestory~");
        // save map view state such as map center position and zoom level.

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            //임시로 번호표 데이터 삭제
            SharedPreferences pref = getSharedPreferences("waiting_num", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("num");
            editor.commit();
            super.onBackPressed();

        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Log.v("ASFDSD", permissions[0] + "은" + grantResults[0]);
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

    // 맵 위에서 일어나는 이벤트 처리
    private final MapView.MapViewEventListener mapViewEventListener = new MapView.MapViewEventListener() {
        @Override
        public void onMapViewInitialized(MapView mapView) {
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.5666091, 126.978371), 2, true);
        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
            MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
            Log.i(LOG_TAG, String.format("MapView onMapViewCenterPointMoved (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
        }

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i) {

        }

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
            fade_anim();
        }

        @Override
        public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
            MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
            mReverseGeoCoder = new MapReverseGeoCoder(API_KEY, mapPoint, reverseGeoCodingResultListener, MainActivity.this);
            mReverseGeoCoder.startFindingAddress();
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
//            alertDialog.setTitle("DaumMapLibrarySample");
//            alertDialog.setMessage(String.format("Double-Tap on (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
//            alertDialog.setPositiveButton("OK", null);
//            alertDialog.show();
        }

        @Override
        public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        }
    };

    private final MapView.POIItemEventListener poiItemEventListener = new MapView.POIItemEventListener() {

        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

        }
    };

    private final MapView.CurrentLocationEventListener currentLocationEventListener = new MapView.CurrentLocationEventListener() {
        @Override
        public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

        }

        @Override
        public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

        }

        @Override
        public void onCurrentLocationUpdateFailed(MapView mapView) {

        }

        @Override
        public void onCurrentLocationUpdateCancelled(MapView mapView) {

        }
    };

    private final MapReverseGeoCoder.ReverseGeoCodingResultListener reverseGeoCodingResultListener = new MapReverseGeoCoder.ReverseGeoCodingResultListener() {
        @Override
        public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {

            Toast.makeText(MainActivity.this, "Reverse Geo-coding : " + s, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
            Toast.makeText(MainActivity.this, "Reverse Geo-coding : Fail", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_REVERSE_GEO, Menu.NONE, "Reverse Geo-Coding");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int itemId = item.getItemId();

        switch (itemId) {
            case MENU_REVERSE_GEO: {
                mReverseGeoCoder = new MapReverseGeoCoder(API_KEY, mapView.getMapCenterPoint(), reverseGeoCodingResultListener, MainActivity.this);
                mReverseGeoCoder.startFindingAddress();
                return true;
            }

        }


        return super.onOptionsItemSelected(item);
    }


    private final FloatingActionButton.OnClickListener onClickListener = new FloatingActionButton.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            Intent intent;
            switch (id) {
                case R.id.fab:
                    anim();
                    break;
                case R.id.fab1:

                    MapPoint center = mapView.getMapCenterPoint();
                    double a = center.getMapPointGeoCoord().latitude;
                    Log.v("aasdf", "" + a);
                    intent = new Intent(getBaseContext(), SearchActivity.class);
                    intent.putExtra("latitude", center.getMapPointGeoCoord().latitude)
                            .putExtra("longitude", center.getMapPointGeoCoord().longitude);
                    startActivity(intent);

                    break;
                case R.id.fab2:
                    intent = new Intent(getBaseContext(), WaitingActivity.class);
                    startActivity(intent);
                    break;
                //위치 버튼을 클릭했을때
                case R.id.navigation_button:
                    if(checkLocationPermission()) {
                        LocationManager location = (LocationManager) getSystemService(LOCATION_SERVICE);

                        if (!location.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            startActivity(intent);
                        }
                        if (isCompass) {
                            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                            navigation_button.setImageResource(R.drawable.round_near_me_white_36dp);
                            isCompass = false;

                        } else {
                            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                            navigation_button.setImageResource(R.drawable.round_near_me_black_36dp);
                            isCompass = true;
                        }
                    }
                    break;
                default:
            }
        }
    };



    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    public void fade_anim() {
        if (isFade) {
            fab.startAnimation(fade_in_right);
            fab1.startAnimation(fade_in_right);
            fab2.startAnimation(fade_in_right);
            navigation_button.startAnimation(fade_in_left);
            myToolbar.startAnimation(fade_in_up);
            myToolbar.setVisibility(View.VISIBLE);
            fab.setClickable(true);
            navigation_button.setClickable(true);
            isFade = false;
        } else {
            fab.startAnimation(fade_away_right);
            fab1.startAnimation(fade_away_right);
            fab2.startAnimation(fade_away_right);
            navigation_button.startAnimation(fade_away_left);
            myToolbar.startAnimation(fade_away_up);
            myToolbar.setVisibility(View.GONE);
            navigation_button.setClickable(false);
            fab.setClickable(false);
            isFade = true;
        }
    }
    public boolean checkLocationPermission() {
        //권한 승인 아닐때
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //처음이 아닐때
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("권한 설정해")
                        .setMessage("애애")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSON_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSON_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

}



