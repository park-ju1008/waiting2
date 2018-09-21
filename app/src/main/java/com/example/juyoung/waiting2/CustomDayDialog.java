package com.example.juyoung.waiting2;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.view.WindowManager.LayoutParams;

public class CustomDayDialog {

    private Context context;
    public CustomDayDialog(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(final TextView main_label) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_day_dialog);
        LayoutParams params = dlg.getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        dlg.getWindow().setAttributes((LayoutParams) params);
        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final ToggleButton mon = (ToggleButton) dlg.findViewById(R.id.mon);
        final ToggleButton tue = (ToggleButton) dlg.findViewById(R.id.tue);
        final ToggleButton wed = (ToggleButton) dlg.findViewById(R.id.wed);
        final ToggleButton thu = (ToggleButton) dlg.findViewById(R.id.thu);
        final ToggleButton fri = (ToggleButton) dlg.findViewById(R.id.fri);
        final ToggleButton sat = (ToggleButton) dlg.findViewById(R.id.sat);
        final ToggleButton sun = (ToggleButton) dlg.findViewById(R.id.sun);
        final Button okButton = (Button) dlg.findViewById(R.id.ok_button);
        final CheckBox daily = (CheckBox) dlg.findViewById(R.id.checkBox);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancle_button);

        //체크박스 리스너
        daily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mon.setChecked(true);
                    tue.setChecked(true);
                    wed.setChecked(true);
                    thu.setChecked(true);
                    fri.setChecked(true);
                    sat.setChecked(true);
                    sun.setChecked(true);
                } else {
                    mon.setChecked(false);
                    tue.setChecked(false);
                    wed.setChecked(false);
                    thu.setChecked(false);
                    fri.setChecked(false);
                    sat.setChecked(false);
                    sun.setChecked(false);
                }
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String day = "";
                int check=0;
                if (daily.isChecked()) {
                    day = "매일";
                } else {
                    if (mon.isChecked()) {
                        day += "월,";
                        check|=(1<<6);
                    }
                    if (tue.isChecked()){
                        day += "화,";
                        check |= (1 <<5);
                    }
                    if (wed.isChecked()) {
                        day += "수,";
                        check |= (1 <<4);
                    }
                    if (thu.isChecked()) {
                        day += "목,";
                        check |= (1 << 3);
                    }
                    if (fri.isChecked()) {
                        day += "금,";
                        check |= (1 << 2);
                    }
                    if (sat.isChecked()) {
                        day += "토,";
                        check |= (1 << 1);
                    }
                    if (sun.isChecked()) {
                        day += "일,";
                       check|=1;
                    }
                    day = day.substring(0, day.length() - 1);
                }
                if(check==127){
                    day="매일";
                }
                else if(check==124){
                    day="평일";
                }
                else if(check==3){
                    day="주말";
                }
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.
                main_label.setText(day);

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }
}