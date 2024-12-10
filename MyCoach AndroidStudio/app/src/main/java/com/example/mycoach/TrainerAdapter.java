package com.example.mycoach;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.TrainerViewHolder> {

    private static final String TAG = "TrainerAdapter";
    private List<Trainer> trainers;
    private Context context;
    private FirebaseFirestore db;

    public TrainerAdapter(List<Trainer> trainers, Context context) {
        this.trainers = trainers;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public TrainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trainer, parent, false);
        return new TrainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainerViewHolder holder, int position) {
        Trainer trainer = trainers.get(position);
        holder.trainerName.setText(trainer.getFirstName() + " " + trainer.getLastName());
        holder.trainerSpecialization.setText(trainer.getSpecialization());

        holder.sendRequestButton.setOnClickListener(view -> sendRequestToTrainer(trainer));
    }

    private void sendRequestToTrainer(Trainer trainer) {
        String clientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String clientName = "Имя клиента"; // Получите имя клиента из Firebase

        Map<String, Object> request = new HashMap<>();
        request.put("clientId", clientId);
        request.put("clientName", clientName);

        db.collection("trainers").document(trainer.getUserId()).collection("clientRequests")
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Запрос на подключение отправлен с ID: " + documentReference.getId());
                    Toast.makeText(context, "Запрос отправлен", Toast.LENGTH_SHORT).show();
                    // ... (опционально: обновить UI, например, скрыть кнопку "Отправить запрос")
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Ошибка отправки запроса:", e);
                    Toast.makeText(context, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return trainers.size();
    }

    // Метод для обновления списка тренеров (для фильтрации)
    public void updateList(List<Trainer> filteredTrainers) {
        trainers = filteredTrainers;
        notifyDataSetChanged();
    }

    public static class TrainerViewHolder extends RecyclerView.ViewHolder {
        TextView trainerName;
        TextView trainerSpecialization;
        Button sendRequestButton;

        public TrainerViewHolder(@NonNull View itemView) {
            super(itemView);
            trainerName = itemView.findViewById(R.id.trainer_name);
            trainerSpecialization = itemView.findViewById(R.id.trainer_specialization);
            sendRequestButton = itemView.findViewById(R.id.send_request_button);
        }
    }
}
