package com.example.shamobiles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.shamobiles.adapters.OwnerPagerAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwnerDashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView totalRevenueText, mechanic1RevenueText, mechanic2RevenueText, managerRevenueText;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String ownerId = mAuth.getCurrentUser().getUid();

        initializeViews();
        loadUserName(ownerId);
        setupViewPager();
        setupButtons();
        loadRevenue();
    }

    private void initializeViews() {
        totalRevenueText = findViewById(R.id.totalRevenueText);
        mechanic1RevenueText = findViewById(R.id.mechanic1RevenueText);
        mechanic2RevenueText = findViewById(R.id.mechanic2RevenueText);
        managerRevenueText = findViewById(R.id.managerRevenueText);
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
                    welcomeText.setText("Welcome Owner")
                );
    }

    private void setupViewPager() {
        OwnerPagerAdapter pagerAdapter = new OwnerPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(pagerAdapter.getPageTitle(position));
        }).attach();

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
    }

    private void setupButtons() {
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> logout());
    }

    private void loadRevenue() {
        Map<String, Double> revenueMap = new HashMap<>();
        final double[] totalRevenue = {0.0};

        // Load mechanics data first
        db.collection("users")
                .whereIn("role", Arrays.asList("mechanic1", "mechanic2"))
                .get()
                .addOnSuccessListener(mechanicsSnapshot -> {
                    // Load services data
                    db.collection("services")
                            .get()
                            .addOnSuccessListener(servicesSnapshot -> {
                                for (DocumentSnapshot doc : servicesSnapshot.getDocuments()) {
                                    Double price = doc.getDouble("price");
                                    String mechanicId = doc.getString("mechanicId");
                                    
                                    if (price != null && mechanicId != null) {
                                        totalRevenue[0] += price;
                                        double currentRevenue = revenueMap.getOrDefault(mechanicId, 0.0);
                                        revenueMap.put(mechanicId, currentRevenue + price);
                                    }
                                }

                                // Load accessory sales data
                                db.collection("accessory_sales")
                                        .get()
                                        .addOnSuccessListener(salesSnapshot -> {
                                            for (DocumentSnapshot doc : salesSnapshot.getDocuments()) {
                                                Double price = doc.getDouble("price");
                                                Long quantity = doc.getLong("quantity");
                                                String managerId = doc.getString("managerId");
                                                
                                                if (price != null && quantity != null) {
                                                    double saleTotal = price * quantity;
                                                    totalRevenue[0] += saleTotal;
                                                    if (managerId != null) {
                                                        double currentManagerRevenue = revenueMap.getOrDefault("manager", 0.0);
                                                        revenueMap.put("manager", currentManagerRevenue + saleTotal);
                                                    }
                                                }
                                            }
                                            updateRevenueDisplay(mechanicsSnapshot.getDocuments(), revenueMap, totalRevenue[0]);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error loading sales data", Toast.LENGTH_SHORT).show();
                                            updateRevenueDisplay(mechanicsSnapshot.getDocuments(), revenueMap, totalRevenue[0]);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error loading service data", Toast.LENGTH_SHORT).show();
                                updateRevenueDisplay(mechanicsSnapshot.getDocuments(), revenueMap, 0.0);
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading mechanics data", Toast.LENGTH_SHORT).show());
    }

    private void updateRevenueDisplay(List<DocumentSnapshot> mechanics, Map<String, Double> revenueMap, double totalRevenue) {
        runOnUiThread(() -> {
            try {
                totalRevenueText.setText(String.format("Total Revenue: ₹%.2f", totalRevenue));
                
                // Display revenue for each mechanic
                int mechanicCount = 0;
                
                for (DocumentSnapshot mechanic : mechanics) {
                    String mechanicId = mechanic.getId();
                    String mechanicName = mechanic.getString("username");
                    Double revenue = revenueMap.get(mechanicId);
                    
                    if (mechanicCount == 0) {
                        mechanic1RevenueText.setText(String.format("%s Revenue: ₹%.2f", 
                            mechanicName != null ? mechanicName : "Mechanic 1", 
                            revenue != null ? revenue : 0.0));
                    } else if (mechanicCount == 1) {
                        mechanic2RevenueText.setText(String.format("%s Revenue: ₹%.2f", 
                            mechanicName != null ? mechanicName : "Mechanic 2", 
                            revenue != null ? revenue : 0.0));
                    }
                    mechanicCount++;
                }
                
                // Display manager revenue
                Double managerRevenue = revenueMap.get("manager");
                managerRevenueText.setText(String.format("Manager Revenue: ₹%.2f", 
                    managerRevenue != null ? managerRevenue : 0.0));
                
            } catch (Exception e) {
                Toast.makeText(OwnerDashboardActivity.this, 
                    "Error updating revenue display", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
} 