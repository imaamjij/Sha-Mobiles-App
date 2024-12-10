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
import com.example.shamobiles.adapters.ManagerServicesPagerAdapter;
import com.google.firebase.firestore.DocumentSnapshot;

public class ManagerDashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView welcomeText;
    private TextView mechanic1RevenueText;
    private TextView mechanic2RevenueText;
    private TextView managerRevenueText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String managerId = mAuth.getCurrentUser().getUid();

        initializeViews();
        loadUserName(managerId);
        setupViewPager();
        setupButtons();
        loadRevenues();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        welcomeText = findViewById(R.id.welcomeText);
        mechanic1RevenueText = findViewById(R.id.mechanic1RevenueText);
        mechanic2RevenueText = findViewById(R.id.mechanic2RevenueText);
        managerRevenueText = findViewById(R.id.managerRevenueText);
    }

    private void setupViewPager() {
        String managerId = mAuth.getCurrentUser().getUid();
        ManagerServicesPagerAdapter pagerAdapter = new ManagerServicesPagerAdapter(this, managerId);
        viewPager.setAdapter(pagerAdapter);

        // Setup TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(pagerAdapter.getPageTitle(position));
        }).attach();

        // Style the tabs
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.purple_500));
    }

    private void loadRevenues() {
        String managerId = mAuth.getCurrentUser().getUid();
        double[] totalRevenue = {0.0};
        double[] mechanic1Revenue = {0.0};
        double[] mechanic2Revenue = {0.0};
        double[] managerRevenue = {0.0};

        // Load mechanic services revenue
        db.collection("services")
                .get()
                .addOnSuccessListener(serviceSnapshot -> {
                    for (DocumentSnapshot doc : serviceSnapshot.getDocuments()) {
                        Double price = doc.getDouble("price");
                        String mechanicId = doc.getString("mechanicId");
                        
                        if (price != null && mechanicId != null) {
                            totalRevenue[0] += price;
                            
                            // Get mechanic role
                            db.collection("users")
                                    .document(mechanicId)
                                    .get()
                                    .addOnSuccessListener(mechanicDoc -> {
                                        String role = mechanicDoc.getString("role");
                                        if ("mechanic1".equals(role)) {
                                            mechanic1Revenue[0] += price;
                                        } else if ("mechanic2".equals(role)) {
                                            mechanic2Revenue[0] += price;
                                        }
                                        updateRevenueTexts(totalRevenue[0], mechanic1Revenue[0], 
                                                         mechanic2Revenue[0], managerRevenue[0]);
                                    });
                        }
                    }

                    // Load accessory sales revenue
                    db.collection("accessory_sales")
                            .whereEqualTo("managerId", managerId)
                            .get()
                            .addOnSuccessListener(salesSnapshot -> {
                                for (DocumentSnapshot doc : salesSnapshot.getDocuments()) {
                                    Double price = doc.getDouble("price");
                                    Long quantity = doc.getLong("quantity");
                                    if (price != null && quantity != null) {
                                        double saleTotal = price * quantity;
                                        managerRevenue[0] += saleTotal;
                                        totalRevenue[0] += saleTotal;
                                    }
                                }
                                updateRevenueTexts(totalRevenue[0], mechanic1Revenue[0], 
                                                 mechanic2Revenue[0], managerRevenue[0]);
                            });
                });
    }

    private void updateRevenueTexts(double total, double mech1, double mech2, double manager) {
        mechanic1RevenueText.setText(String.format("Elumalai Revenue: ₹%.2f", mech1));
        mechanic2RevenueText.setText(String.format("Nizar Revenue: ₹%.2f", mech2));
        managerRevenueText.setText(String.format("Accessory Sales: ₹%.2f", manager));
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

    private void loadUserName(String userId) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    String role = document.getString("role");
                    if ("manager".equals(role)) {
                        welcomeText.setText("Welcome Javeed");
                    } else {
                        welcomeText.setText("Welcome Manager");
                    }
                })
                .addOnFailureListener(e -> 
                    welcomeText.setText("Welcome Manager")
                );
    }
} 