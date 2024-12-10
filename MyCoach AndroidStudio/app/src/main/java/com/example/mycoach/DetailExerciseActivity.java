package com.example.mycoach;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class DetailExerciseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_exercise); // Создайте этот макет

        TextView exerciseNameTextView = findViewById(R.id.detail_exercise_name);
        TextView exerciseDetailsTextView = findViewById(R.id.detail_exercise_details);

        Intent intent = getIntent();
        if (intent != null) {
            String exerciseName = intent.getStringExtra("exerciseName");
            int sets = intent.getIntExtra("exerciseSets", 0);
            int reps = intent.getIntExtra("exerciseReps", 0);
            String weight = intent.getStringExtra("exerciseWeight");
            int restTime = intent.getIntExtra("exerciseRestTime", 0);

            exerciseNameTextView.setText(exerciseName);
            exerciseDetailsTextView.setText(String.format(Locale.getDefault(),
                    "%d x %d, %s, %d сек. отдых", sets, reps, weight, restTime));
        } else {
            // Обработка ошибки, если Intent пустой
            Toast.makeText(this, "Ошибка: не удалось загрузить данные упражнения", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}