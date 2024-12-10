package com.example.shamobiles.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shamobiles.R;
import com.example.shamobiles.models.SaleWithManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AccessorySaleAdapter extends RecyclerView.Adapter<AccessorySaleAdapter.SaleViewHolder> {
    private List<SaleWithManager> sales = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public void updateSales(List<SaleWithManager> newSales) {
        this.sales = newSales;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sale, parent, false);
        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
        SaleWithManager sale = sales.get(position);
        holder.managerText.setText("Manager: " + sale.getManagerName());
        holder.accessoryNameText.setText("Item: " + sale.getItem());
        holder.quantityText.setText("Quantity: " + sale.getQuantity());
        holder.priceText.setText("Price: RM" + sale.getPrice());
        holder.dateText.setText("Date: " + dateFormat.format(new Date(sale.getTimestamp())));
        
        // Hide delete button
        if (holder.deleteButton != null) {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    static class SaleViewHolder extends RecyclerView.ViewHolder {
        TextView managerText, accessoryNameText, quantityText, priceText, dateText;
        View deleteButton;

        SaleViewHolder(View itemView) {
            super(itemView);
            managerText = itemView.findViewById(R.id.managerText);
            accessoryNameText = itemView.findViewById(R.id.accessoryNameText);
            quantityText = itemView.findViewById(R.id.quantityText);
            priceText = itemView.findViewById(R.id.priceText);
            dateText = itemView.findViewById(R.id.dateText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 