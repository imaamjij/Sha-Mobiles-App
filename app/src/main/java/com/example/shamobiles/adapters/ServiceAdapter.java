package com.example.shamobiles.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shamobiles.R;
import com.example.shamobiles.models.ServiceWithMechanic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private List<ServiceWithMechanic> services = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceWithMechanic service = services.get(position);
        holder.customerNameText.setText("Customer: " + service.getCustomerName());
        holder.mobileModelText.setText("Model: " + service.getMobileModel());
        holder.faultText.setText("Fault: " + service.getFault());
        holder.priceText.setText(String.format("Price: ₹%.2f", service.getPrice()));
        holder.dateText.setText("Date: " + dateFormat.format(new Date(service.getTimestamp())));
        holder.mechanicText.setText("Mechanic: " + service.getMechanicName());
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public void updateServices(List<ServiceWithMechanic> newServices) {
        services = newServices;
        notifyDataSetChanged();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameText, mobileModelText, faultText, priceText, dateText, mechanicText;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameText = itemView.findViewById(R.id.customerNameText);
            mobileModelText = itemView.findViewById(R.id.mobileModelText);
            faultText = itemView.findViewById(R.id.faultText);
            priceText = itemView.findViewById(R.id.priceText);
            dateText = itemView.findViewById(R.id.dateText);
            mechanicText = itemView.findViewById(R.id.mechanicText);
        }
    }
} 