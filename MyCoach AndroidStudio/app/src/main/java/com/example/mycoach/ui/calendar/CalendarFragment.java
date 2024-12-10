package com.example.mycoach.ui.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoach.CalendarTasksAdapter;
import com.example.mycoach.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private static final String TAG = "CalendarFragment";

    private LinearLayout calendarDaysLayout;
    private RecyclerView calendarTasksRecyclerView;
    private Button allButton, todoButton, inProgressButton, completedButton;

    private FirebaseFirestore db;
    private List<Task> allTasks = new ArrayList<>();
    private Calendar selectedDate = Calendar.getInstance();
    private SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
    private SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        db = FirebaseFirestore.getInstance();
        calendarDaysLayout = view.findViewById(R.id.calendar_days_layout);
        calendarTasksRecyclerView = view.findViewById(R.id.calendar_tasks_recyclerview);
        allButton = view.findViewById(R.id.calendar_filter_all_button);
        todoButton = view.findViewById(R.id.calendar_filter_todo_button);
        inProgressButton = view.findViewById(R.id.calendar_filter_inprogress_button);
        completedButton = view.findViewById(R.id.calendar_filter_completed_button);

        setupCalendarDays();
        setupRecyclerView();
        setupFilterButtons();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loadTasks(user.getUid());
        } else {
            // Обработка случая, когда пользователь не авторизован
            Toast.makeText(getContext(), "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show();
            // ... (дополнительные действия, например, переход на экран входа)
        }

        return view;
    }

    private void setupCalendarDays() {
        Calendar calendar = (Calendar) selectedDate.clone();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // Начинаем с первого дня недели

        for (int i = 0; i < 7; i++) {
            View dayView = LayoutInflater.from(getContext()).inflate(R.layout.item_calendar_day, calendarDaysLayout, false);
            TextView dayNumber = dayView.findViewById(R.id.calendar_day_number);
            TextView dayOfWeek = dayView.findViewById(R.id.calendar_day_of_week);

            dayNumber.setText(dayFormat.format(calendar.getTime()));
            dayOfWeek.setText(dayOfWeekFormat.format(calendar.getTime()));

            // Устанавливаем слушатель для каждого дня
            dayView.setOnClickListener(v -> {
                int clickedDay = Integer.parseInt(dayNumber.getText().toString());
                int clickedMonth = calendar.get(Calendar.MONTH);
                int clickedYear = calendar.get(Calendar.YEAR);

                selectedDate.set(clickedYear, clickedMonth, clickedDay);
                updateSelectedDayBackground();
                loadTasksForSelectedDate(FirebaseAuth.getInstance().getCurrentUser().getUid());
            });

            calendarDaysLayout.addView(dayView);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        updateSelectedDayBackground(); // Выделяем текущий день при запуске
    }

    private void updateSelectedDayBackground() {
        // Сброс фона для всех дней
        for (int i = 0; i < calendarDaysLayout.getChildCount(); i++) {
            calendarDaysLayout.getChildAt(i).setBackgroundResource(0);
        }

        // Установка фона для выбранного дня
        Calendar calendar = (Calendar) selectedDate.clone();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        int daysDifference = (int) ((selectedDate.getTimeInMillis() - calendar.getTimeInMillis()) / (1000 * 60 * 60 * 24));
        if (daysDifference >= 0 && daysDifference < 7) {
            calendarDaysLayout.getChildAt(daysDifference).setBackgroundResource(R.drawable.selected_day_background);
        }
    }

    private void setupRecyclerView() {
        calendarTasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        calendarTasksRecyclerView.setAdapter(new CalendarTasksAdapter(allTasks));
    }

    private void setupFilterButtons() {
        allButton.setOnClickListener(view -> filterTasks("All"));
        todoButton.setOnClickListener(view -> filterTasks("To-do"));
        inProgressButton.setOnClickListener(view -> filterTasks("In Progress"));
        completedButton.setOnClickListener(view -> filterTasks("Completed"));
    }

    private void filterTasks(String status) {
        List<Task> filteredTasks = new ArrayList<>();
        if (status.equals("All")) {
            filteredTasks.addAll(allTasks);
        } else {
            for (Task task : allTasks) {
                if (task.getStatus().equals(status)) {
                    filteredTasks.add(task);
                }
            }
        }
        calendarTasksRecyclerView.setAdapter(new CalendarTasksAdapter(filteredTasks));
    }

    private void loadTasksForSelectedDate(String userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = dateFormat.format(selectedDate.getTime());

        db.collection("users").document(userId).collection("tasks")
                .whereEqualTo("date", dateString) // Фильтрация по дате
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allTasks.clear(); // Очищаем список задач перед загрузкой новых
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Преобразуем документ Firestore в объект Task
                            Task taskItem = document.toObject(Task.class);

                            // Дополнительная обработка данных, если необходимо
                            // Например, преобразование timestamp в удобочитаемую дату и время

                            // Добавляем задачу в список
                            allTasks.add(taskItem);
                        }
                        // Обновляем адаптер RecyclerView
                        calendarTasksRecyclerView.getAdapter().notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Ошибка получения задач:", task.getException());
                        Toast.makeText(getContext(), "Ошибка загрузки задач", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTasks(String userId) {
        loadTasksForSelectedDate(userId); // Загружаем задачи для текущей даты при запуске
    }

    // Вспомогательный класс для представления задачи
    public static class Task {
        private String title;
        private String time;
        private String status;
        private String date;
        // ... другие поля

        public Task() {} // Пустой конструктор необходим для Firestore

        // Геттеры
        public String getTitle() {
            return title;
        }

        public String getTime() {
            return time;
        }

        public String getStatus() {
            return status;
        }

        public String getDate() {
            return date;
        }

        // Сеттеры
        public void setTitle(String title) {
            this.title = title;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}