package com.example.mycoach;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewTrainingPlanActivity extends AppCompatActivity {

    private static final String TAG = "ViewTrainingPlanActivity";

    private TextView trainingPlanNameTextView, trainingPlanDateTextView, trainingPlanDescriptionTextView,
            trainingStatusTextView, trainingCommentsTextView; // Добавьте TextView для статуса и комментариев

    private RecyclerView exercisesRecyclerView;

    private FirebaseFirestore db;
    private TrainingPlanItem trainingPlan;
    private ExerciseViewAdapter exerciseViewAdapter; // Адаптер для просмотра (может отличаться от ExerciseAdapter)
    private String trainingPlanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_training_plan); // Создайте этот макет

        db = FirebaseFirestore.getInstance();

        trainingPlanNameTextView = findViewById(R.id.view_training_plan_name);
        trainingPlanDateTextView = findViewById(R.id.view_training_plan_date);
        trainingPlanDescriptionTextView = findViewById(R.id.view_training_plan_description);
        trainingStatusTextView = findViewById(R.id.view_training_plan_status); // Инициализируйте TextView для статуса
        trainingCommentsTextView = findViewById(R.id.view_training_plan_comments); // Инициализируйте TextView для комментариев
        exercisesRecyclerView = findViewById(R.id.view_exercises_recycler_view); // ID RecyclerView в макете

        // Получение данных тренировочного плана из Intent
        trainingPlan = (TrainingPlanItem) getIntent().getSerializableExtra("trainingPlan");
        if (trainingPlan != null) {
            trainingPlanId = getIntent().getStringExtra("trainingPlanId");
            displayTrainingPlanDetails();
        } else {
            Toast.makeText(this, "Ошибка: не удалось загрузить тренировку", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayTrainingPlanDetails() {
        trainingPlanNameTextView.setText(trainingPlan.getName());
        trainingPlanDateTextView.setText(trainingPlan.getDate());
        trainingPlanDescriptionTextView.setText(trainingPlan.getDescription());
        trainingStatusTextView.setText("Статус: " + trainingPlan.getStatus()); // Отображение статуса
        trainingCommentsTextView.setText("Комментарии: " + trainingPlan.getComments()); // Отображение комментариев

        exerciseViewAdapter = new ExerciseViewAdapter(trainingPlan.getExercises(), this, trainingPlanId);
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exercisesRecyclerView.setAdapter(exerciseViewAdapter);
    }

    // ... (методы для обновления статуса тренировки, добавления комментариев и т.д.)
}
