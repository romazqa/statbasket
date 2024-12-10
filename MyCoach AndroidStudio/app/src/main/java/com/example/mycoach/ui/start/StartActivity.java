package com.example.mycoach.ui.start;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.mycoach.R;
import com.example.mycoach.ui.start.StartFragment;


public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.start_fragment_container, new StartFragment()) // R.id.start_fragment_container из activity_start.xml
                .commit();
    }
}
