package com.example.mocom_project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InputInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText editName, editPrice, editLanguage, editCity, editSchedule, editPlace, editTag, editGoal, editStartTime, editEndTime, editDay;
    private MapView mapView;
    private GoogleMap googleMap;
    private List<Marker> markers = new ArrayList<>(); // 마커를 저장하는 리스트
    private List<LatLng> savedLocations = new ArrayList<>(); // 저장된 위치를 저장하는 리스트

    // 언어 선택 옵션
    private static final String[] LANGUAGES = {"한국어", "English"};

    // 도시 선택 옵션
    private static final String[] DAEJEON_DISTRICTS = {"유성구", "서구", "동구"};
    private static final String[] SEOUL_DISTRICTS = {"홍대", "강남", "종로"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_info);

        // 뷰 초기화
        editName = findViewById(R.id.edit_name);
        editPrice = findViewById(R.id.edit_price);
        editLanguage = findViewById(R.id.edit_language);
        editCity = findViewById(R.id.edit_city);
        editSchedule = findViewById(R.id.edit_schedule);
        editPlace = findViewById(R.id.edit_place);
        editTag = findViewById(R.id.edit_tag);
        editGoal = findViewById(R.id.edit_goal);
        editStartTime = findViewById(R.id.edit_start_time); // 업데이트된 시작 시간 EditText
        editEndTime = findViewById(R.id.edit_end_time); // 업데이트된 종료 시간 EditText
        editDay = findViewById(R.id.edit_day);

        Button buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 모든 필드가 채워졌는지 확인
                if (validateInputs()) {
                    saveUserData(); // 사용자 데이터를 서버에 저장
                } else {
                    Toast.makeText(InputInfoActivity.this, "모든 필드를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 맵뷰 초기화
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // 날짜 추가 버튼 초기화
        Button buttonAddDate = findViewById(R.id.button_add_date);
        buttonAddDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // 시작 시간 및 종료 시간 선택기 초기화
        editStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTimePickerDialog();
            }
        });

        editEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndTimePickerDialog();
            }
        });
    }

    private boolean validateInputs() {
        // 모든 필드 유효성 검사
        String name = editName.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String language = editLanguage.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String schedule = editSchedule.getText().toString().trim();
        String place = editPlace.getText().toString().trim();
        String tag = editTag.getText().toString().trim();
        String goal = editGoal.getText().toString().trim();
        String startTime = editStartTime.getText().toString().trim();
        String endTime = editEndTime.getText().toString().trim();
        String day = editDay.getText().toString().trim();

        return !name.isEmpty() && !price.isEmpty() && !language.isEmpty() && !city.isEmpty() &&
                !schedule.isEmpty() && !place.isEmpty() && !tag.isEmpty() && !goal.isEmpty() &&
                !startTime.isEmpty() && !endTime.isEmpty() && !day.isEmpty();
    }

    private void saveUserData() {
        // 입력된 값을 가져오기
        String name = editName.getText().toString().trim();
        int price = Integer.parseInt(editPrice.getText().toString().trim());
        String language = editLanguage.getText().toString().trim();
        String city = editCity.getText().toString().trim();
        String schedule = editSchedule.getText().toString().trim(); // 이제 위도와 경도가 포함된 스케줄 문자열
        String place = editPlace.getText().toString().trim();
        String tag = editTag.getText().toString().trim();
        String goal = editGoal.getText().toString().trim();
        String startTime = editStartTime.getText().toString().trim();
        String endTime = editEndTime.getText().toString().trim();
        String day = editDay.getText().toString().trim();

        // OkHttpClient 인스턴스 생성
        OkHttpClient client = new OkHttpClient();

        // 폼 데이터 준비
        FormBody.Builder formBodyBuilder = new FormBody.Builder()
                .add("name", name)
                .add("price", String.valueOf(price))
                .add("language", language)
                .add("city", city)
                .add("goal", goal)
                .add("startTime", startTime)
                .add("endTime", endTime)
                .add("day", day);

        // 모든 저장된 위치를 폼 데이터에 추가
        for (int i = 0; i < savedLocations.size(); i++) {
            LatLng location = savedLocations.get(i);
            formBodyBuilder.add("latitude[" + i + "]", String.valueOf(location.latitude))
                    .add("longitude[" + i + "]", String.valueOf(location.longitude));
        }

        // 나머지 필드 추가
        formBodyBuilder.add("schedule", schedule)
                .add("place", place)
                .add("tag", tag);

        RequestBody formBody = formBodyBuilder.build();

        // 요청 생성
        Request request = new Request.Builder()
                .url("https://port-0-today-goals-lxhy8g9qc44319ad.sel5.cloudtype.app/users")
                .post(formBody)
                .build();

        // 비동기적으로 요청 보내기
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // 실패 처리
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InputInfoActivity.this, "사용자 데이터 저장 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // 응답 처리
                final String responseBody = response.body().string(); // 응답 본문은 한 번만 소비되도록 보장
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            Toast.makeText(InputInfoActivity.this, "사용자 데이터 저장 성공", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(InputInfoActivity.this, "사용자 데이터 저장 실패: " + responseBody, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle("MAP_VIEW_BUNDLE_KEY");
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle("MAP_VIEW_BUNDLE_KEY", mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // 저장된 위치에 마커 추가
        for (LatLng location : savedLocations) {
            Marker marker = googleMap.addMarker(new MarkerOptions().position(location).title("저장된 위치"));
            markers.add(marker);
        }

        // 맵 클릭 이벤트 리스너 설정
        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // 클릭된 위치를 저장된 위치에 추가
                savedLocations.add(latLng);

                // 이전 마커들 지우기
                clearMarkers();

                // 마커 추가
                for (LatLng location : savedLocations) {
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(location).title("저장된 위치"));
                    markers.add(marker);
                }

                // 모든 저장된 위치를 포함한 장소 EditText 업데이트
                updatePlaceEditText();
            }
        });

        // 대전의 한 점에 마커 추가하고 카메라 이동
        LatLng daejeon = new LatLng(36.3504, 127.3845);
        this.googleMap.addMarker(new MarkerOptions().position(daejeon).title("대전의 한 점"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(daejeon, 13));
    }

    private void updatePlaceEditText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (LatLng location : savedLocations) {
            stringBuilder.append(String.format("%f, %f\n", location.latitude, location.longitude));
        }
        editPlace.setText(stringBuilder.toString());
    }

    private void clearMarkers() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

    // Language EditText 클릭 시 호출되는 메소드
    public void showLanguageOptions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("언어 선택")
                .setItems(LANGUAGES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedLanguage = LANGUAGES[which];
                        editLanguage.setText(selectedLanguage);
                    }
                });
        builder.create().show();
    }

    // City EditText 클릭 시 호출되는 메소드
    public void showCityOptions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("도시 선택")
                .setItems(getCityOptions(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedCity = getCityOptions()[which];
                        editCity.setText(selectedCity);
                    }
                });
        builder.create().show();
    }

    // 언어 선택에 따라 도시 옵션을 결정하는 메소드
    private String[] getCityOptions() {
        String language = editLanguage.getText().toString().trim();
        if (language.equals("한국어")) {
            return DAEJEON_DISTRICTS;
        } else if (language.equals("English")) {
            return SEOUL_DISTRICTS;
        } else {
            return new String[0];
        }
    }

    private void showDatePickerDialog() {
        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialog 생성
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // 연도는 무시하고 월과 일만 사용
                        String selectedDate = (monthOfYear + 1) + "-" + dayOfMonth;
                        String currentText = editDay.getText().toString().trim();
                        if (currentText.isEmpty()) {
                            editDay.setText(selectedDate);
                        } else {
                            editDay.setText(currentText + ", " + selectedDate);
                        }
                    }
                }, year, month, dayOfMonth);

        // DatePickerDialog 보이기
        datePickerDialog.show();
    }

    private void showStartTimePickerDialog() {
        // 현재 시간 가져오기
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // TimePickerDialog 생성
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // 선택된 시간 포맷
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        editStartTime.setText(selectedTime);
                    }
                }, hour, minute, true); // 24시간 형식 사용

        // TimePickerDialog 보이기
        timePickerDialog.show();
    }

    private void showEndTimePickerDialog() {
        // 현재 시간 가져오기
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // TimePickerDialog 생성
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // 선택된 시간 포맷
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        editEndTime.setText(selectedTime);
                    }
                }, hour, minute, true); // 24시간 형식 사용

        // TimePickerDialog 보이기
        timePickerDialog.show();
    }

}
