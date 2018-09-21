package com.example.juyoung.waiting2.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.MyDataBase;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.ShopInfo;
import com.example.juyoung.waiting2.adapter.ImageAdapter;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import org.json.JSONArray;

import java.util.ArrayList;

public class ImageActivityBoss extends AppCompatActivity {
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int INTENT_REQUEST_GET_SINGLE_IMAGE = 12;
    private static final int RESULT_IMAGE_CAPTURE=11;
    private ArrayList<Uri> images;
    private Uri mMian_image;
    private ShopInfo place;
    private MyDataBase db;
    RecyclerView rc_imageList;
    ImageAdapter listAdapter;
    ImageView mMainView;
    Button mOkButton;
    TextView mError_main;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_photo_boss);
        //인텐트로 넘긴 장소 객체 가져옴
        Intent intent=getIntent();
        place=(ShopInfo)intent.getSerializableExtra("place");
        //디비를 사용하기위해 객체 가져옴
        db=MyDataBase.getInstance(this);
        initView();
        setImageRecyclerView();

    }

    private void initView(){
        rc_imageList=(RecyclerView)findViewById(R.id.recyclerView);
        mMainView=(ImageView)findViewById(R.id.main_view);
        mOkButton=(Button)findViewById(R.id.ok_button);
        mError_main=(TextView)findViewById(R.id.error_main);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMian_image==null){
                    mError_main.setVisibility(View.VISIBLE);
                    return;
                }
                else{
                    place.setMainImage(mMian_image.toString());
                    JSONArray subImage=new JSONArray();
                    boolean isEmpty=true;
                    //서브이미지가 저장 되어있다면 서브이미지를 저장한다.
                    isEmpty=images.get(0).equals(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image));
                    if(!isEmpty){
                        for(Uri i:images)
                        subImage.put(i);
                        place.setSubImage(subImage.toString());
                    }



                    db.insert(place, MyApplication.user_nick);
                    Intent intent=new Intent(view.getContext(),ManageActivityBoss.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        mMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()) {
                    Config config = new Config();
                    config.setSelectionLimit(1);
                    ImagePickerActivity.setConfig(config);
                    Intent intent = new Intent(view.getContext(), ImagePickerActivity.class);
                    startActivityForResult(intent, INTENT_REQUEST_GET_SINGLE_IMAGE);
                }
            }
        });
    }

    private void setImageRecyclerView(){
        //어뎁터에 넘겨줄 배열 생성 및 추가
        images=new ArrayList();
        images.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image));
        images.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image));
        images.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image));
        images.add(Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.image));
        //레이아웃 매니저 생성
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        //레이아웃 방향 설정
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rc_imageList.setLayoutManager(layoutManager);
        //어댑터 생성 및 데이터 전달
        listAdapter=new ImageAdapter(this,images);
        rc_imageList.setAdapter(listAdapter);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==INTENT_REQUEST_GET_IMAGES&&resultCode==RESULT_OK){
            images=data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            listAdapter.setImage_uris(images);
            listAdapter.notifyDataSetChanged();
        }
        else if(requestCode==INTENT_REQUEST_GET_SINGLE_IMAGE&&resultCode==RESULT_OK){
            ArrayList<Uri> temp=data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            mMian_image=temp.get(0);
            mMainView.setImageURI(mMian_image);
            mMainView.setScaleType(ImageView.ScaleType.FIT_XY);
            mError_main.setVisibility(View.GONE);
        }
    }

    public boolean checkPermission() {
        //권한 승인 아닐때
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            //처음이 아닐때
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("권한이 거부되었습니다.사용을 원하시면 확인 버튼을 눌러주세요")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ImageActivityBoss.this,
                                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        RESULT_IMAGE_CAPTURE);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        RESULT_IMAGE_CAPTURE);
            }
            return false;
        } else {
            return true;
        }
    }
}
