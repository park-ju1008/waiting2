package com.example.customizing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.juyoung.waiting2.GameItem;
import com.example.juyoung.waiting2.MyApplication;
import com.example.juyoung.waiting2.R;
import com.example.juyoung.waiting2.activity.GameActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import android.os.Handler;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    final static int END_GMAE = 0;
    final static int TIMER_COUNT = 1;
    Context context;
    ArrayList<GameItem> mMouselist = new ArrayList<GameItem>();
    SurfaceHolder mHolder;
    GameThread mThread;
    Thread timeThread;
    Handler mHandler;
    boolean trun = true;
    boolean iswait = false;

    public GameView(Context context, Handler handler) {
        super(context);
        this.context = context;
        mHandler = handler;
        mHolder = getHolder(); //surface가 holder를 가지고 있기 때문에 getholder 하면됨
        mHolder.addCallback(this);//콜백 객체 등록
        mThread = new GameThread(); //스레드 객체 생성
        setTimeThread();

    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mHandler = ((GameActivity) context).getHandler();
        mHolder = getHolder(); //surface가 holder를 가지고 있기 때문에 getholder 하면됨
        mHolder.addCallback(this);//콜백 객체 등록
        mThread = new GameThread(); //스레드 객체 생성
        setTimeThread();
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //표면이 처음 생성된 직후에 호출됩니다. 이때부터 표면에 그리기가 허용됩니다.
        //     표면에는 한 스레드만 그리기를 수행할 수 있습니다.
        mThread.start();
        timeThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

        mThread.setScreenSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.v("asadf", "정지 오느");
        stopThread();
        //timeThread run() 멈춤
        pauseThread();
    }
//스레드를 재시작하는 메소드

    public void restartThread() {

        //스레드 정지
        mThread.isRun = false;
        //스레드 비우고
        mThread = null;
        //객체 다시 생성
        mThread = new GameThread();
        //화면의 폭과 높이 전달
        mThread.setScreenSize(getWidth(), getHeight());
        //스레드 시작
        mThread.start();

    }

    public void stopThread() {
        boolean tryJoin = true;
        mThread.isRun = false;
        //미쳐 종료되지 못한 스레드를 기다린다.
        while (tryJoin) {
            try {
                mThread.join();
                tryJoin = false;
            } catch (Exception e) {

            }
        }
    }

    class GameThread extends Thread {

        Canvas mCanvas;
        int mWidth, mHeight;
        Bitmap[] resize_image;
        boolean isRun = true;
        Random ran = new Random();
        MyApplication myApplication = (MyApplication) ((Activity) context).getApplication();

        public GameThread() {
            DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
            mWidth = dm.widthPixels;
            mHeight = dm.heightPixels;
            setImage();
        }

        //화면의 폭과 높이를 전달 받는다.

        public void setScreenSize(int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
            setImage();
        }

        public void run() {
            while (isRun) {

                mCanvas = mHolder.lockCanvas(); //화면정보를 다 담을때까지 화면을 잠궈 놓는다.
                //화면에 그리는 작업을 한다.
                try {
                    //동기화 블럭에서 작업을 해야한다.
                    synchronized (mHolder) {
                        //그리기위한 모든 작업을 하는 곳
                        //canvas 객체를 이용해서 반복적인 그리기 작업을 한다.
                        mCanvas.drawColor(Color.WHITE);
                        for (int i = 0; i < mMouselist.size(); i++) {
                            GameItem gameItem = mMouselist.get(i);

                            mCanvas.drawBitmap(resize_image[gameItem.getType()], gameItem.getX(), 0, null);
                        }
                        if (ran.nextInt(100) == 0) {
                            makeMouse();

                        }

                        moveMouse();

                    }

                } catch (Exception e) {
                } finally {
                    if (mCanvas != null) {
                        mHolder.unlockCanvasAndPost(mCanvas);
                    }
                }
            }

        }

        public void makeMouse() {
            float x;
            int speedX;
            if (ran.nextInt(2) == 0) {
                x = 0;
                speedX = ran.nextInt(15);
            } else {
                x = mWidth;
                speedX = ran.nextInt(15) - 15;
            }
            int type = ran.nextInt(2);
            GameItem gameItem = new GameItem(type, x, myApplication.convertDpToPixel(80), speedX);
            mMouselist.add(gameItem);
        }

        public void moveMouse() {
            Iterator<GameItem> iterator = mMouselist.iterator();
            while (iterator.hasNext()) {
                GameItem gameItem = iterator.next();
                gameItem.setX(gameItem.getX() + gameItem.getSpeedX());
                if (gameItem.getX() <= 0 || gameItem.getX() >= mWidth) {
                    if (gameItem.getType() == 1) {
                        LinearLayout life_layout = ((Activity) context).findViewById(R.id.linear_life);
                        int heart_count = (int) life_layout.getTag();
                        heart_count--;
                        ((ImageView) life_layout.getChildAt(heart_count)).setImageResource(R.drawable.like_not);
                        life_layout.setTag(heart_count);
                        if (heart_count == 0) {
                            mHandler.sendEmptyMessage(END_GMAE);
                        }
                    }
                    iterator.remove();
                }
            }

        }

        public void setImage() {
            //0번 캥커루 1번 쥐
            resize_image = new Bitmap[2];
            Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.kan);
            resize_image[0] = Bitmap.createScaledBitmap(temp, myApplication.convertDpToPixel(80), mHeight, true);
            temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.mouse);
            resize_image[1] = Bitmap.createScaledBitmap(temp, myApplication.convertDpToPixel(80), mHeight, true);
        }

    }

    public ArrayList<GameItem> getMouselist() {
        return mMouselist;
    }

    public void setMouselist(ArrayList<GameItem> list) {
        mMouselist = list;
    }


    public void removeIndex(int index) {
        this.mMouselist.remove(index);
    }

    public void setTimeThread() {
        this.timeThread = new Thread() {
            @Override
            public void run() {
                int count = 60;
                while (trun) {
                    Message msg = new Message();
                    msg.what = TIMER_COUNT;
                    msg.arg1 = count--;
                    mHandler.sendMessage(msg);
                    try {
                        Thread.sleep(1000);
                        synchronized (this) {
                            if (iswait) {
                                this.wait();
                            }
                        }
                    } catch (InterruptedException e) {
                    }

                }
            }
        };
    }

    public void stopTimeThread() {
        trun = false;
    }

    public void pauseThread() {
        iswait = true;
    }

    public void restartTimeThread() {
        synchronized (timeThread) {
            iswait = false;
            timeThread.notify();
        }
    }
}
