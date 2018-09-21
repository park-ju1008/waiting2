package com.example.customizing;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.example.juyoung.waiting2.R;

public class FurnitureView extends View {
    static final int TYPE_TABLE = 0;
    static final int TYPE_EMPTY_CHAIR = 1;
    static final int TYPE_FILL_CHAIR = 2;

    private int type;
    private float x; //사각형의 중심점
    private float y;
    private float dWidth;
    private float dHeight;

    private int degree;

    private String text;
    private Paint Pnt;
    public FurnitureView(Context context, int type) {
        super(context);
        this.type = type;
        this.Pnt = new Paint();
    }

    public FurnitureView(Context context, int type, float x, float y, float dWidth, float dHeight, int degree) {
        super(context);
        this.type = type;
        this.x = x;
        this.y = y;
        this.dWidth = dWidth;
        this.dHeight = dHeight;
        this.degree=degree;
        this.Pnt = new Paint();
    }


    public FurnitureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Pnt = new Paint();
        text = context.obtainStyledAttributes(attrs, R.styleable.FurnitureView).getString(R.styleable.FurnitureView_android_text);
        type = context.obtainStyledAttributes(attrs, R.styleable.FurnitureView).getInteger(R.styleable.FurnitureView_fuv_type, 0);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //부모 레이아웃이 차일드에게 제공하는 여유 공간의 폭과 높이에 대한 정보
        //뷰의 최속폭과 높이를 기본크기로 제안하여 뷰의 기하를 측정

        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (type == TYPE_TABLE) {
            Pnt.setColor(getResources().getColor(R.color.furnitureColor));
        } else if (type == TYPE_EMPTY_CHAIR) {
            Pnt.setColor(getResources().getColor(R.color.chairColor));
            Pnt.setStrokeWidth(5);
            Pnt.setStyle(Paint.Style.STROKE);
        } else {
            Pnt.setColor(getResources().getColor(R.color.chairColor));
            Pnt.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        RectF r = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(r, 20, 20, Pnt);

        if (text != null) {
            Pnt.setTextSize(getResources().getDisplayMetrics().scaledDensity * 14);
            Pnt.setColor(Color.WHITE);
            //가로세로의 중심에 가상선에 중심이라는 뜻으로 중심에 글자를 배치하겠다라는 것이다.
            Pnt.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, getWidth() / 2, getHeight() / 2, Pnt);
        }

    }


    public int getType() {
        return type;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    public float getdWidth() {
        return dWidth;
    }

    public float getdHeight() {
        return dHeight;
    }

    public int getDegree() {
        return degree;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setmX(float x) {
        this.x = x;
    }

    public void setmY(float y) {
        this.y = y;
    }
}
