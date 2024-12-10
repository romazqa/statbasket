package com.example.mycoach;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<TrainingPlanItem.Exercise> exercises;
    private Context context;

    public ExerciseAdapter(List<TrainingPlanItem.Exercise> exercises, Context context) {
        this.exercises = exercises;
        this.context = context;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        TrainingPlanItem.Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());
        holder.exerciseDetails.setText(String.format(Locale.getDefault(),
                "%d x %d, %s, %d сек. отдых",
                exercise.getSets(), exercise.getReps(), exercise.getWeight(), exercise.getRestTime()));

        // Обработка кнопки редактирования
        holder.editButton.setOnClickListener(v -> showEditExerciseDialog(exercise, position));

        // Обработка кнопки удаления
        holder.deleteButton.setOnClickListener(v -> {
            exercises.remove(position);
            notifyItemRemoved(position);
        });
    }

    private void showEditExerciseDialog(TrainingPlanItem.Exercise exercise, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Редактировать упражнение");

        // Создание полей ввода для редактирования
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_exercise, null);
        EditText nameEditText = dialogView.findViewById(R.id.edit_exercise_name);
        EditText setsEditText = dialogView.findViewById(R.id.edit_exercise_sets);
        EditText repsEditText = dialogView.findViewById(R.id.edit_exercise_reps);
        EditText weightEditText = dialogView.findViewById(R.id.edit_exercise_weight);
        EditText restTimeEditText = dialogView.findViewById(R.id.edit_exercise_rest_time);

        // Установка текущих значений в поля ввода
        nameEditText.setText(exercise.getName());
        setsEditText.setText(String.valueOf(exercise.getSets()));
        repsEditText.setText(String.valueOf(exercise.getReps()));
        weightEditText.setText(exercise.getWeight());
        restTimeEditText.setText(String.valueOf(exercise.getRestTime()));

        builder.setView(dialogView);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            // Обновление данных упражнения
            try {
                String name = nameEditText.getText().toString().trim();
                int sets = Integer.parseInt(setsEditText.getText().toString().trim());
                int reps = Integer.parseInt(repsEditText.getText().toString().trim());
                String weight = weightEditText.getText().toString().trim();
                int restTime = Integer.parseInt(restTimeEditText.getText().toString().trim());

                exercise.setName(name);
                exercise.setSets(sets);
                exercise.setReps(reps);
                exercise.setWeight(weight);
                exercise.setRestTime(restTime);

                notifyItemChanged(position); // Обновление элемента в RecyclerView
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Ошибка: неверный формат данных", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }


    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView exerciseDetails;
        ImageButton editButton;
        ImageButton deleteButton;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            exerciseDetails = itemView.findViewById(R.id.exercise_details);
            editButton = itemView.findViewById(R.id.edit_exercise_button);
            deleteButton = itemView.findViewById(R.id.delete_exercise_button);
        }
    }
}