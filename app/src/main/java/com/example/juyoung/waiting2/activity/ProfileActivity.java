package com.example.juyoung.waiting2.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_ALBUM = 2;
    static final int REQUEST_IMAGE_CROP=4;
    static final int RESULT_IMAGE_CAPTURE = 3;
    private ImageView profile_image;
    private TextView nickname_view;
    private TextView phone_view;
    private Button ok_butten;
    //카메라 사진을 위한 변수
    private String imageFilePath;
    private Uri photoUri;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("기다림");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_image = (ImageView) findViewById(R.id.profile_imageView);
        nickname_view = (TextView) findViewById(R.id.nickname_view);
        phone_view = (TextView) findViewById(R.id.phone_view);
        ok_butten = (Button) findViewById(R.id.ok_button);

        //프로필 값 보여줌
        //프로필 사진 도 넣어라
        if (MyApplication.user_Image != null) {
            Bitmap image = StringToBitMap(MyApplication.user_Image);
            profile_image.setImageBitmap(image);
        }
        nickname_view.setText(MyApplication.user_nick);
        phone_view.setText(MyApplication.user_phoneNum);
        //프로필 사진 불러오기
        profile_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"사진 촬영", "앨범에서 사진 선택"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                //제목셋팅
                alertDialogBuilder.setTitle("프로필");
                //AlertDialog 셋팅
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            sendTakePhotoIntent();
                        } else {
                            //앨범에서 가져오기 선택 시
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, REQUEST_IMAGE_ALBUM);
                        }
                    }
                }).show();
            }
        });

        ok_butten.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                //이미지 뷰 사진을 bitmap으로 가져옴
                BitmapDrawable d = (BitmapDrawable) profile_image.getDrawable();
                Bitmap bitmap = d.getBitmap();

                SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("profile", BitMapToString(bitmap));
                editor.putString("nickname", nickname_view.getText().toString());
                editor.putString("phone", phone_view.getText().toString());
                editor.commit();
                MyApplication.user_Image=BitMapToString(bitmap);
                MyApplication.user_nick=nickname_view.getText().toString();
                MyApplication.user_phoneNum=phone_view.getText().toString();
                finish();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            cropImage();
        } else if (requestCode == REQUEST_IMAGE_ALBUM && resultCode == RESULT_OK) {
            try {
                photoUri=data.getData();
                profile_image.setImageURI(photoUri);

            } catch (Exception e) {

            }

        }else if(requestCode==REQUEST_IMAGE_CROP&&resultCode==RESULT_OK){
            profile_image.setImageURI(photoUri);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.v("qq", "PPPPPPPPonRestart~");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.v("qq", "PPPPPPPPonResum~");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.v("qq", "PPPPPPPPonPause~");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v("qq", "PPPPPPPPonStop~");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v("qq", "PPPPPPPPonDestory~");
        super.onDestroy();
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

    private void sendTakePhotoIntent() {
        if (checkPermission()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //인텐트를 처리할 수 있는 앱이 있는가 확인
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {

                }
                if (photoFile != null) {
                    //FileProvider은 manifest에서 추가했던 provider요소 이용해 uri를 불러오는 역할을 한다.
                    photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String imageFilename = "TEST_" + timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "waiting2");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = new File(storageDir, imageFilename);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    //이미지를 문자열로 변경
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;

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

    public void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        //URI을 활용한 작업에 지장안받기 위한 플레그 설정
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoUri, "image/*");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", photoUri);

        List<ResolveInfo> list=getPackageManager().queryIntentActivities(cropIntent,0);
        grantUriPermission(list.get(0).activityInfo.packageName,photoUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent i=new Intent(cropIntent);
        ResolveInfo res=list.get(0);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        grantUriPermission(res.activityInfo.packageName,photoUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setComponent(new ComponentName(res.activityInfo.packageName,res.activityInfo.name));
        startActivityForResult(i,REQUEST_IMAGE_CROP);
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
                                ActivityCompat.requestPermissions(ProfileActivity.this,
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
