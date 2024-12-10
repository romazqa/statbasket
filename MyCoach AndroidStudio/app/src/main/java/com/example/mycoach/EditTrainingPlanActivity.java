package com.example.mycoach;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditTrainingPlanActivity extends AppCompatActivity {

    private static final String TAG = "EditTrainingPlanActivity";

    private EditText trainingPlanNameEditText, trainingPlanDateEditText, trainingPlanDescriptionEditText;
    private RecyclerView exercisesRecyclerView;
    private Button addExerciseButton, updateTrainingPlanButton;

    private FirebaseFirestore db;
    private TrainingPlanItem trainingPlan;
    private ExerciseAdapter exerciseAdapter;
    private String trainingPlanId;
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_training_plan);

        db = FirebaseFirestore.getInstance();

        trainingPlanNameEditText = findViewById(R.id.training_plan_name_edittext);
        trainingPlanDateEditText = findViewById(R.id.training_plan_date_edittext);
        trainingPlanDescriptionEditText = findViewById(R.id.training_plan_description_edittext);
        exercisesRecyclerView = findViewById(R.id.exercises_recyclerview);
        addExerciseButton = findViewById(R.id.add_exercise_button);
        updateTrainingPlanButton = findViewById(R.id.update_training_plan_button);

        // Получение данных тренировочного плана из Intent
        trainingPlan = (TrainingPlanItem) getIntent().getSerializableExtra("trainingPlan");
        if (trainingPlan != null) {
            trainingPlanId = getIntent().getStringExtra("trainingPlanId");
            trainingPlanNameEditText.setText(trainingPlan.getName());
            trainingPlanDateEditText.setText(trainingPlan.getDate());
            trainingPlanDescriptionEditText.setText(trainingPlan.getDescription());

            exerciseAdapter = new ExerciseAdapter(trainingPlan.getExercises(), this);
            exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            exercisesRecyclerView.setAdapter(exerciseAdapter);
        } else {
            Toast.makeText(this, "Ошибка: не удалось загрузить тренировку", Toast.LENGTH_SHORT).show();
            finish();
        }

        trainingPlanDateEditText.setOnClickListener(v -> showDatePicker());
        addExerciseButton.setOnClickListener(v -> openAddExerciseDialog());
        updateTrainingPlanButton.setOnClickListener(v -> updateTrainingPlan());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить упражнение");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_exercise, null);
        EditText nameEditText = dialogView.findViewById(R.id.add_exercise_name);
        EditText setsEditText = dialogView.findViewById(R.id.add_exercise_sets);
        EditText repsEditText = dialogView.findViewById(R.id.add_exercise_reps);
        EditText weightEditText = dialogView.findViewById(R.id.add_exercise_weight);
        EditText restTimeEditText = dialogView.findViewById(R.id.add_exercise_rest_time);

        builder.setView(dialogView);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            try {
                String name = nameEditText.getText().toString().trim();
                int sets = Integer.parseInt(setsEditText.getText().toString().trim());
                int reps = Integer.parseInt(repsEditText.getText().toString().trim());
                String weight = weightEditText.getText().toString().trim();
                int restTime = Integer.parseInt(restTimeEditText.getText().toString().trim());

                TrainingPlanItem.Exercise newExercise = new TrainingPlanItem.Exercise(name, sets, reps, weight, restTime);
                trainingPlan.getExercises().add(newExercise);
                exerciseAdapter.notifyItemInserted(trainingPlan.getExercises().size() - 1);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ошибка: неверный формат данных", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void updateTrainingPlan() {
        String name = trainingPlanNameEditText.getText().toString().trim();
        String date = trainingPlanDateEditText.getText().toString().trim();
        String description = trainingPlanDescriptionEditText.getText().toString().trim();

        if (name.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Заполните обязательные поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Обновление данных тренировочного плана
        trainingPlan.setName(name);
        trainingPlan.setDate(date);
        trainingPlan.setDescription(description);

        // Сохранение изменений в Firebase
        String trainerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("trainers").document(trainerId).collection("trainingPlans").document(trainingPlanId)
                .set(trainingPlan)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Тренировочный план обновлен");
                    Toast.makeText(this, "Изменения сохранены!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Ошибка обновления тренировочного плана:", e);
                    Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}