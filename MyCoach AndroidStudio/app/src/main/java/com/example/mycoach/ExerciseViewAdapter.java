package com.example.mycoach;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoach.R;
import com.example.mycoach.TrainingPlanItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class ExerciseViewAdapter extends RecyclerView.Adapter<ExerciseViewAdapter.ExerciseViewHolder> {

    private static final String TAG = "ExerciseViewAdapter";
    private List<TrainingPlanItem.Exercise> exercises;
    private Context context;
    private FirebaseFirestore db;
    private String trainingPlanId; // ID тренировочного плана 

    public ExerciseViewAdapter(List<TrainingPlanItem.Exercise> exercises, Context context, String trainingPlanId) {
        this.exercises = exercises;
        this.context = context;
        this.trainingPlanId = trainingPlanId;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_view, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        TrainingPlanItem.Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());
        holder.exerciseDetails.setText(String.format(Locale.getDefault(),
                "%d x %d, %s, %d сек. отдых",
                exercise.getSets(), exercise.getReps(), exercise.getWeight(), exercise.getRestTime()));

        // Обработка CheckBox для отметки выполненных упражнений
        holder.completedCheckBox.setOnCheckedChangeListener(null);
        holder.completedCheckBox.setChecked(exercise.isCompleted());
        holder.completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            exercise.setCompleted(isChecked);
            saveExerciseCompletionStatus(exercise, position, isChecked);
        });
    }

    private void saveExerciseCompletionStatus(TrainingPlanItem.Exercise exercise, int position, boolean isChecked) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Предполагаем, что упражнения хранятся в подколлекции "exercises" документа тренировочного плана
        db.collection("users").document(userId)
                .collection("trainingPlans").document(trainingPlanId)
                .collection("exercises").document(String.valueOf(position)) // Используем позицию как ID упражнения
                .update("completed", isChecked)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Состояние упражнения обновлено");
                    Toast.makeText(context, "Состояние упражнения обновлено", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Ошибка обновления состояния упражнения:", e);
                    Toast.makeText(context, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Вернуть CheckBox в предыдущее состояние
                    notifyItemChanged(position);
                });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView exerciseDetails;
        CheckBox completedCheckBox;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_view_name);
            exerciseDetails = itemView.findViewById(R.id.exercise_view_details);
            completedCheckBox = itemView.findViewById(R.id.exercise_completed_checkbox);
        }
    }
}