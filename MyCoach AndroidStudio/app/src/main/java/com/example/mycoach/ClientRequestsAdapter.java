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
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ClientRequestsAdapter extends RecyclerView.Adapter<ClientRequestsAdapter.ClientRequestViewHolder> {

    private static final String TAG = "ClientRequestsAdapter";
    private List<ClientRequest> clientRequests;
    private FirebaseFirestore db;
    private Context context;

    public ClientRequestsAdapter(List<ClientRequest> clientRequests, Context context) {
        this.clientRequests = clientRequests;
        this.db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    @NonNull
    @Override
    public ClientRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client_request, parent, false);
        return new ClientRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientRequestViewHolder holder, int position) {
        ClientRequest request = clientRequests.get(position);
        holder.clientName.setText(request.getClientName());

        holder.acceptButton.setOnClickListener(v -> {
            acceptRequest(request, position);
        });

        holder.rejectButton.setOnClickListener(v -> {
            rejectRequest(request, position);
        });
    }

    private void acceptRequest(ClientRequest request, int position) {
        // 1. Обновить данные пользователя (клиента) в Firebase, установив поле "trainer"
        db.collection("users").document(request.getClientId())
                .update("trainer", db.collection("trainers").document(request.getTrainerId()))
                .addOnSuccessListener(aVoid -> {
                    // 2. Удалить запрос из коллекции "clientRequests"
                    db.collection("trainers").document(request.getTrainerId())
                            .collection("clientRequests").document(request.getRequestId())
                            .delete()
                            .addOnSuccessListener(aVoid1 -> {
                                clientRequests.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Запрос принят", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Ошибка удаления запроса:", e);
                                Toast.makeText(context, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Ошибка обновления данных пользователя:", e);
                    Toast.makeText(context, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectRequest(ClientRequest request, int position) {
        // Удаление запроса из Firebase
        db.collection("trainers").document(request.getTrainerId())
                .collection("clientRequests").document(request.getRequestId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    clientRequests.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Запрос отклонен", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Ошибка удаления запроса:", e);
                    Toast.makeText(context, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return clientRequests.size();
    }

    public static class ClientRequestViewHolder extends RecyclerView.ViewHolder {
        TextView clientName;
        Button acceptButton;
        Button rejectButton;

        public ClientRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            clientName = itemView.findViewById(R.id.client_name);
            acceptButton = itemView.findViewById(R.id.accept_request_button);
            rejectButton = itemView.findViewById(R.id.reject_request_button);
        }
    }
}