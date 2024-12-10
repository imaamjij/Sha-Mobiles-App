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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceRecordsFragment extends Fragment {
    private RecyclerView servicesRecyclerView;
    private ServiceAdapter serviceAdapter;
    private FirebaseFirestore db;
    private String mechanicId;
    private String mechanicRole;
    private String viewMode;

    public void setMechanicId(String mechanicId) {
        this.mechanicId = mechanicId;
    }

    public void setMechanicRole(String role) {
        this.mechanicRole = role;
    }

    public void setViewMode(String mode) {
        this.viewMode = mode;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_records, container, false);
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
        if ("all".equals(viewMode)) {
            // Load all services without filtering
            db.collection("services")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Toast.makeText(getContext(), "Error loading services", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (value != null) {
                            List<Service> services = value.toObjects(Service.class);
                            loadMechanicNames(services);
                        }
                    });
        } else if (mechanicRole != null) {
            // Load services by mechanic role
            db.collection("users")
                    .whereEqualTo("role", mechanicRole)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            String mechanicId = querySnapshot.getDocuments().get(0).getId();
                            loadServicesForMechanic(mechanicId);
                        }
                    });
        } else if (mechanicId != null) {
            // Load services for specific mechanic
            loadServicesForMechanic(mechanicId);
        }
    }

    private void loadServicesForMechanic(String mechanicId) {
        db.collection("services")
                .whereEqualTo("mechanicId", mechanicId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error loading services", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        List<Service> services = value.toObjects(Service.class);
                        loadMechanicNames(services);
                    }
                });
    }

    private void loadMechanicNames(List<Service> services) {
        List<ServiceWithMechanic> servicesWithMechanics = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(services.size());

        for (Service service : services) {
            ServiceWithMechanic serviceWithMechanic = new ServiceWithMechanic();
            // Copy all fields from Service to ServiceWithMechanic
            serviceWithMechanic.setMechanicId(service.getMechanicId());
            serviceWithMechanic.setCustomerName(service.getCustomerName());
            serviceWithMechanic.setMobileModel(service.getMobileModel());
            serviceWithMechanic.setFault(service.getFault());
            serviceWithMechanic.setPrice(service.getPrice());
            serviceWithMechanic.setTimestamp(service.getTimestamp());

            // Get mechanic details
            db.collection("users")
                    .document(service.getMechanicId())
                    .get()
                    .addOnSuccessListener(document -> {
                        String role = document.getString("role");
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
    }
} 