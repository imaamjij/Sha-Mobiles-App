package com.example.shamobiles.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.shamobiles.fragments.ServiceRecordsFragment;
import com.example.shamobiles.fragments.AccessorySalesFragment;

public class OwnerPagerAdapter extends FragmentStateAdapter {
    private static final int TOTAL_TABS = 4;

    public OwnerPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                ServiceRecordsFragment allServicesFragment = new ServiceRecordsFragment();
                allServicesFragment.setViewMode("all");
                return allServicesFragment;
            case 1:
                ServiceRecordsFragment elumalaiFragment = new ServiceRecordsFragment();
                elumalaiFragment.setMechanicRole("mechanic1");
                return elumalaiFragment;
            case 2:
                ServiceRecordsFragment nizarFragment = new ServiceRecordsFragment();
                nizarFragment.setMechanicRole("mechanic2");
                return nizarFragment;
            case 3:
                AccessorySalesFragment salesFragment = new AccessorySalesFragment();
                salesFragment.setManagerId(null);
                return salesFragment;
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return TOTAL_TABS;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "All Services";
            case 1:
                return "Elumalai Services";
            case 2:
                return "Nizar Services";
            case 3:
                return "Sales History";
            default:
                return "";
        }
    }
} 