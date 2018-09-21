package com.example.juyoung.waiting2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

//ViewHolder를 추상클래스로 만들어 각 뷰홀더에 맞게 클래스 생성
public abstract class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(View itemView) {
        super(itemView);
    }

    //각각의 뷰홀더에 맞게 셋팅하기 위해서
    abstract void bind(int position);
}