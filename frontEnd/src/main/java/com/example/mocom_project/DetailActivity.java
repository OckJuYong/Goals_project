package com.example.mocom_project;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    // 텍스트뷰 선언
    private TextView nameTextView;
    private TextView priceTextView;
    private TextView languageTextView;
    private TextView goalTextView;
    private TextView timeTextView;
    private TextView scheduleTextView; // 추가: 스케줄을 보여줄 텍스트뷰 추가
    private MapView mapView;
    private GoogleMap googleMap;

    // 맵뷰 번들 키 정의
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_SCHEDULE = "schedule";
    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_PRICE = "price";
    public static final String EXTRA_LANGUAGE = "language";
    public static final String EXTRA_GOAL = "goal";
    public static final String EXTRA_TIME = "time";

    // 추가: address와 schedule 변수 선언
    private String address;
    private String schedule;
    private int price;
    private String language;
    private String goal;
    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 텍스트뷰 초기화
        nameTextView = findViewById(R.id.name_textview);
        priceTextView = findViewById(R.id.price_textview);
        languageTextView = findViewById(R.id.language_textview);
        goalTextView = findViewById(R.id.goal_textview);
        timeTextView = findViewById(R.id.time_textview);
        scheduleTextView = findViewById(R.id.schedule_textview); // 추가: 스케줄 텍스트뷰 초기화
        mapView = findViewById(R.id.map);

        // 인텐트로부터 데이터 가져오기
        String name = getIntent().getStringExtra(EXTRA_NAME);
        schedule = getIntent().getStringExtra(EXTRA_SCHEDULE);
        address = getIntent().getStringExtra(EXTRA_ADDRESS);
        price = getIntent().getIntExtra(EXTRA_PRICE, 0);
        language = getIntent().getStringExtra(EXTRA_LANGUAGE);
        goal = getIntent().getStringExtra(EXTRA_GOAL);
        time = getIntent().getIntExtra(EXTRA_TIME, 0);

        // 텍스트뷰에 데이터 설정
        nameTextView.setText("Name: " + name);
        // 언어에 따라 가격 형식 지정
        if ("korea".equalsIgnoreCase(language)) {
            // 한국어 형식: 100,000원
            priceTextView.setText("Price: " + String.format("%,d", price) + "원");
        } else if ("english".equalsIgnoreCase(language)) {
            // 영어 형식: $100,000
            priceTextView.setText("Price: $" + String.format("%,d", price));
        } else {
            // 언어가 지정되지 않았거나 인식되지 않으면 기본 영어 형식 사용
            priceTextView.setText("Price: $" + String.format("%,d", price));
        }
        languageTextView.setText("Language: " + language);
        goalTextView.setText("Goal: " + goal);
        timeTextView.setText("Time: " + formatTime(time));
        scheduleTextView.setText("Schedule: " + schedule); // 추가: 스케줄 텍스트뷰에 데이터 설정

        // 맵 초기화
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        try {
            // 주소를 파싱하여 좌표 얻기
            String[] addressParts = address.split(",\\s*");

            // 주소 부분으로부터 LatLng 리스트 생성
            List<LatLng> locations = new ArrayList<>();
            for (int i = 0; i < addressParts.length; i += 2) {
                double lat = Double.parseDouble(addressParts[i]);
                double lng = Double.parseDouble(addressParts[i + 1]);
                locations.add(new LatLng(lat, lng));
            }

            // 시작 지점(0,0)으로부터의 거리로 위치 정렬 (간단하게 하기 위해)
            LatLng startingPoint = locations.get(0);
            locations.sort(new Comparator<LatLng>() {
                @Override
                public int compare(LatLng loc1, LatLng loc2) {
                    float[] result1 = new float[1];
                    float[] result2 = new float[1];
                    android.location.Location.distanceBetween(startingPoint.latitude, startingPoint.longitude, loc1.latitude, loc1.longitude, result1);
                    android.location.Location.distanceBetween(startingPoint.latitude, startingPoint.longitude, loc2.latitude, loc2.longitude, result2);
                    return Float.compare(result1[0], result2[0]);
                }
            });

            // 마커 추가 및 폴리라인으로 연결
            LatLng previousLocation = null;
            for (LatLng location : locations) {
                googleMap.addMarker(new MarkerOptions().position(location).title(location.toString()));

                if (previousLocation != null) {
                    googleMap.addPolyline(new PolylineOptions()
                            .add(previousLocation, location)
                            .width(5)
                            .color(Color.RED)); // 필요에 따라 너비와 색상 조정
                }

                previousLocation = location;

                // 첫 번째 마커로 카메라 이동 (줌 레벨은 덜 확대)
                if (location.equals(locations.get(0))) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12)); // 필요에 따라 줌 레벨 조정
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    // 시간을 형식화하는 헬퍼 메소드
    private String formatTime(int time) {
        String timeStr = String.format("%08d", time); // 문자열이 8자리가 되도록 보장
        String startHour = timeStr.substring(0, 2);
        String startMinute = timeStr.substring(2, 4);
        String endHour = timeStr.substring(4, 6);
        String endMinute = timeStr.substring(6, 8);
        return startHour + ":" + startMinute + " ~ " + endHour + ":" + endMinute;
    }

    // 스케줄 텍스트를 형식화하는 헬퍼 메소드 (필요한 경우)
    private String formatSchedule(String schedule) {
        // 필요한 경우 형식화 로직 추가
        return schedule;
    }
}
