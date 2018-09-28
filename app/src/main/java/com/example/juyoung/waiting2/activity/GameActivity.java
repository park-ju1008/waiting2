package com.example.juyoung.waiting2.activity;

import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.customizing.GameView;
import com.example.juyoung.waiting2.CustomGameoverDialog;
import com.example.juyoung.waiting2.GameItem;
import com.example.juyoung.waiting2.R;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    final static int END_GMAE = 0;
    final static int TIMER_COUNT = 1;
    int heart_count = 0,score_count=0;
    TextView time,score;
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
        mGameView.restartTimeThread();
    }

    public void initView() {
        heart_count = 5;
        img = (ImageView) findViewById(R.id.thunder_image);
        mGameView = (GameView) findViewById(R.id.gameview);
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.root);
        mLifeLayout = (LinearLayout) findViewById(R.id.linear_life);
        time = (TextView) findViewById(R.id.time);
        score=(TextView)findViewById(R.id.score);
        score.setText(""+score_count);
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
                            //캥거루를 클릭했을시 하트감소
                            heart_count = (int) mLifeLayout.getTag();
                            heart_count--;
                            ((ImageView) mLifeLayout.getChildAt(heart_count)).setImageResource(R.drawable.like_not);
                            mLifeLayout.setTag(heart_count);
                            if(score_count>0) {
                                score_count -= 100;
                                score.setText("" + score_count);
                            }
                            if (heart_count == 0) {
                                mGameView.stopThread();
                                showDialog();
                            }
                        }else{
                            score_count+=100;
                            score.setText(""+score_count);
                        }
                        mGameView.removeIndex(index);
                    }
                }
                return true;
            }
        });
    }

    public void gameRestart() {
        heart_count = 5;
        mLifeLayout.setTag(heart_count);
        mGameView.setMouselist(new ArrayList<GameItem>());
        mGameView.restartThread();
        mGameView.setTimeThread();
        for (int i = 0; i < heart_count; i++) {
            ((ImageView) mLifeLayout.getChildAt(i)).setImageResource(R.drawable.like);
        }
    }

    public void showDialog() {
        CustomGameoverDialog gameoverDialog = new CustomGameoverDialog(this,score_count);
        Point windowSize = new Point();
        gameoverDialog.getWindow().getWindowManager().getDefaultDisplay().getSize(windowSize);
        gameoverDialog.getWindow().setLayout((int) (windowSize.x * 0.9), (int) (windowSize.y * 0.9));
        gameoverDialog.show();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case END_GMAE:
                    showDialog();
                    mGameView.stopThread();
                    mGameView.stopTimeThread();
                    break;
                case TIMER_COUNT:
                    if (msg.arg1 < 0) {
                        showDialog();
                        mGameView.stopThread();
                        mGameView.stopTimeThread();
                    } else {
                        time.setText("" + msg.arg1);
                    }
                    break;
            }
        }
    };

    public Handler getHandler() {
        return mHandler;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGameView.stopTimeThread();
    }
}
