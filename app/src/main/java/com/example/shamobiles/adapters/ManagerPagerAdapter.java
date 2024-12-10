package com.example.shamobiles.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.shamobiles.fragments.AccessorySalesFragment;
import com.example.shamobiles.fragments.ServiceRecordsFragment;

public class ManagerPagerAdapter extends FragmentStateAdapter {

    public ManagerPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new AccessorySalesFragment() : new ServiceRecordsFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
} 