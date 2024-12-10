package com.example.shamobiles.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.shamobiles.fragments.ServiceRecordsFragment;
import com.example.shamobiles.fragments.AccessorySalesFragment;
import com.example.shamobiles.fragments.NewAccessorySaleFragment;
import com.example.shamobiles.fragments.SectionHeaderFragment;

public class ManagerServicesPagerAdapter extends FragmentStateAdapter {
    private final String managerId;
    private static final int TOTAL_TABS = 4; // Reduced number of tabs

    public ManagerServicesPagerAdapter(@NonNull FragmentActivity fragmentActivity, String managerId) {
        super(fragmentActivity);
        this.managerId = managerId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                ServiceRecordsFragment fragment1 = new ServiceRecordsFragment();
                fragment1.setMechanicRole("mechanic1");
                return fragment1;
            case 1:
                ServiceRecordsFragment fragment2 = new ServiceRecordsFragment();
                fragment2.setMechanicRole("mechanic2");
                return fragment2;
            case 2:
                NewAccessorySaleFragment newSaleFragment = new NewAccessorySaleFragment();
                newSaleFragment.setManagerId(managerId);
                return newSaleFragment;
            case 3:
                AccessorySalesFragment salesFragment = new AccessorySalesFragment();
                salesFragment.setManagerId(managerId);
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
                return "Elumalai";
            case 1:
                return "Nizar";
            case 2:
                return "New Sale";
            case 3:
                return "Sales History";
            default:
                return "";
        }
    }
} 