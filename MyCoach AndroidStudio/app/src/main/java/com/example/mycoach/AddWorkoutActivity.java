package com.example.mycoach;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddWorkoutActivity extends AppCompatActivity {

    private static final String TAG = "AddWorkoutActivity";

    private EditText workoutNameEditText, workoutDateEditText, workoutTimeEditText, workoutDescriptionEditText;
    private Button addWorkoutButton;

    private FirebaseFirestore db;
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        db = FirebaseFirestore.getInstance();

        workoutNameEditText = findViewById(R.id.workout_name_edit_text);
        workoutDateEditText = findViewById(R.id.workout_date_edit_text);
        workoutTimeEditText = findViewById(R.id.workout_time_edit_text);
        workoutDescriptionEditText = findViewById(R.id.workout_description_edit_text);
        addWorkoutButton = findViewById(R.id.add_workout_button);

        // Обработчик нажатия на поле даты
        workoutDateEditText.setOnClickListener(view -> showDatePicker());

        // Обработчик нажатия на поле времени
        workoutTimeEditText.setOnClickListener(view -> showTimePicker());

        addWorkoutButton.setOnClickListener(view -> addWorkout());
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

    private void showTimePicker() {
        int hour = selectedDate.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDate.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDate.set(Calendar.MINUTE, minuteOfHour);
                    updateTimeEditText();
                }, hour, minute, true);
        timePickerDialog.show();
    }

    // Обновление текста в поле даты
    private void updateDateEditText() {
        String myFormat = "yyyy-MM-dd"; // Выберите желаемый формат
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        workoutDateEditText.setText(sdf.format(selectedDate.getTime()));
    }

    // Обновление текста в поле времени
    private void updateTimeEditText() {
        String myFormat = "HH:mm"; // Выберите желаемый формат
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        workoutTimeEditText.setText(sdf.format(selectedDate.getTime()));
    }

    private void addWorkout() {
        String workoutName = workoutNameEditText.getText().toString().trim();
        String workoutDate = workoutDateEditText.getText().toString().trim();
        String workoutTime = workoutTimeEditText.getText().toString().trim();
        String workoutDescription = workoutDescriptionEditText.getText().toString().trim();

        // Валидация данных
        if (TextUtils.isEmpty(workoutName)) {
            workoutNameEditText.setError("Введите название тренировки");
            return;
        }

        if (TextUtils.isEmpty(workoutDate)) {
            workoutDateEditText.setError("Выберите дату");
            return;
        }

        if (TextUtils.isEmpty(workoutTime)) {
            workoutTimeEditText.setError("Выберите время");
            return;
        }

        // ... (добавьте валидацию для других полей, если нужно)

        Map<String, Object> workout = new HashMap<>();
        workout.put("name", workoutName);
        workout.put("date", workoutDate);
        workout.put("time", workoutTime);
        workout.put("description", workoutDescription);
        // ... (добавление других полей)

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).collection("workouts").add(workout)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Тренировка добавлена с ID: " + documentReference.getId());
                    Toast.makeText(this, "Тренировка добавлена!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Ошибка добавления тренировки", e);
                    Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
