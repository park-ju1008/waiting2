package com.example.juyoung.waiting2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


public class BottomFilterDialog extends BottomSheetDialogFragment {
    final static int CATE_DESSERT = 3;
    private int curindex;
    private int sel_index;
    private BottomFilterDialogListener mBottomFilterDialogListener;
    LinearLayout category;
    FrameLayout food_sel;
    TextView cancle, filter_ok, cate_all, cate_restaurant, cate_foodtruck, cate_dessert;
    ToggleButton rice, susih, chinese, western, fest, chicken, rice_cake, cocktail, cafe, iceCream, bakery;
    View food, dessert;

    public static BottomFilterDialog getInstance() {
        return new BottomFilterDialog();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_filter, container, false);
        initView(view);
        Bundle bundle = getArguments();
        curindex = bundle.getInt("curIndex");
        sel_index = bundle.getInt("sel_index",0);
        //선택된 카테고리 선택된 모양 만들어줌.
        category.getChildAt(curindex).setBackgroundResource(R.drawable.sort_button);
        ((TextView) category.getChildAt(curindex)).setTextColor(getResources().getColor(R.color.buttonBoard));
        //선택된 카테고리에 맞게 뷰를 생성해준다.
        if (curindex != CATE_DESSERT) {
            food = getLayoutInflater().inflate(R.layout.food_item, food_sel, false);
            initFoodView(food, sel_index);
            food_sel.addView(food);
        } else {
            dessert = getLayoutInflater().inflate(R.layout.dessert_item, food_sel, false);
            initDessertView(dessert, sel_index);
            food_sel.addView(dessert);
        }
        return view;
    }

    //카테고리 선택과 적용 및 취소를 위한 리스너
    private final TextView.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //선택된 뷰의 카테고리 인덱스 알아냄
            int new_index = category.indexOfChild(view);

            if (new_index != -1 && curindex != new_index) {
                //이전에 선택된 정렬 조건과 현재 선택한 정렬 조건이 다르다면
                //이전의 선택된 상태를 없애고 현재 선택한 정렬 조건에 선택 상태를 만들어 준다.
                category.getChildAt(curindex).setBackgroundResource(0);
                ((TextView) category.getChildAt(curindex)).setTextColor(getResources().getColor(R.color.not_sel_button));

                category.getChildAt(new_index).setBackgroundResource(R.drawable.sort_button);
                ((TextView) category.getChildAt(new_index)).setTextColor(getResources().getColor(R.color.buttonBoard));
            }

            //현재 선택된 뷰에 맞는 처리
            int id = view.getId();
            switch (id) {
                case R.id.cancle:
                    dismiss();
                    break;
                case R.id.filter_ok:
                    mBottomFilterDialogListener.selectFilterItem(curindex,sel_index);
                    dismiss();
                    break;
                case R.id.cate_all:
                case R.id.cate_restaurant:
                case R.id.cate_foodtruck:
                    //이전에 디저트목록이였다면 목록이 새로 바뀌여야함
                    if (curindex == CATE_DESSERT) {
                        food_sel.removeViewAt(0);
                        if(food==null){
                            food = getLayoutInflater().inflate(R.layout.food_item, food_sel, false);
                            initFoodView(food, 0);
                        }
                        food_sel.addView(food);
                    }
                    //목록이 바뀌지는 않지만 카테고리가 바뀌거나 목록이 바뀌어 선택된것들 초기화
                    if(curindex!=new_index){
                        sel_index=0;
                        rice.setChecked(false);
                        susih.setChecked(false);
                        chinese.setChecked(false);
                        western.setChecked(false);
                        fest.setChecked(false);
                        chicken.setChecked(false);
                        rice_cake.setChecked(false);
                        cocktail.setChecked(false);
                    }
                    curindex = new_index;

                    break;
                case R.id.cate_dessert:
                    if (curindex != CATE_DESSERT) {
                        food_sel.removeViewAt(0);
                        if (dessert == null) {
                            dessert = getLayoutInflater().inflate(R.layout.dessert_item, food_sel, false);
                            initDessertView(dessert, 0);
                        }
                        food_sel.addView(dessert);
                        curindex = new_index;
                        sel_index=0;
                        cocktail.setChecked(false);
                        cafe.setChecked(false);
                        iceCream.setChecked(false);
                        bakery.setChecked(false);
                    }
                    break;
            }


        }
    };
    //선택된 카테고리에 맞는 음식종류 목록들의 리스너
    private final ToggleButton.OnCheckedChangeListener toggleListener = new ToggleButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                compoundButton.setTextColor(getResources().getColor(R.color.buttonBoard));
                switch (compoundButton.getId()) {
                    case R.id.rice_button:
                        rice.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sel_rice, 0, 0);
                        sel_index|=(1<<0);
                        break;
                    case R.id.susih_button:
                        susih.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sel_sushi, 0, 0);
                        sel_index|=(1<<1);
                        break;
                    case R.id.Chinese_button:
                        chinese.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sel_noodles, 0, 0);
                        sel_index|=(1<<2);
                        break;
                    case R.id.western_button:
                        western.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sel_pizza, 0, 0);
                        sel_index|=(1<<3);
                        break;
                    case R.id.fest_button:
                        fest.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sel_fast_food, 0, 0);
                        sel_index|=(1<<4);
                        break;
                    case R.id.chicken_button:
                        chicken.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sel_chicken, 0, 0);
                        sel_index|=(1<<5);
                        break;
                    case R.id.rice_cake_button:
                        rice_cake.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sel_rice_cake, 0, 0);
                        sel_index|=(1<<6);
                        break;
                    case R.id.cocktail_button:
                        cocktail.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sel_cocktail, 0, 0);
                        sel_index|=(1<<7);
                        break;
                    case R.id.cafe_button:
                        cafe.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sel_coffee, 0, 0);
                        sel_index|=(1<<0);
                        break;
                    case R.id.icecream_button:
                        iceCream.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sel_icecream, 0, 0);
                        sel_index|=(1<<1);
                        break;
                    case R.id.bakery_button:
                        bakery.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sel_cake, 0, 0);
                        sel_index|=(1<<2);
                        break;
                }
            } else {
                compoundButton.setTextColor(getResources().getColor(R.color.not_sel_button));
                switch (compoundButton.getId()) {
                    case R.id.rice_button:
                        rice.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.rice, 0, 0);
                        sel_index&=~(1<<0);
                        break;
                    case R.id.susih_button:
                        susih.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sushi, 0, 0);
                        sel_index&=~(1<<1);
                        break;
                    case R.id.Chinese_button:
                        chinese.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.noodles, 0, 0);
                        sel_index&=~(1<<2);
                        break;
                    case R.id.western_button:
                        western.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.pizza, 0, 0);
                        sel_index&=~(1<<3);
                        break;
                    case R.id.fest_button:
                        fest.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.fast_food, 0, 0);
                        sel_index&=~(1<<4);
                        break;
                    case R.id.chicken_button:
                        chicken.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.chicken, 0, 0);
                        sel_index&=~(1<<5);
                        break;
                    case R.id.rice_cake_button:
                        rice_cake.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.rice_cake, 0, 0);
                        sel_index&=~(1<<6);
                        break;
                    case R.id.cocktail_button:
                        cocktail.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cocktail, 0, 0);
                        sel_index&=~(1<<7);
                        break;
                    case R.id.cafe_button:
                        cafe.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.coffee, 0, 0);
                        sel_index&=~(1<<0);
                        break;
                    case R.id.icecream_button:
                        iceCream.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icecream, 0, 0);
                        sel_index&=~(1<<1);
                        break;
                    case R.id.bakery_button:
                        bakery.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cake, 0, 0);
                        sel_index&=~(1<<2);
                        break;
                }
            }
        }
    };


    private void initView(View view) {
        category = (LinearLayout) view.findViewById(R.id.category);
        food_sel = (FrameLayout) view.findViewById(R.id.food_sel);
        cancle = (TextView) view.findViewById(R.id.cancle);
        filter_ok = (TextView) view.findViewById(R.id.filter_ok);
        cate_all = (TextView) view.findViewById(R.id.cate_all);
        cate_restaurant = (TextView) view.findViewById(R.id.cate_restaurant);
        cate_foodtruck = (TextView) view.findViewById(R.id.cate_foodtruck);
        cate_dessert = (TextView) view.findViewById(R.id.cate_dessert);
        filter_ok.setOnClickListener(mOnClickListener);
        cate_all.setOnClickListener(mOnClickListener);
        cate_restaurant.setOnClickListener(mOnClickListener);
        cate_foodtruck.setOnClickListener(mOnClickListener);
        cate_dessert.setOnClickListener(mOnClickListener);
    }

    private void initFoodView(View view, int sel_index) {
        rice = (ToggleButton) view.findViewById(R.id.rice_button);
        susih = (ToggleButton) view.findViewById(R.id.susih_button);
        chinese = (ToggleButton) view.findViewById(R.id.Chinese_button);
        western = (ToggleButton) view.findViewById(R.id.western_button);
        fest = (ToggleButton) view.findViewById(R.id.fest_button);
        chicken = (ToggleButton) view.findViewById(R.id.chicken_button);
        rice_cake = (ToggleButton) view.findViewById(R.id.rice_cake_button);
        cocktail = (ToggleButton) view.findViewById(R.id.cocktail_button);
        rice.setOnCheckedChangeListener(toggleListener);
        susih.setOnCheckedChangeListener(toggleListener);
        chinese.setOnCheckedChangeListener(toggleListener);
        western.setOnCheckedChangeListener(toggleListener);
        fest.setOnCheckedChangeListener(toggleListener);
        chicken.setOnCheckedChangeListener(toggleListener);
        rice_cake.setOnCheckedChangeListener(toggleListener);
        cocktail.setOnCheckedChangeListener(toggleListener);
        if (checkIndex(sel_index, 0)) {
            rice.setChecked(true);
        }
        if (checkIndex(sel_index, 1)) {
            susih.setChecked(true);
        }
        if (checkIndex(sel_index, 2)) {
            chinese.setChecked(true);
        }
        if (checkIndex(sel_index, 3)) {
            western.setChecked(true);
        }
        if (checkIndex(sel_index, 4)) {
            fest.setChecked(true);
        }
        if (checkIndex(sel_index, 5)) {
            chicken.setChecked(true);
        }
        if (checkIndex(sel_index, 6)) {
            rice_cake.setChecked(true);
        }
        if (checkIndex(sel_index, 7)) {
            cocktail.setChecked(true);
        }
    }

    private void initDessertView(View view, int index) {

        cafe = (ToggleButton) view.findViewById(R.id.cafe_button);
        iceCream = (ToggleButton) view.findViewById(R.id.icecream_button);
        bakery = (ToggleButton) view.findViewById(R.id.bakery_button);
        cafe.setOnCheckedChangeListener(toggleListener);
        iceCream.setOnCheckedChangeListener(toggleListener);
        bakery.setOnCheckedChangeListener(toggleListener);
        if (checkIndex(index, 0)) {
            cafe.setChecked(true);
        }
        if (checkIndex(index, 1)) {
            iceCream.setChecked(true);
        }
        if (checkIndex(index, 2)) {
            bakery.setChecked(true);
        }
    }
    //선택된 음식목록을 찾아내는 메소드
    public boolean checkIndex(int sel_index, int index) {
        int temp = sel_index;
        if (((temp >> index) & 1) == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void setBottomFilterDialogListener(BottomFilterDialogListener bottomFilterDialogListener){
        this.mBottomFilterDialogListener=bottomFilterDialogListener;
    }
}
