package com.example.mocom_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AreaChoiceActivity extends AppCompatActivity {

    private String selectedCity;
    private String selectedArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.areachoicepage);

        // 인텐트로부터 선택된 지역을 받아옵니다.
        selectedCity = getIntent().getStringExtra("selected_city");
        selectedArea = getIntent().getStringExtra("selected_area");

        // TextView를 찾아서 선택된 지역을 설정합니다.
        TextView leftTextView = findViewById(R.id.left_textview);
        leftTextView.setText(selectedArea);

        // CalendarView에서 날짜 선택
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

                // 다음 화면으로 데이터 전달을 위한 Intent 생성
                Intent intent = new Intent(AreaChoiceActivity.this, NextActivity.class);
                intent.putExtra("selected_city", selectedCity);
                intent.putExtra("selected_area", selectedArea);
                intent.putExtra("selected_date", selectedDate);
                startActivity(intent);
            }
        });
    }
}
