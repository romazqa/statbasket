package com.example.mycoach.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoach.ClientRequest;
import com.example.mycoach.ClientRequestsAdapter;
import com.example.mycoach.EditProfileActivity;
import com.example.mycoach.FindTrainerActivity;
import com.example.mycoach.R;
import com.example.mycoach.ui.start.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private ImageView avatarImageView;
    private TextView nameTextView, emailTextView,
            genderTextView, ageTextView, heightTextView,
            weightTextView, experienceLevelTextView, trainingGoalsTextView,
            specializationTextView, experienceTextViewTrainer, contactInfoTextView;
    private LinearLayout trainerSection, athleteSection;
    private RecyclerView clientRequestsRecyclerView;
    private Button logoutButton, editProfileButton;

    private FirebaseFirestore db;
    private String userId;
    private String userRole;
    private View view; // Объявляем view как поле класса

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        db = FirebaseFirestore.getInstance();

        // Инициализация элементов UI
        avatarImageView = view.findViewById(R.id.profile_avatar);
        nameTextView = view.findViewById(R.id.profile_name);
        emailTextView = view.findViewById(R.id.profile_email);
        genderTextView = view.findViewById(R.id.profile_gender);
        ageTextView = view.findViewById(R.id.profile_age);
        heightTextView = view.findViewById(R.id.profile_height);
        weightTextView = view.findViewById(R.id.profile_weight);
        experienceLevelTextView = view.findViewById(R.id.profile_experience_level);
        trainingGoalsTextView = view.findViewById(R.id.profile_training_goals);

        specializationTextView = view.findViewById(R.id.profile_specialization);
        experienceTextViewTrainer = view.findViewById(R.id.profile_experience);
        contactInfoTextView = view.findViewById(R.id.profile_contact_info);

        trainerSection = view.findViewById(R.id.trainer_section);
        athleteSection = view.findViewById(R.id.athlete_section);
        clientRequestsRecyclerView = view.findViewById(R.id.client_requests_recyclerview);
        logoutButton = view.findViewById(R.id.logout_button);
        editProfileButton = view.findViewById(R.id.edit_profile_button);


        clientRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), com.example.mycoach.ui.start.LoginActivity.class));
            getActivity().finish();
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("userRole", userRole);
            startActivity(intent);
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            loadUserProfile(userId);
        } else {
            Toast.makeText(getContext(), "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show();
            // Переход на экран входа
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }

        return view;
    }

    private void loadUserProfile(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            loadUserData(document);

                            // Проверка, является ли пользователь тренером
                            DocumentReference trainerRef = document.getDocumentReference("trainer");
                            if (trainerRef != null) {
                                // Пользователь - спортсмен, у него есть тренер
                                loadTrainerData(trainerRef);
                            } else {
                                checkIfTrainer(userId);
                            }
                        } else {
                            handleProfileLoadingError("Пользователь не найден.");
                        }
                    } else {
                        handleProfileLoadingError("Ошибка загрузки профиля.");
                    }
                });
    }

    private void checkIfTrainer(String userId) {
        db.collection("trainers").document(userId).get()
                .addOnCompleteListener(trainerTask -> {
                    if (trainerTask.isSuccessful()) {
                        DocumentSnapshot trainerDoc = trainerTask.getResult();
                        if (trainerDoc.exists()) {
                            // Пользователь - тренер
                            userRole = "trainer";
                            setupTrainerProfile(trainerDoc);
                        } else {
                            // Пользователь - спортсмен без тренера
                            userRole = "athlete";
                            setupAthleteProfile();
                        }
                    } else {
                        handleProfileLoadingError("Ошибка проверки роли тренера.");
                    }
                });
    }


    private void loadUserData(DocumentSnapshot document) {
        // Загрузка общих данных пользователя
        String firstName = document.getString("firstName");
        String lastName = document.getString("lastName");
        String email = document.getString("email");
        String gender = document.getString("gender");
        Long age = document.getLong("age");
        Long height = document.getLong("height");
        Double weight = document.getDouble("weight");
        String experienceLevel = document.getString("experienceLevel");
        String trainingGoals = document.getString("trainingGoals");

        nameTextView.setText("Имя: " + firstName + " " + lastName);
        emailTextView.setText("Email: " + email);
        genderTextView.setText("Пол: " + gender);
        ageTextView.setText("Возраст: " + (age != null ? age : ""));
        heightTextView.setText("Рост: " + (height != null ? height : "") + " см");
        weightTextView.setText("Вес: " + (weight != null ? weight : "") + " кг");
        experienceLevelTextView.setText("Уровень подготовки: " + experienceLevel);
        trainingGoalsTextView.setText("Цели тренировок: " + trainingGoals);
    }

    private void setupTrainerProfile(DocumentSnapshot document) {
        // Настройка профиля для тренера
        trainerSection.setVisibility(View.VISIBLE);
        athleteSection.setVisibility(View.GONE);

        String specialization = document.getString("specialization");
        Long experience = document.getLong("experience");
        String contactInfo = document.getString("contactInfo");

        specializationTextView.setText("Специализация: " + specialization);
        experienceTextViewTrainer.setText("Опыт: " + (experience != null ? experience : "") + " лет");
        contactInfoTextView.setText("Контакты: " + contactInfo);

        // Загрузка запросов на подключение
        loadClientRequests();
    }

    private void setupAthleteProfile() {
        // Настройка профиля для спортсмена
        trainerSection.setVisibility(View.GONE);
        athleteSection.setVisibility(View.VISIBLE);

        Button findTrainerButton = view.findViewById(R.id.find_trainer_button);
        findTrainerButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FindTrainerActivity.class);
            startActivity(intent);
        });
    }

    private void loadTrainerData(DocumentReference trainerRef) {
        trainerRef.get().addOnCompleteListener(trainerTask -> {
            if (trainerTask.isSuccessful()) {
                DocumentSnapshot trainerDoc = trainerTask.getResult();
                if (trainerDoc.exists()) {
                    String trainerName = trainerDoc.getString("firstName") + " " + trainerDoc.getString("lastName");
                    String trainerContact = trainerDoc.getString("contactInfo");

                    TextView trainerNameTextView = view.findViewById(R.id.profile_trainer_name);
                    trainerNameTextView.setText("Тренер: " + trainerName);
                    trainerNameTextView.setVisibility(View.VISIBLE);

                    TextView trainerContactTextView = view.findViewById(R.id.profile_trainer_contact);
                    trainerContactTextView.setText("Контакты тренера: " + trainerContact);
                    trainerContactTextView.setVisibility(View.VISIBLE);
                } else {
                    handleProfileLoadingError("Тренер не найден.");
                }
            } else {
                handleProfileLoadingError("Ошибка загрузки данных тренера.");
            }
        });
    }

    private void loadClientRequests() {
        String trainerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("trainers").document(trainerId).collection("clientRequests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ClientRequest> requests = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String clientId = document.getString("clientId");
                            String clientName = document.getString("clientName");
                            requests.add(new ClientRequest(document.getId(), trainerId, clientId, clientName));
                        }
                        clientRequestsRecyclerView.setAdapter(new ClientRequestsAdapter(requests, getContext()));
                    } else {
                        Log.w(TAG, "Ошибка получения запросов:", task.getException());
                        Toast.makeText(getContext(), "Ошибка загрузки запросов", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleProfileLoadingError(String errorMessage) {
        Log.w(TAG, "Ошибка: " + errorMessage);
        Toast.makeText(getContext(), "Ошибка: " + errorMessage, Toast.LENGTH_SHORT).show();
        // ... (дополнительные действия, например, скрыть элементы UI или показать сообщение об ошибке)
    }
}