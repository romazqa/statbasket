package com.example.mycoach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class TrainingPlanActivity extends AppCompatActivity {

    private static final String TAG = "TrainingPlanActivity";

    private RecyclerView trainingPlanRecyclerView;
    private FloatingActionButton addButton;

    private FirebaseFirestore db;
    private List<TrainingPlanItem> trainingPlanItems = new ArrayList<>();
    private TrainingPlanAdapter trainingPlanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_plan);

        db = FirebaseFirestore.getInstance();
        trainingPlanRecyclerView = findViewById(R.id.training_plan_recyclerview);
        addButton = findViewById(R.id.add_button);

        trainingPlanAdapter = new TrainingPlanAdapter(trainingPlanItems, this);
        trainingPlanRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trainingPlanRecyclerView.setAdapter(trainingPlanAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            loadTrainingPlan(userId);

            user.getIdToken(true)
                    .addOnSuccessListener(getIdTokenResult -> {
                        String role = (String) getIdTokenResult.getClaims().get("role");
                        if ("trainer".equals(role)) {
                            setupTrainerUI(userId);
                        } else if ("athlete".equals(role)) {
                            setupAthleteUI(userId);
                        } else {
                            Log.e(TAG, "Ошибка: Неизвестная роль пользователя");
                            Toast.makeText(this, "Ошибка: роль пользователя не определена", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Ошибка получения роли пользователя", e);
                        Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } else {
            Toast.makeText(this, "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupTrainerUI(String userId) {
        addButton.setVisibility(View.VISIBLE);

        addButton.setOnClickListener(view -> {
            // Intent для открытия активности добавления тренировки
            Intent intent = new Intent(TrainingPlanActivity.this, AddTrainingPlanActivity.class);
            intent.putExtra("userId", userId); // Передаем ID пользователя, если нужно
            startActivity(intent);
        });
    }

    private void setupAthleteUI(String userId) {
        addButton.setVisibility(View.GONE);
    }

    private void loadTrainingPlan(String userId) {
        String collectionPath = getUserRole().equals("trainer") ? "trainers" : "users";

        db.collection(collectionPath).document(userId).collection("trainingPlans")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        trainingPlanItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            TrainingPlanItem planItem = document.toObject(TrainingPlanItem.class);
                            planItem.setId(document.getId());
                            trainingPlanItems.add(planItem);
                        }
                        trainingPlanAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Ошибка загрузки тренировочного плана:", task.getException());
                        Toast.makeText(this, "Ошибка загрузки тренировочного плана", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getUserRole() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return (String) user.getIdToken(false).getResult().getClaims().get("role");
        }
        return null;
    }
}