package com.example.mycoach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.example.mycoach.ui.calendar.CalendarFragment;
import com.example.mycoach.ui.home.HomeFragment;
import com.example.mycoach.ui.list.ListFragment;
import com.example.mycoach.ui.profile.ProfileFragment;
import com.example.mycoach.ui.start.StartActivity;
import com.example.mycoach.ui.start.StartFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.mycoach.R;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FloatingActionButton addButton;
    private BottomNavigationView bottomNavigationView;

    private FirebaseFirestore db;
    private String userId;
    private String userRole;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Инициализация фрагментов
        HomeFragment homeFragment = new HomeFragment();
        CalendarFragment calendarFragment = new CalendarFragment();
        ListFragment listFragment = new ListFragment();
        ProfileFragment profileFragment = new ProfileFragment();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();

            user.getIdToken(true)
                    .addOnSuccessListener(getIdTokenResult -> {
                        userRole = (String) getIdTokenResult.getClaims().get("role");

                        if (userRole != null) {
                            if ("trainer".equals(userRole)) {
                                setupTrainerUI();
                            } else if ("athlete".equals(userRole)) {
                                setupAthleteUI();
                            }
                            // Загрузка HomeFragment по умолчанию
                            replaceFragment(homeFragment);
                        } else {
                            handleRoleError();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Ошибка получения роли пользователя", e);
                        Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        // Обработка ошибки (например, выход из системы)
                    });
        } else {
            // Пользователь не авторизован, переход на экран входа
            goToStartActivity();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                replaceFragment(homeFragment);
                return true;
            } else if (itemId == R.id.navigation_calendar) {
                replaceFragment(calendarFragment);
                return true;
            } else if (itemId == R.id.navigation_add) {
                handleAddButtonClick();
                return true;
            } else if (itemId == R.id.navigation_list) {
                replaceFragment(listFragment);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                replaceFragment(profileFragment);
                return true;
            }

            return false;
        });
    }

    private void setupTrainerUI() {
        addButton.setVisibility(View.VISIBLE);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddClientActivity.class);
            startActivity(intent);
        });
    }

    private void setupAthleteUI() {
        addButton.setVisibility(View.VISIBLE);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddWorkoutActivity.class);
            startActivity(intent);
        });
    }

    private void handleAddButtonClick() {
        if ("trainer".equals(userRole)) {
            startActivity(new Intent(MainActivity.this, AddClientActivity.class));
        } else if ("athlete".equals(userRole)) {
            startActivity(new Intent(MainActivity.this, AddWorkoutActivity.class));
        } else {
            Toast.makeText(this, "Ошибка: роль пользователя не определена", Toast.LENGTH_SHORT).show();
        }
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void handleRoleError() {
        Log.e(TAG, "Ошибка: Неизвестная роль пользователя");
        Toast.makeText(this, "Ошибка: роль пользователя не определена", Toast.LENGTH_SHORT).show();
        goToStartActivity();
    }

    private void goToStartActivity() {
        startActivity(new Intent(MainActivity.this, StartActivity.class)); // Запускаем StartActivity
        finish();
    }



}