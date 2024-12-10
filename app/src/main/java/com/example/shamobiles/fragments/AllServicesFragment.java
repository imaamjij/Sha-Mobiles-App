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
import com.example.shamobiles.adapters.ServiceAdapter;
import com.example.shamobiles.models.Service;
import com.example.shamobiles.models.ServiceWithMechanic;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AllServicesFragment extends Fragment {
    private RecyclerView servicesRecyclerView;
    private ServiceAdapter serviceAdapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_services, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        db = FirebaseFirestore.getInstance();
        servicesRecyclerView = view.findViewById(R.id.servicesRecyclerView);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        serviceAdapter = new ServiceAdapter();
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        servicesRecyclerView.setAdapter(serviceAdapter);
        loadServices();
    }

    private void loadServices() {
        db.collection("services")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ServiceWithMechanic> servicesWithMechanics = new ArrayList<>();
                    AtomicInteger counter = new AtomicInteger(queryDocumentSnapshots.size());

                    if (queryDocumentSnapshots.isEmpty()) {
                        serviceAdapter.updateServices(servicesWithMechanics);
                        return;
                    }

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ServiceWithMechanic serviceWithMechanic = doc.toObject(ServiceWithMechanic.class);
                        if (serviceWithMechanic == null) continue;

                        // Get mechanic details
                        db.collection("users")
                                .document(serviceWithMechanic.getMechanicId())
                                .get()
                                .addOnSuccessListener(mechanicDoc -> {
                                    String role = mechanicDoc.getString("role");
                                    // Set mechanic name based on role
                                    switch (role) {
                                        case "mechanic1":
                                            serviceWithMechanic.setMechanicName("Elumalai");
                                            break;
                                        case "mechanic2":
                                            serviceWithMechanic.setMechanicName("Nizar");
                                            break;
                                        default:
                                            serviceWithMechanic.setMechanicName("Unknown Mechanic");
                                    }
                                    servicesWithMechanics.add(serviceWithMechanic);
                                    
                                    if (counter.decrementAndGet() == 0) {
                                        servicesWithMechanics.sort((s1, s2) -> 
                                            Long.compare(s2.getTimestamp(), s1.getTimestamp()));
                                        serviceAdapter.updateServices(servicesWithMechanics);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    serviceWithMechanic.setMechanicName("Unknown Mechanic");
                                    servicesWithMechanics.add(serviceWithMechanic);
                                    
                                    if (counter.decrementAndGet() == 0) {
                                        servicesWithMechanics.sort((s1, s2) -> 
                                            Long.compare(s2.getTimestamp(), s1.getTimestamp()));
                                        serviceAdapter.updateServices(servicesWithMechanics);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> 
                    Toast.makeText(getContext(), "Error loading services", Toast.LENGTH_SHORT).show()
                );
    }
} 