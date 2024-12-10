package com.example.mycoach;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private EditText firstNameEditText, lastNameEditText, emailEditText,
            genderEditText, ageEditText, heightEditText, weightEditText,
            experienceLevelEditText, trainingGoalsEditText,
            specializationEditText, experienceEditText, contactInfoEditText;
    private LinearLayout trainerFieldsLayout;
    private Button saveButton;

    private FirebaseFirestore db;
    private String userId;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();

        // Инициализация элементов UI
        firstNameEditText = findViewById(R.id.edit_first_name);
        lastNameEditText = findViewById(R.id.edit_last_name);
        emailEditText = findViewById(R.id.edit_email);
        genderEditText = findViewById(R.id.edit_gender);
        ageEditText = findViewById(R.id.edit_age);
        heightEditText = findViewById(R.id.edit_height);
        weightEditText = findViewById(R.id.edit_weight);
        experienceLevelEditText = findViewById(R.id.edit_experience_level);
        trainingGoalsEditText = findViewById(R.id.edit_training_goals);
        specializationEditText = findViewById(R.id.edit_specialization);
        experienceEditText = findViewById(R.id.edit_experience);
        contactInfoEditText = findViewById(R.id.edit_contact_info);
        trainerFieldsLayout = findViewById(R.id.trainer_fields_layout);
        saveButton = findViewById(R.id.save_profile_button);

        // Получение данных из Intent
        userId = getIntent().getStringExtra("userId");
        userRole = getIntent().getStringExtra("userRole");

        // Настройка видимости полей в зависимости от роли
        if ("trainer".equals(userRole)) {
            trainerFieldsLayout.setVisibility(View.VISIBLE);
        } else {
            trainerFieldsLayout.setVisibility(View.GONE);
        }

        // Загрузка данных профиля
        loadProfileData();

        saveButton.setOnClickListener(view -> saveProfileChanges());
    }

    private void loadProfileData() {
        String collectionPath = userRole.equals("trainer") ? "trainers" : "users";

        db.collection(collectionPath).document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Заполнение полей данными из документа
                            firstNameEditText.setText(document.getString("firstName"));
                            lastNameEditText.setText(document.getString("lastName"));
                            emailEditText.setText(document.getString("email"));
                            genderEditText.setText(document.getString("gender"));
                            ageEditText.setText(document.getLong("age") != null ? document.getLong("age").toString() : "");
                            heightEditText.setText(document.getLong("height") != null ? document.getLong("height").toString() : "");
                            weightEditText.setText(document.getDouble("weight") != null ? document.getDouble("weight").toString() : "");
                            experienceLevelEditText.setText(document.getString("experienceLevel"));
                            trainingGoalsEditText.setText(document.getString("trainingGoals"));

                            if ("trainer".equals(userRole)) {
                                specializationEditText.setText(document.getString("specialization"));
                                experienceEditText.setText(document.getLong("experience") != null ? document.getLong("experience").toString() : "");
                                contactInfoEditText.setText(document.getString("contactInfo"));
                            }
                        } else {
                            Log.d(TAG, "No such document");
                            Toast.makeText(this, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w(TAG, "Ошибка получения документа:", task.getException());
                        Toast.makeText(this, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void saveProfileChanges() {
        Map<String, Object> updatedData = new HashMap<>();

        // Общие поля для всех пользователей
        updatedData.put("firstName", firstNameEditText.getText().toString());
        updatedData.put("lastName", lastNameEditText.getText().toString());
        updatedData.put("email", emailEditText.getText().toString());
        updatedData.put("gender", genderEditText.getText().toString());
        updatedData.put("age", !ageEditText.getText().toString().isEmpty() ? Long.parseLong(ageEditText.getText().toString()) : null);
        updatedData.put("height", !heightEditText.getText().toString().isEmpty() ? Long.parseLong(heightEditText.getText().toString()) : null);
        updatedData.put("weight", !weightEditText.getText().toString().isEmpty() ? Double.parseDouble(weightEditText.getText().toString()) : null);
        updatedData.put("experienceLevel", experienceLevelEditText.getText().toString());
        updatedData.put("trainingGoals", trainingGoalsEditText.getText().toString());

        // Поля для тренеров
        if ("trainer".equals(userRole)) {
            updatedData.put("specialization", specializationEditText.getText().toString());
            updatedData.put("experience", !experienceEditText.getText().toString().isEmpty() ? Long.parseLong(experienceEditText.getText().toString()) : null);
            updatedData.put("contactInfo", contactInfoEditText.getText().toString());
        }

        // Определение коллекции для обновления данных
        String collectionPath = userRole.equals("trainer") ? "trainers" : "users";

        db.collection(collectionPath).document(userId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Данные профиля успешно обновлены");
                    Toast.makeText(EditProfileActivity.this, "Профиль обновлен!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Ошибка обновления данных профиля:", e);
                    Toast.makeText(EditProfileActivity.this, "Ошибка обновления профиля", Toast.LENGTH_SHORT).show();
                });
    }
}
