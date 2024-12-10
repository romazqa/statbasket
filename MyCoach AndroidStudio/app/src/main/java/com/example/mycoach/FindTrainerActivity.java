package com.example.mycoach;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FindTrainerActivity extends AppCompatActivity {

    private static final String TAG = "FindTrainerActivity";

    private RecyclerView trainersRecyclerView;
    private SearchView searchView;

    private FirebaseFirestore db;
    private List<Trainer> trainers = new ArrayList<>();
    private TrainerAdapter trainerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trainer); //  Создайте этот макет

        db = FirebaseFirestore.getInstance();
        trainersRecyclerView = findViewById(R.id.trainers_recyclerview);
        searchView = findViewById(R.id.trainer_search_view);

        trainerAdapter = new TrainerAdapter(trainers, this);
        trainersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trainersRecyclerView.setAdapter(trainerAdapter);

        loadTrainers();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Фильтрация тренеров по запросу
                filterTrainers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Фильтрация тренеров по мере ввода текста
                filterTrainers(newText);
                return true;
            }
        });
    }

    private void loadTrainers() {
        db.collection("trainers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        trainers.clear(); // Очистить список перед загрузкой новых данных
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Trainer trainer = document.toObject(Trainer.class);
                            trainers.add(trainer);
                        }
                        trainerAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Ошибка загрузки тренеров:", task.getException());
                        Toast.makeText(this, "Ошибка загрузки тренеров", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterTrainers(String query) {
        List<Trainer> filteredTrainers = new ArrayList<>();
        for (Trainer trainer : trainers) {
            // Проверка, содержит ли имя тренера или специализация запрос
            if (trainer.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                    trainer.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                    trainer.getSpecialization().toLowerCase().contains(query.toLowerCase())) {
                filteredTrainers.add(trainer);
            }
        }
        // Обновление адаптера с отфильтрованным списком
        trainerAdapter.updateList(filteredTrainers);
    }
}
