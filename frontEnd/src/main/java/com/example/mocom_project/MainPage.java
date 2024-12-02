package com.example.mocom_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {

    // 동적 버튼을 담을 컨테이너와 선택된 도시를 저장할 변수 선언
    private LinearLayout dynamicButtonsContainer;
    private String selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        // 레이아웃에서 버튼과 동적 버튼 컨테이너를 초기화
        Button button = findViewById(R.id.button);
        dynamicButtonsContainer = findViewById(R.id.dynamic_buttons_container);

        // 버튼 클릭 리스너 설정
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팝업 메뉴 생성 및 메뉴 항목 설정
                PopupMenu popupMenu = new PopupMenu(MainPage.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                // 팝업 메뉴 항목 클릭 리스너 설정
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.seoul) {
                            // 서울 선택 시
                            selectedCity = getString(R.string.seoul);
                            showToast(selectedCity);
                            addDynamicButtons(new String[]{ "강남구", "압구정동", "홍대", "이태원", "삼청동", "명동", "서촌", "종로", "가로수길", "동대문"});
                            return true;
                        } else if (id == R.id.daejun) {
                            // 대전 선택 시
                            selectedCity = getString(R.string.daejeon);
                            showToast(selectedCity);
                            addDynamicButtons(new String[]{ "유성구", "서구", "동구", "대덕구"});
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                // 팝업 메뉴 표시
                popupMenu.show();
            }
        });
    }

    // 토스트 메시지 표시 메소드
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // 동적 버튼을 추가하는 메소드
    private void addDynamicButtons(final String[] neighborhoods) {
        // 기존 버튼들 제거
        dynamicButtonsContainer.removeAllViews();

        // 각 지역에 대해 버튼 생성 및 추가
        for (final String neighborhood : neighborhoods) {
            Button button = new Button(this);
            button.setText(neighborhood);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 버튼 클릭 시 AreaChoiceActivity로 이동 및 선택된 도시와 지역 전달
                    Intent intent = new Intent(MainPage.this, AreaChoiceActivity.class);
                    intent.putExtra("selected_city", selectedCity);
                    intent.putExtra("selected_area", neighborhood);
                    startActivity(intent);
                }
            });
            dynamicButtonsContainer.addView(button);
        }
    }
}
