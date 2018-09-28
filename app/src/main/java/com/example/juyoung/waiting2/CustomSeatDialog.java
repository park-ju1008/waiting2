package com.example.juyoung.waiting2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.customizing.FurnitureView;

public class CustomSeatDialog extends Dialog {
    private CustomDialogListener mCustomDialogListener;
    private Context mContext;
    private MyApplication mMyApplication;
    FurnitureView mFurnitureView;
    FrameLayout mFrameLayout;
    EditText width_text, height_text;
    Button ok_button, cancle_button;
    SeekBar mSeekBar;

    public CustomSeatDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_seat_dialog);
        mMyApplication = (MyApplication) ((Activity) mContext).getApplication();
        initView();
    }

    private void initView() {
        mFrameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        width_text = (EditText) findViewById(R.id.width_text);
        height_text = (EditText) findViewById(R.id.heigth_text);
        ok_button = (Button) findViewById(R.id.okButton);
        cancle_button = (Button) findViewById(R.id.cancleButton);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                //SeekBar의 상태가 변경되었을때 실행
                //값이 변경되면 그 값만큼 커스텀뷰 회전 시켜줌
               mFurnitureView.setRotation(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //SeekBar의 움직임이 시작되었을때 실행

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //SeekBar의 움직임이 멈추었을때 실행
            }
        });
        width_text.addTextChangedListener(mTextWatcher);
        height_text.addTextChangedListener(mTextWatcher);
        ok_button.setOnClickListener(mOnClickListener);
        cancle_button.setOnClickListener(mOnClickListener);
        mFurnitureView = new FurnitureView(mContext, 0);
        //높이와 너비를 목록에서 클릭한 뷰와 똑같은 크기로 지정한다.
        mFurnitureView.setLayoutParams(new FrameLayout.LayoutParams(mMyApplication.convertDpToPixel(30), mMyApplication.convertDpToPixel(30)));
        mFrameLayout.addView(mFurnitureView);
        mFurnitureView.setX(mMyApplication.convertDpToPixel(100) - mMyApplication.convertDpToPixel(15));
        mFurnitureView.setY(mMyApplication.convertDpToPixel(100) - mMyApplication.convertDpToPixel(15));
    }


    public void setDialogListener(CustomDialogListener customDialogListener){
        this.mCustomDialogListener=customDialogListener;
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            //EditText에 입력이 끝났을때 실행
            if (!editable.toString().isEmpty()) {
                //입력값이 있다면 실행되며 5 초과 숫자가 입력되었을경우 텍스트를 비우고 토스트메시지를 출력 아니라면 크기에 맞게 커스텀뷰 출력
                if (Integer.parseInt(editable.toString()) > 5) {
                    Toast.makeText(mContext, "5이하의 숫자까지입력가능합니다.", Toast.LENGTH_SHORT).show();
                    if (height_text.getText().hashCode() == editable.hashCode()) {
                        height_text.getText().clear();
                    } else {
                        width_text.getText().clear();
                    }
                } else if (!width_text.getText().toString().isEmpty() && !height_text.getText().toString().isEmpty()) {
                    int after_width = mMyApplication.convertDpToPixel(30 * (Float.parseFloat(width_text.getText().toString())));
                    int after_height = mMyApplication.convertDpToPixel(30 * (Float.parseFloat(height_text.getText().toString())));
                    mFurnitureView.setLayoutParams(new FrameLayout.LayoutParams(after_width, after_height));
                    mFurnitureView.setX(mMyApplication.convertDpToPixel(100) - after_width / 2);
                    mFurnitureView.setY(mMyApplication.convertDpToPixel(100) - after_height / 2);
                } else {
                    //
                    mFurnitureView.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
                }
            } else {
                mFurnitureView.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
            }
        }
    };

    private final Button.OnClickListener mOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id=view.getId();
            switch (id){
                case R.id.okButton:
                    int width=Integer.parseInt(width_text.getText().toString());
                    int height=Integer.parseInt(height_text.getText().toString());
                    int degree=mSeekBar.getProgress();
                    mCustomDialogListener.OnPositiveClicked(width,height,degree);
                    dismiss();
                    break;
                case R.id.cancleButton:
                    cancel();
                    break;
            }
        }
    };

}
