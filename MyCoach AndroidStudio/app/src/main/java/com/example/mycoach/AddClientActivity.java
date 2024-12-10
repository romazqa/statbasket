package com.example.mycoach;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddClientActivity extends AppCompatActivity {

    private static final String TAG = "AddClientActivity";

    private EditText clientNameEditText, clientEmailEditText;
    // ... (другие поля)
    private Button addClientButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        db = FirebaseFirestore.getInstance();

        clientNameEditText = findViewById(R.id.client_name_edit_text);
        clientEmailEditText = findViewById(R.id.client_email_edit_text);
        // ... (инициализация других полей)
        addClientButton = findViewById(R.id.add_client_button);

        addClientButton.setOnClickListener(view -> addClient());
    }

    private void addClient() {
        String clientName = clientNameEditText.getText().toString().trim();
        String clientEmail = clientEmailEditText.getText().toString().trim();

        // Валидация данных
        if (TextUtils.isEmpty(clientName)) {
            clientNameEditText.setError("Введите имя клиента");
            return;
        }

        if (TextUtils.isEmpty(clientEmail) || !Patterns.EMAIL_ADDRESS.matcher(clientEmail).matches()) {
            clientEmailEditText.setError("Введите корректный email");
            return;
        }

        // ... (добавьте валидацию для других полей, если нужно)

        Map<String, Object> client = new HashMap<>();
        client.put("name", clientName);
        client.put("email", clientEmail);
        // ... (добавление других полей)

        String trainerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("trainers").document(trainerId).collection("clients").add(client)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Клиент добавлен с ID: " + documentReference.getId());
                    Toast.makeText(this, "Клиент добавлен!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Ошибка добавления клиента", e);
                    Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
