package com.example.juyoung.waiting2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class BottomSortDialog extends BottomSheetDialogFragment {
    private int curIndex;
    private BottomSortDialogListener mBottomSortDialogListener;
    TextView waiting_sort,look_sort,reply_sort,distance_sort;
    LinearLayout standard;

    public static BottomSortDialog getInstance(){
        return new BottomSortDialog();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //activity에서 onCreate같은 메소드
        View view=inflater.inflate(R.layout.bottom_sheet_sort,container,false);
        standard=(LinearLayout)view.findViewById(R.id.standard_sort);
        waiting_sort=(TextView)view.findViewById(R.id.waiting_sort);
        look_sort=(TextView)view.findViewById(R.id.look_sort);
        reply_sort=(TextView)view.findViewById(R.id.reply_sort);
        distance_sort=(TextView)view.findViewById(R.id.distance_sort);
        waiting_sort.setOnClickListener(mOnClickListener);
        look_sort.setOnClickListener(mOnClickListener);
        reply_sort.setOnClickListener(mOnClickListener);
        distance_sort.setOnClickListener(mOnClickListener);
        curIndex=Integer.parseInt(getTag());
        standard.getChildAt(curIndex).setBackgroundResource(R.drawable.sort_button);
        ((TextView)standard.getChildAt(curIndex)).setTextColor(getResources().getColor(R.color.buttonBoard));
        return view;

    }
    public void setBottomSortDialogListener(BottomSortDialogListener bottomSortDialogListener){
        this.mBottomSortDialogListener=bottomSortDialogListener;
    }

    private final TextView.OnClickListener mOnClickListener=new TextView.OnClickListener(){

        @Override
        public void onClick(View view) {
            //선택된 자식뷰 인덱스 를 알아내어 해당 자식 선택된 표시
            int index=standard.indexOfChild(view);
            if(curIndex!=index){
                //이전에 선택된 정렬 조건과 현재 선택한 정렬 조건이 다르다면
                //이전의 선택된 상태를 없애고 현재 선택한 정렬 조건에 선택 상태를 만들어 준다.
                standard.getChildAt(curIndex).setBackgroundResource(0);
                ((TextView)standard.getChildAt(curIndex)).setTextColor(Color.DKGRAY);

                standard.getChildAt(index).setBackgroundResource(R.drawable.sort_button);
                ((TextView)standard.getChildAt(index)).setTextColor(getResources().getColor(R.color.buttonBoard));
            }
            mBottomSortDialogListener.OnItemClicked(index);
        }
    };
}
