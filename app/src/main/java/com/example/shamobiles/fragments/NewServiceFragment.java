package com.example.shamobiles.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shamobiles.R;
import com.example.shamobiles.models.Service;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class NewServiceFragment extends Fragment {
    private EditText customerNameInput, mobileModelInput, faultInput, priceInput;
    private FirebaseFirestore db;
    private String mechanicId;

    public void setMechanicId(String mechanicId) {
        this.mechanicId = mechanicId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        db = FirebaseFirestore.getInstance();
        
        initializeViews(view);
        setupButtons(view);
    }

    private void initializeViews(View view) {
        customerNameInput = view.findViewById(R.id.customerNameInput);
        mobileModelInput = view.findViewById(R.id.mobileModelInput);
        faultInput = view.findViewById(R.id.faultInput);
        priceInput = view.findViewById(R.id.priceInput);
    }

    private void setupButtons(View view) {
        Button submitButton = view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> submitServiceRecord());
    }

    private void submitServiceRecord() {
        String customerName = customerNameInput.getText().toString().trim();
        String mobileModel = mobileModelInput.getText().toString().trim();
        String fault = faultInput.getText().toString().trim();
        String priceStr = priceInput.getText().toString().trim();

        if (customerName.isEmpty() || mobileModel.isEmpty() || fault.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);

            Service service = new Service();
            service.setMechanicId(mechanicId);
            service.setCustomerName(customerName);
            service.setMobileModel(mobileModel);
            service.setFault(fault);
            service.setPrice(price);
            service.setTimestamp(new Date().getTime());

            db.collection("services")
                    .add(service)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Service record added successfully", Toast.LENGTH_SHORT).show();
                        clearInputs();
                    })
                    .addOnFailureListener(e -> 
                        Toast.makeText(getContext(), "Error adding service record", Toast.LENGTH_SHORT).show()
                    );
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid price format", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        customerNameInput.setText("");
        mobileModelInput.setText("");
        faultInput.setText("");
        priceInput.setText("");
    }
} 