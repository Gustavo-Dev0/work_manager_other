package com.vacuno_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.FirebaseDatabase;
import com.vacuno_app.menu.MenuFragment;
import com.vacuno_app.menu.sheets.SheetFragment;
import com.vacuno_app.menu.users.UsersFragment;
import com.vacuno_app.utils.Constants;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants.APP_FARM_ID = getIntent().getStringExtra("app_farm_id");

    }
}