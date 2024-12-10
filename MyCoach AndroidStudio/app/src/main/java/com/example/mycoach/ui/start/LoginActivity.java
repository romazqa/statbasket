package com.example.mycoach.ui.start;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mycoach.MainActivity;
import com.example.mycoach.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText emailEditText, passwordEditText;
    private Button loginButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.login_email_edit_text);
        passwordEditText = findViewById(R.id.login_password_edit_text);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        // !!! ЗАМЕНИТЕ НА РЕАЛЬНОЕ ХЭШИРОВАНИЕ !!!
        // Здесь должен быть код для хэширования пароля, например, с помощью bcrypt/scrypt

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            getUserRoleAndStartMain(user);
                        } else {
                            handleLoginError("Ошибка аутентификации: пользователь не найден");
                        }
                    } else {
                        handleLoginError("Ошибка аутентификации");
                    }
                });
    }

    private void getUserRoleAndStartMain(FirebaseUser user) {
        user.getIdToken(true)
                .addOnCompleteListener(getTokenTask -> {
                    if (getTokenTask.isSuccessful()) {
                        String role = (String) getTokenTask.getResult().getClaims().get("role");
                        startMainActivity(role);
                    } else {
                        handleLoginError("Ошибка получения роли пользователя");
                    }
                });
    }



    private void startMainActivity(String role) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userRole", role);
        startActivity(intent);
        finish();
    }



    private void handleLoginError(String errorMessage) {
        Log.w(TAG, errorMessage);
        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        // ... (другие действия, например, очистка полей ввода)
    }
}