package com.vacuno_app.menu;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.vacuno_app.R;
import com.vacuno_app.databinding.FragmentMenuBinding;



public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMenuBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        ImageButton settingsBtn = binding.settingsButton;
        ImageButton usersBtn = binding.usersButton;
        ImageButton sheetsBtn = binding.sheetsButton;
        ImageButton alarmsBtn = binding.alarmsButton;
        ImageButton productionBtn = binding.milkButton;
        ImageButton reportsBtn = binding.reportsButton;

        NavController navController = Navigation.findNavController(view);

        settingsBtn.setOnClickListener(view1 -> navController.navigate(R.id.settingFragment));

        usersBtn.setOnClickListener(view1 -> navController.navigate(R.id.usersFragment));

        sheetsBtn.setOnClickListener(view1 -> navController.navigate(R.id.sheetFragment));

        alarmsBtn.setOnClickListener(view1 -> navController.navigate(R.id.alarmFragment));

        productionBtn.setOnClickListener(view1 -> navController.navigate(R.id.productionFragment));

        reportsBtn.setOnClickListener(view1 -> navController.navigate(R.id.reportFragment));

    }
}