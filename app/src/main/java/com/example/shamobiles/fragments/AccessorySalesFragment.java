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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

public class AccessorySalesFragment extends Fragment {
    private RecyclerView recyclerView;
    private AccessorySaleAdapter saleAdapter;
    private FirebaseFirestore db;
    private String managerId;
    private boolean isOwnerView;

    public void setManagerId(String managerId) {
        this.managerId = managerId;
        this.isOwnerView = (managerId == null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accessory_sales, container, false);
        recyclerView = view.findViewById(R.id.salesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        saleAdapter = new AccessorySaleAdapter();
        recyclerView.setAdapter(saleAdapter);
        
        db = FirebaseFirestore.getInstance();
        loadSales();
        
        return view;
    }

    private void loadSales() {
        Query query = db.collection("accessory_sales");
        
        // If managerId is set (manager view), filter by managerId
        if (managerId != null) {
            query = query.whereEqualTo("managerId", managerId);
        }
        
        query.orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<SaleWithManager> sales = new ArrayList<>();
                
                if (queryDocumentSnapshots.isEmpty()) {
                    saleAdapter.updateSales(sales);
                    return;
                }

                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    SaleWithManager sale = doc.toObject(SaleWithManager.class);
                    if (sale != null) {
                        sale.setId(doc.getId());
                        sales.add(sale);
                        
                        // Get manager details
                        db.collection("users")
                            .document(sale.getManagerId())
                            .get()
                            .addOnSuccessListener(managerDoc -> {
                                String username = managerDoc.getString("username");
                                sale.setManagerName(username != null ? username : "Unknown Manager");
                                
                                // Update adapter when all sales are processed
                                if (sales.size() == queryDocumentSnapshots.size()) {
                                    saleAdapter.updateSales(sales);
                                }
                            })
                            .addOnFailureListener(e -> {
                                sale.setManagerName("Unknown Manager");
                                // Update adapter when all sales are processed
                                if (sales.size() == queryDocumentSnapshots.size()) {
                                    saleAdapter.updateSales(sales);
                                }
                            });
                    }
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error loading sales", Toast.LENGTH_SHORT).show();
                saleAdapter.updateSales(new ArrayList<>());
            });
    }
} 