package com.example.mycoach.ui.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import com.example.mycoach.ListAdapter;
import com.example.mycoach.ListItem;
import com.example.mycoach.R;


public class ListFragment extends Fragment {

    private static final String TAG = "ListFragment";

    private RecyclerView listRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListAdapter listAdapter;
    private List<ListItem> listItems = new ArrayList<>();

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        db = FirebaseFirestore.getInstance();
        listRecyclerView = view.findViewById(R.id.list_recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        listRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new ListAdapter(listItems, getContext()); // Передаем контекст
        listRecyclerView.setAdapter(listAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        loadData(); // Первичная загрузка данных
        return view;
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true); // Показываем индикатор загрузки

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Определяем, какую коллекцию использовать в зависимости от роли пользователя
        String collectionPath = getUserRole().equals("trainer") ? "trainers" : "users";


        db.collection(collectionPath).document(userId).collection("items") // Замените "items" на вашу коллекцию, если нужно
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListItem item = document.toObject(ListItem.class);
                            listItems.add(item);
                        }
                        listAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                });
    }


    // Вспомогательный метод для получения роли пользователя
    private String getUserRole() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return (String) user.getIdToken(false).getResult().getClaims().get("role");
        }
        return null; // Или другая обработка, если пользователь не авторизован
    }


}
