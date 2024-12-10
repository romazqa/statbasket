package com.example.mycoach;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.example.mycoach.TrainingPlanItem;

public class AddTrainingPlanActivity extends AppCompatActivity {

    private static final String TAG = "AddTrainingPlanActivity";

    private EditText trainingPlanNameEditText, trainingPlanDateEditText, trainingPlanDescriptionEditText;
    private RecyclerView exercisesRecyclerView;
    private Button addExerciseButton, saveTrainingPlanButton;

    private FirebaseFirestore db;
    private List<TrainingPlanItem.Exercise> exercises = new ArrayList<>();
    private ExerciseAdapter exerciseAdapter;
    private Calendar selectedDate = Calendar.getInstance();
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_training_plan);

        db = FirebaseFirestore.getInstance();

        trainingPlanNameEditText = findViewById(R.id.training_plan_name_edittext);
        trainingPlanDateEditText = findViewById(R.id.training_plan_date_edittext);
        trainingPlanDescriptionEditText = findViewById(R.id.training_plan_description_edittext);
        exercisesRecyclerView = findViewById(R.id.exercises_recyclerview);
        addExerciseButton = findViewById(R.id.add_exercise_button);
        saveTrainingPlanButton = findViewById(R.id.save_training_plan_button);

        clientId = getIntent().getStringExtra("userId"); // Получаем ID клиента, если передан

        exerciseAdapter = new ExerciseAdapter(exercises, this);  // Передаем контекст
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exercisesRecyclerView.setAdapter(exerciseAdapter);

        trainingPlanDateEditText.setOnClickListener(v -> showDatePicker());

        addExerciseButton.setOnClickListener(v -> openAddExerciseDialog());

        saveTrainingPlanButton.setOnClickListener(v -> saveTrainingPlan());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, monthOfYear);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateEditText();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateDateEditText() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        trainingPlanDateEditText.setText(sdf.format(selectedDate.getTime()));
    }

    private void openAddExerciseDialog() {
        // Логика для открытия диалогового окна добавления упражнения
        // ... (Создайте диалоговое окно или новую активность для добавления упражнения)
    }

    private void saveTrainingPlan() {
        String name = trainingPlanNameEditText.getText().toString().trim();
        String date = trainingPlanDateEditText.getText().toString().trim();
        String description = trainingPlanDescriptionEditText.getText().toString().trim();

        if (name.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Заполните обязательные поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получаем ID тренера (предполагается, что вы используете FirebaseAuth)
        String trainerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Создание объекта TrainingPlanItem
        TrainingPlanItem trainingPlan = new TrainingPlanItem(name, date, description, exercises, "To-do", "");

        // Сохранение в Firebase
        db.collection("trainers").document(trainerId).collection("trainingPlans")
                .add(trainingPlan)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Тренировочный план добавлен с ID: " + documentReference.getId());
                    Toast.makeText(this, "Тренировка сохранена!", Toast.LENGTH_SHORT).show();
                    finish(); // Закрываем активность после сохранения
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Ошибка сохранения тренировочного плана:", e);
                    Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

