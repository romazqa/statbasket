package com.example.mycoach;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TrainingPlanAdapter extends RecyclerView.Adapter<TrainingPlanAdapter.TrainingPlanViewHolder> {

    private static final String TAG = "TrainingPlanAdapter";
    private List<TrainingPlanItem> trainingPlanItems;
    private Context context;

    public TrainingPlanAdapter(List<TrainingPlanItem> trainingPlanItems, Context context) {
        this.trainingPlanItems = trainingPlanItems;
        this.context = context;
    }

    @NonNull
    @Override
    public TrainingPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training_plan, parent, false);
        return new TrainingPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingPlanViewHolder holder, int position) {
        TrainingPlanItem planItem = trainingPlanItems.get(position);

        holder.trainingName.setText(planItem.getName());
        holder.trainingDate.setText(planItem.getDate());
        holder.trainingDescription.setText(planItem.getDescription());
        holder.trainingStatus.setText(planItem.getStatus());
        holder.trainingComments.setText(planItem.getComments());

        // Настройка RecyclerView для упражнений
        holder.exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.exercisesRecyclerView.setAdapter(new ExerciseAdapter(planItem.getExercises()));

        // Обработка нажатий
        holder.itemView.setOnClickListener(v -> {
            String userRole = getUserRole();
            if ("trainer".equals(userRole)) {
                // Открыть окно редактирования для тренера
                openEditTrainingPlanActivity(planItem, position);
            } else if ("athlete".equals(userRole)) {
                // Открыть окно просмотра/отметки прогресса для спортсмена
                openViewTrainingPlanActivity(planItem);
            } else {
                Toast.makeText(context, "Ошибка: роль пользователя не определена", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Методы для открытия активностей редактирования и просмотра
    private void openEditTrainingPlanActivity(TrainingPlanItem planItem, int position) {
        Intent intent = new Intent(context, EditTrainingPlanActivity.class);
        intent.putExtra("trainingPlan", planItem);
        intent.putExtra("trainingPlanId", getItemId(position));
        context.startActivity(intent);
    }


    private void openViewTrainingPlanActivity(TrainingPlanItem planItem) {
        Intent intent = new Intent(context, ViewTrainingPlanActivity.class);
        intent.putExtra("trainingPlan", planItem);
        context.startActivity(intent);
    }

    // Вспомогательный метод для получения роли пользователя
    private String getUserRole() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return (String) user.getIdToken(false)
                    .getResult()
                    .getClaims()
                    .get("role");
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return trainingPlanItems.size();
    }

    public static class TrainingPlanViewHolder extends RecyclerView.ViewHolder {
        TextView trainingName;
        TextView trainingDate;
        TextView trainingDescription;
        TextView trainingStatus;
        TextView trainingComments;
        RecyclerView exercisesRecyclerView;

        public TrainingPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            trainingName = itemView.findViewById(R.id.training_plan_name);
            trainingDate = itemView.findViewById(R.id.training_plan_date);
            trainingDescription = itemView.findViewById(R.id.training_plan_description);
            trainingStatus = itemView.findViewById(R.id.training_plan_status);
            trainingComments = itemView.findViewById(R.id.training_plan_comments);
            exercisesRecyclerView = itemView.findViewById(R.id.exercises_recycler_view);
        }
    }

    private Map<String, Long> itemIdMap = new HashMap<>(); // Карта для хранения соответствия ID документа и itemId
    private long nextItemId = 0; // Счетчик для генерации itemId

    @Override
    public long getItemId(int position) {
        String documentId = trainingPlanItems.get(position).getId();
        if (!itemIdMap.containsKey(documentId)) {
            // Если ID документа еще нет в карте, генерируем новый itemId
            itemIdMap.put(documentId, nextItemId++);
        }
        return itemIdMap.get(documentId);
    }

    // Адаптер для упражнений
    public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

        private List<TrainingPlanItem.Exercise> exercises;

        public ExerciseAdapter(List<TrainingPlanItem.Exercise> exercises) {
            this.exercises = exercises;
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
            holder.itemView.setOnClickListener(v -> {
                // Получаем позицию элемента
                int currentPosition = holder.getAdapterPosition();

                // Создаем Intent для открытия DetailExerciseActivity
                Intent intent = new Intent(context, DetailExerciseActivity.class);

                // Передаем данные упражнения в Intent
                intent.putExtra("exerciseName", exercise.getName());
                intent.putExtra("exerciseSets", exercise.getSets());
                intent.putExtra("exerciseReps", exercise.getReps());
                intent.putExtra("exerciseWeight", exercise.getWeight());
                intent.putExtra("exerciseRestTime", exercise.getRestTime());

                // Запускаем DetailExerciseActivity
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }

        public class ExerciseViewHolder extends RecyclerView.ViewHolder {
            TextView exerciseName;
            TextView exerciseDetails;

            public ExerciseViewHolder(@NonNull View itemView) {
                super(itemView);
                exerciseName = itemView.findViewById(R.id.exercise_name);
                exerciseDetails = itemView.findViewById(R.id.exercise_details);
            }
        }
    }
}