package com.example.mocom_project;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NextActivity extends AppCompatActivity {

    private static final String TAG = NextActivity.class.getSimpleName();
    private LinearLayout container;
    private String selectedDate;
    private String selectedArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_activity_layout);

        container = findViewById(R.id.container);

        // 인텐트로부터 선택된 날짜와 지역을 가져옵니다.
        selectedDate = getIntent().getStringExtra("selected_date");
        selectedArea = getIntent().getStringExtra("selected_area");

        // GET 요청을 위한 URL
        String url = "https://port-0-today-goals-lxhy8g9qc44319ad.sel5.cloudtype.app/users/";

        // OkHttpClient 인스턴스 생성
        OkHttpClient client = new OkHttpClient();

        // 요청 생성
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 요청을 비동기적으로 보냅니다.
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // 실패 처리
                e.printStackTrace();
                Log.e(TAG, "GET 요청 실패: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(NextActivity.this, "데이터를 가져오는데 실패했습니다", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // 요청 성공 여부 확인
                if (!response.isSuccessful()) {
                    throw new IOException("요청 실패: " + response);
                }

                // 응답 바디를 문자열로 가져옵니다.
                String responseBody = response.body().string();

                Log.d(TAG, "응답 바디: " + responseBody);

                // UI 스레드에서 데이터를 처리하고 표시합니다.
                runOnUiThread(() -> {
                    try {
                        // JSON 응답을 파싱합니다.
                        JSONArray jsonArray = new JSONArray(responseBody);

                        // 이름별로 데이터를 그룹화할 맵을 생성합니다.
                        Map<String, List<JSONObject>> nameMap = new HashMap<>();

                        // 각 JSON 객체를 반복 처리합니다.
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            // 장소가 선택된 지역과 날짜가 선택된 날짜와 일치하는지 확인합니다.
                            String place = jsonObject.getString("place");
                            String day = jsonObject.getString("day");

                            if (place.equalsIgnoreCase(selectedArea) && day.contains(convertDateToMMDD(selectedDate))) {
                                // 선택된 지역과 날짜에 맞는 항목이 발견됨
                                // 데이터 추출
                                String name = jsonObject.getString("name");
                                String tag = jsonObject.getString("tag");
                                int price = jsonObject.getInt("price");
                                String goal = jsonObject.getString("goal");
                                int time = jsonObject.getInt("time");
                                String language = jsonObject.getString("language");
                                String address = jsonObject.getString("address");

                                // 주소에서 위도와 경도 추출
                                String[] addressParts = address.split(",\\s*");
                                double latitude = 0.0;
                                double longitude = 0.0;
                                if (addressParts.length >= 2) {
                                    latitude = Double.parseDouble(addressParts[0]);
                                    longitude = Double.parseDouble(addressParts[1]);
                                } else {
                                    Log.e(TAG, "항목의 주소 형식이 잘못되었습니다: " + name);
                                    return; // 주소 형식이 잘못된 경우 이 항목을 건너뜁니다.
                                }

                                // 시간을 포맷된 문자열로 변환합니다.
                                String formattedTime = formatTime(time);

                                // 언어에 따라 가격을 포맷합니다.
                                String formattedPrice = formatPrice(price, language);

                                // 이름별 그룹에 추가합니다.
                                if (!nameMap.containsKey(name)) {
                                    nameMap.put(name, new ArrayList<>());
                                }
                                nameMap.get(name).add(jsonObject);
                            }
                        }

                        // 각 이름별 그룹을 표시합니다.
                        for (Map.Entry<String, List<JSONObject>> entry : nameMap.entrySet()) {
                            String name = entry.getKey();
                            List<JSONObject> items = entry.getValue();

                            // 이름을 위한 TextView 생성
                            TextView nameView = new TextView(NextActivity.this);
                            nameView.setText(name);
                            nameView.setTextSize(20);
                            nameView.setPadding(10, 10, 10, 10);
                            nameView.setBackgroundResource(android.R.color.darker_gray);
                            nameView.setTextColor(Color.BLACK);
                            nameView.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));

                            // 마진을 포함한 레이아웃 파라미터 설정
                            LinearLayout.LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            nameLayoutParams.setMargins(10, 20, 10, 10); // 왼쪽, 위, 오른쪽, 아래 마진
                            nameView.setLayoutParams(nameLayoutParams);

                            container.addView(nameView);

                            // 각 항목을 이름 아래에 표시합니다.
                            for (JSONObject jsonObject : items) {
                                try {
                                    int price = jsonObject.getInt("price");
                                    String goal = jsonObject.getString("goal");
                                    int time = jsonObject.getInt("time");
                                    String language = jsonObject.getString("language");
                                    String address = jsonObject.getString("address");

                                    // 주소에서 위도와 경도 추출
                                    String[] addressParts = address.split(",\\s*");
                                    final double latitude;
                                    final double longitude;
                                    if (addressParts.length >= 2) {
                                        latitude = Double.parseDouble(addressParts[0]);
                                        longitude = Double.parseDouble(addressParts[1]);
                                    } else {
                                        Log.e(TAG, "항목의 주소 형식이 잘못되었습니다: " + name);
                                        continue; // 주소 형식이 잘못된 경우 이 항목을 건너뜁니다.
                                    }

                                    // 시간을 포맷된 문자열로 변환합니다.
                                    String formattedTime = formatTime(time);

                                    // 언어에 따라 가격을 포맷합니다.
                                    String formattedPrice = formatPrice(price, language);

                                    // 항목을 위한 TextView 생성
                                    TextView textView = new TextView(NextActivity.this);
                                    textView.setText("이름: " + name + "\n가격: " + formattedPrice + "\n목표: " + goal + "\n시간: " + formattedTime + "\n언어: " + language);
                                    textView.setTextSize(18);
                                    textView.setPadding(10, 10, 10, 10);
                                    textView.setBackgroundResource(android.R.drawable.edit_text);
                                    textView.setTextColor(Color.BLACK);
                                    textView.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD_ITALIC));

                                    // 마진과 API 21 이상에서는 그림자를 위한 elevation 설정
                                    LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    );
                                    itemLayoutParams.setMargins(10, 10, 10, 20); // 왼쪽, 위, 오른쪽, 아래 마진
                                    textView.setLayoutParams(itemLayoutParams);
                                    textView.setElevation(8); // API 21+에서 그림자 추가

                                    // TextView에 대한 클릭 리스너 설정
                                    textView.setOnClickListener(v -> {
                                        Intent intent = new Intent(NextActivity.this, DetailActivity.class);
                                        try {
                                            intent.putExtra("name", name);
                                            intent.putExtra("price", price);
                                            intent.putExtra("language", language);
                                            intent.putExtra("address", address);
                                            intent.putExtra("latitude", latitude);
                                            intent.putExtra("longitude", longitude);
                                            intent.putExtra("schedule", jsonObject.getString("schedule"));
                                            intent.putExtra("place", jsonObject.getString("place"));
                                            intent.putExtra("tag", jsonObject.getString("tag"));
                                            intent.putExtra("goal", goal);
                                            intent.putExtra("time", time);
                                            intent.putExtra("day", jsonObject.getString("day"));
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            Log.e(TAG, "Extras 설정 중 JSONException 발생: " + e.getMessage());
                                            Toast.makeText(NextActivity.this, "JSON 파싱 오류", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    // 컨테이너에 TextView 추가
                                    container.addView(textView);
                                } catch (JSONException e) {
                                    Log.e(TAG, "항목 처리 중 JSONException 발생: " + e.getMessage());
                                } catch (NumberFormatException e) {
                                    Log.e(TAG, "위도 또는 경도 파싱 중 NumberFormatException 발생: " + e.getMessage());
                                }
                            }
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "JSON 배열 파싱 중 JSONException 발생: " + e.getMessage());
                        Toast.makeText(NextActivity.this, "JSON 파싱 오류", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // 시간을 포맷된 문자열로 변환하는 도우미 메서드
    private String formatTime(int time) {
        String timeStr = String.format("%08d", time); // 문자열 길이가 8이 되도록 포맷합니다.
        String startHour = timeStr.substring(0, 2);
        String startMinute = timeStr.substring(2, 4);
        String endHour = timeStr.substring(4, 6);
        String endMinute = timeStr.substring(6, 8);
        return startHour + ":" + startMinute + " ~ " + endHour + ":" + endMinute;
    }

    // 날짜를 MM-DD 형식으로 변환하는 도우미 메서드
    private String convertDateToMMDD(String date) {
        String[] parts = date.split("-");
        if (parts.length == 3) {
            return parts[1] + "-" + parts[2]; // MM-DD 형식으로 반환합니다.
        }
        return date;
    }

    // 언어에 따라 가격을 포맷하는 도우미 메서드
    private String formatPrice(int price, String language) {
        NumberFormat formatter;
        String currencySymbol;
        if (language.equalsIgnoreCase("korea")) {
            formatter = NumberFormat.getInstance(Locale.KOREA);
            currencySymbol = "원";
        } else if (language.equalsIgnoreCase("english")) {
            formatter = NumberFormat.getInstance(Locale.US);
            currencySymbol = "$";
        } else {
            // 언어가 지정되지 않은 경우 기본 포맷을 사용합니다.
            formatter = NumberFormat.getInstance();
            currencySymbol = "";
        }
        return formatter.format(price) + currencySymbol;
    }
}
