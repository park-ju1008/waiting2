package com.example.juyoung.waiting2.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.customizing.GameView;
import com.example.juyoung.waiting2.GameItem;
import com.example.juyoung.waiting2.R;

public class GameActivity extends AppCompatActivity {
    int heart_count = 5;
    ImageView img;
    GameView mGameView;
    ConstraintLayout mConstraintLayout;
    LinearLayout mLifeLayout;
    AnimationDrawable ani;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mGameView.restartThread();
    }

    public void initView() {
        img = (ImageView) findViewById(R.id.thunder_image);
        mGameView = (GameView) findViewById(R.id.gameview);
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.root);
        mLifeLayout = (LinearLayout) findViewById(R.id.linear_life);
        mLifeLayout.setTag(heart_count);
        ani = (AnimationDrawable) img.getDrawable();
        ani.setOneShot(true);
        mConstraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    img.setX(motionEvent.getX() - img.getWidth() / 2);
                    if (ani.isRunning())
                        ani.stop();
                    ani.start();
                    int index = mGameView.getMouselist().indexOf(new GameItem(1, motionEvent.getX(), 210, 1));
                    if (index != -1) {
                        if (mGameView.getMouselist().get(index).getType() == 0) {
                            //쥐를 없애지않고 화면에서 사라질경우 하트를 감소
                            heart_count = (int) mLifeLayout.getTag();
                            heart_count--;
                            ((ImageView) mLifeLayout.getChildAt(heart_count)).setImageResource(R.drawable.like_not);
                            mLifeLayout.setTag(heart_count);
                            if (heart_count == 0) {
                                mGameView.stopThread();
                            }
                        }
                        mGameView.removeIndex(index);
                    }
                }
                return true;
            }
        });
    }
}
