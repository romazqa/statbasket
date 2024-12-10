package com.example.mycoach.ui.start;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycoach.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private RadioGroup roleRadioGroup;
    private EditText emailEditText, passwordEditText, specializationEditText;
    private LinearLayout trainerFields;
    private Button registerButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();

        roleRadioGroup = findViewById(R.id.role_radio_group);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        specializationEditText = findViewById(R.id.specialization_edit_text);
        trainerFields = findViewById(R.id.trainer_fields);
        registerButton = findViewById(R.id.register_button);

        roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.trainer_radio_button) {
                trainerFields.setVisibility(View.VISIBLE);
            } else {
                trainerFields.setVisibility(View.GONE);
            }
        });

        registerButton.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String specialization = specializationEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка, какая роль выбрана
        int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Выберите роль!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedRoleId);
        String role = selectedRadioButton.getText().toString();

        // Создание пользователя в Firebase
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        // !!! ЗАМЕНИТЕ НА РЕАЛЬНОЕ ХЭШИРОВАНИЕ !!!
        user.put("passwordHash", password);

        if (role.equals("Тренер")) {
            user.put("specialization", specialization);
            // ... добавьте другие поля для тренера

            db.collection("trainers").add(user)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Тренер добавлен с ID: " + documentReference.getId());
                        // Добавьте пользователя в коллекцию users с ссылкой на тренера
                        addUserWithTrainerRef(documentReference, email, password);
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Ошибка добавления тренера", e);
                        Toast.makeText(this, "Ошибка регистрации!", Toast.LENGTH_SHORT).show();
                    });

        } else { // Спортсмен
            db.collection("users").add(user)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Спортсмен добавлен с ID: " + documentReference.getId());
                        Toast.makeText(this, "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show();
                        finish(); // Закрыть окно регистрации
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Ошибка добавления спортсмена", e);
                        Toast.makeText(this, "Ошибка регистрации!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void addUserWithTrainerRef(DocumentReference trainerRef, String email, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        // !!! ЗАМЕНИТЕ НА РЕАЛЬНОЕ ХЭШИРОВАНИЕ !!!
        user.put("passwordHash", password);
        user.put("trainer", trainerRef);

        db.collection("users").add(user)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Пользователь (тренер) добавлен с ID: " + documentReference.getId());
                    Toast.makeText(this, "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show();
                    finish(); // Закрыть окно регистрации
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Ошибка добавления пользователя (тренера)", e);
                    Toast.makeText(this, "Ошибка регистрации!", Toast.LENGTH_SHORT).show();
                });
    }
}
