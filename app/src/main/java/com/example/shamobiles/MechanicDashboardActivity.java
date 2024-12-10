package com.example.shamobiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.shamobiles.adapters.MechanicPagerAdapter;

public class MechanicDashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String mechanicId = mAuth.getCurrentUser().getUid();

        initializeViews();
        loadUserName(mechanicId);
        setupViewPager(mechanicId);
        setupButtons();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        welcomeText = findViewById(R.id.welcomeText);
    }

    private void loadUserName(String userId) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    String username = document.getString("username");
                    if (username != null) {
                        welcomeText.setText("Welcome " + username);
                    }
                })
                .addOnFailureListener(e -> 
                    welcomeText.setText("Welcome Mechanic")
                );
    }

    private void setupViewPager(String mechanicId) {
        MechanicPagerAdapter pagerAdapter = new MechanicPagerAdapter(this, mechanicId);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "New Service" : "Service Records");
        }).attach();
    }

    private void setupButtons() {
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
} 