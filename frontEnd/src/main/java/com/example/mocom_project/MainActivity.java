package com.example.mocom_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView logoImage;
    private ImageView logoName;
    private LinearLayout loginContainer;
    private Button signupButton;
    private Button loginButton;
    private RadioGroup radioGroup;
    private RadioButton radioButtonGuide;
    private RadioButton radioButtonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        logoImage = findViewById(R.id.logo_image);
        logoName = findViewById(R.id.logo_name);
        loginContainer = findViewById(R.id.login_container);
        signupButton = findViewById(R.id.signup_button);
        loginButton = findViewById(R.id.login_button);
        radioGroup = findViewById(R.id.radioGroup);
        radioButtonGuide = findViewById(R.id.radioButton);
        radioButtonUser = findViewById(R.id.radioButton2);

        Animation moveAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_animation);
        Animation resizeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.resize_animation);

        // 로고 네임의 애니메이션에 리스너 추가
        moveAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 애니메이션 시작 시 호출되는 메서드
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 애니메이션 종료 시 호출되는 메서드
                loginContainer.setVisibility(View.VISIBLE);
                // 로고 네임의 현재 위치를 가져와서 설정
                logoName.clearAnimation();
                logoName.setTranslationX(-95);
                logoName.setTranslationY(-30);
                logoName.setScaleX(0.7f);
                logoName.setScaleY(0.7f);

                // 로고 이미지의 현재 크기를 가져와서 설정
                logoImage.clearAnimation();
                logoImage.setTranslationX(-400);
                logoImage.setTranslationY(-950);
                logoImage.setScaleX(0.3f);
                logoImage.setScaleY(0.3f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // 애니메이션 반복 시 호출되는 메서드
            }
        });

        logoName.startAnimation(moveAnimation);
        logoImage.startAnimation(resizeAnimation);

        // ------------------------배경 이미지 --------------------------
        // 배경 이미지뷰 가져오기
        ImageView backgroundImage = findViewById(R.id.background_image);
        // 애니메이셔파일 로드
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.firstimg);

        // 애니메이션 리스너
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            // 애니메이션 시작 시 호출
            @Override
            public void onAnimationStart(Animation animation) {
            }

            // 애니메이션 정료 시 호출
            @Override
            public void onAnimationEnd(Animation animation) {
                // 배경 이미지의 투명도를 설정
                backgroundImage.setAlpha(0.3f);
            }

            // 애니메이션 반복 시 호출
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // 배경 이미지뷰에 애니메이션 적용
        backgroundImage.startAnimation(fadeOutAnimation);

        // 회원가입 버튼 클릭 이벤트
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupPage.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼 클릭 이벤트
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == radioButtonGuide.getId()) {
                    Intent intent = new Intent(MainActivity.this, MainGuidePage.class);
                    startActivity(intent);
                } else if (selectedId == radioButtonUser.getId()) {
                    Intent intent = new Intent(MainActivity.this, MainPage.class);
                    startActivity(intent);
                }
            }
        });
    }
}
