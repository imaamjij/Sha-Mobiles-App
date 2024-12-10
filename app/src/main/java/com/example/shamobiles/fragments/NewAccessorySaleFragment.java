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
import com.example.shamobiles.models.AccessorySale;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class NewAccessorySaleFragment extends Fragment {
    private EditText itemNameInput, quantityInput, priceInput;
    private FirebaseFirestore db;
    private String managerId;

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_accessory_sale, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        db = FirebaseFirestore.getInstance();
        initializeViews(view);
        setupButtons(view);
    }

    private void initializeViews(View view) {
        itemNameInput = view.findViewById(R.id.itemNameInput);
        quantityInput = view.findViewById(R.id.quantityInput);
        priceInput = view.findViewById(R.id.priceInput);
    }

    private void setupButtons(View view) {
        Button submitButton = view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> submitSale());
    }

    private void submitSale() {
        String itemName = itemNameInput.getText().toString().trim();
        String quantityStr = quantityInput.getText().toString().trim();
        String priceStr = priceInput.getText().toString().trim();

        if (itemName.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);

            AccessorySale sale = new AccessorySale();
            sale.setManagerId(managerId);
            sale.setItem(itemName);
            sale.setQuantity(quantity);
            sale.setPrice(price);
            sale.setTimestamp(new Date().getTime());

            db.collection("accessory_sales")
                    .add(sale)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Sale record added successfully", Toast.LENGTH_SHORT).show();
                        clearInputs();
                    })
                    .addOnFailureListener(e -> 
                        Toast.makeText(getContext(), "Error adding sale record", Toast.LENGTH_SHORT).show()
                    );
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid quantity or price format", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        itemNameInput.setText("");
        quantityInput.setText("");
        priceInput.setText("");
    }
} 