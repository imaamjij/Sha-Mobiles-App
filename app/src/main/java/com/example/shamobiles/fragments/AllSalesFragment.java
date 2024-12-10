package com.example.shamobiles.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shamobiles.R;
import com.example.shamobiles.adapters.AccessorySaleAdapter;
import com.example.shamobiles.models.AccessorySale;
import com.example.shamobiles.models.SaleWithManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AllSalesFragment extends Fragment {
    private RecyclerView salesRecyclerView;
    private AccessorySaleAdapter saleAdapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_sales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        db = FirebaseFirestore.getInstance();
        salesRecyclerView = view.findViewById(R.id.salesRecyclerView);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        saleAdapter = new AccessorySaleAdapter();
        salesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        salesRecyclerView.setAdapter(saleAdapter);
        loadSales();
    }

    private void loadSales() {
        db.collection("accessory_sales")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<SaleWithManager> salesWithManagers = new ArrayList<>();
                    AtomicInteger counter = new AtomicInteger(queryDocumentSnapshots.size());

                    if (queryDocumentSnapshots.isEmpty()) {
                        saleAdapter.updateSales(salesWithManagers);
                        return;
                    }

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        SaleWithManager saleWithManager = doc.toObject(SaleWithManager.class);
                        if (saleWithManager == null) continue;

                        // Get manager details
                        db.collection("users")
                                .document(saleWithManager.getManagerId())
                                .get()
                                .addOnSuccessListener(managerDoc -> {
                                    String role = managerDoc.getString("role");
                                    // Set manager name if role is manager
                                    if ("manager".equals(role)) {
                                        saleWithManager.setManagerName("Javeed");
                                    } else {
                                        saleWithManager.setManagerName("Unknown Manager");
                                    }
                                    salesWithManagers.add(saleWithManager);
                                    
                                    if (counter.decrementAndGet() == 0) {
                                        salesWithManagers.sort((s1, s2) -> 
                                            Long.compare(s2.getTimestamp(), s1.getTimestamp()));
                                        saleAdapter.updateSales(salesWithManagers);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    saleWithManager.setManagerName("Unknown Manager");
                                    salesWithManagers.add(saleWithManager);
                                    
                                    if (counter.decrementAndGet() == 0) {
                                        salesWithManagers.sort((s1, s2) -> 
                                            Long.compare(s2.getTimestamp(), s1.getTimestamp()));
                                        saleAdapter.updateSales(salesWithManagers);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> 
                    Toast.makeText(getContext(), "Error loading sales", Toast.LENGTH_SHORT).show()
                );
    }
} 