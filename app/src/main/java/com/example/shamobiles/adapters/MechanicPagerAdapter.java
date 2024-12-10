package com.example.shamobiles.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.shamobiles.fragments.NewServiceFragment;
import com.example.shamobiles.fragments.ServiceRecordsFragment;

public class MechanicPagerAdapter extends FragmentStateAdapter {
    private final String mechanicId;

    public MechanicPagerAdapter(@NonNull FragmentActivity fragmentActivity, String mechanicId) {
        super(fragmentActivity);
        this.mechanicId = mechanicId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            NewServiceFragment newServiceFragment = new NewServiceFragment();
            newServiceFragment.setMechanicId(mechanicId);
            return newServiceFragment;
        } else {
            ServiceRecordsFragment fragment = new ServiceRecordsFragment();
            fragment.setMechanicId(mechanicId);
            return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "New Service";
            case 1:
                return "Service History";
            default:
                return "";
        }
    }
} 