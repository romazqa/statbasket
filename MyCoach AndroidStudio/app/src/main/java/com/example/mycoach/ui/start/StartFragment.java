package com.example.mycoach.ui.start;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mycoach.R;
import com.example.mycoach.RegisterActivity; // Замените на ваш пакет

public class StartFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        // Устанавливаем фоновый градиент
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ContextCompat.getColor(requireContext(), R.color.light_pink),
                        ContextCompat.getColor(requireContext(), R.color.light_blue)}); // Розовый и голубой
        layout.setBackground(gradient);

        // Создаем заголовок
        TextView titleView = new TextView(getContext());
        titleView.setText("Мой Тренер");
        titleView.setTextSize(30f);
        titleView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black)); // Измените цвет
        titleView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        ((LinearLayout.LayoutParams) titleView.getLayoutParams()).setMargins(0, 0, 0, 32); // Установите отступы
        layout.addView(titleView);


        // Создаем кнопку "Войти"
        Button loginButton = new Button(getContext());
        loginButton.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        loginButton.setText("Войти");

        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
        layout.addView(loginButton);

        // Создаем кнопку "Зарегистрироваться"
        Button registerButton = new Button(getContext());
        registerButton.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        registerButton.setText("Зарегестрироваться");
        // ... (добавьте стили кнопки)
        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), RegisterActivity.class); // Замените RegisterActivity.class
            startActivity(intent);
        });
        layout.addView(registerButton);

        return layout;
    }
}