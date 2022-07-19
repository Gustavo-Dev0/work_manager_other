package com.vacuno_app.menu.sheets;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vacuno_app.R;
import com.vacuno_app.utils.BackupList;
import com.vacuno_app.domain.model.Race;
import com.vacuno_app.domain.model.Sheet;
import com.vacuno_app.menu.sheets.adapter.SheetAdapter;
import com.vacuno_app.databinding.FragmentSheetBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SheetFragment extends Fragment {

    private SheetViewModel sheetViewModel;
    private FragmentSheetBinding binding;
    private AlertDialog.Builder aBuilder;
    private AlertDialog dialog;
    //private Spinner sp;

    //SearchView search;

    EditText nameET, codeET, fatherET, matherET, weightET, ageET;
    Spinner raceS;
    CheckBox statusCB;

    String rolT;

    FirebaseDatabase db;

    RecyclerView recyclerView;
    SheetAdapter sheetAdapter;
    ArrayList<Sheet> listSheets;
    ArrayList<Race> listRaces;
    String[] optionsRaces;
    String idCount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sheetViewModel = new ViewModelProvider(this).get(SheetViewModel.class);
        binding = FragmentSheetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseDatabase.getInstance();

        binding.addSheetButton.setOnClickListener(view -> {
            createAddDialog();
        });

        /*String[] optionsOrder = {"Nombre A-Z","Nombre Z-A", "Fecha reciente", "Fecha antiguo", "Estado"};
        ArrayAdapter<String> optionsAdapter = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, optionsOrder);
        sp.setAdapter(optionsAdapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                order(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });*/


        recyclerView = binding.sheetsRecyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listSheets = new ArrayList<>();
        sheetAdapter = new SheetAdapter(getContext(), listSheets);
        recyclerView.setAdapter(sheetAdapter);

        listRaces = new ArrayList<>();

        //db.getReference("mess").setValue("HOLA B");
        //Firebase code here
        //Log.e("PRUEBA", ": "+db.);
        db.getReference("sheets").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listSheets.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Sheet s = ds.getValue(Sheet.class);
                    //if(s.getStatus().equals("*"))  continue;//ignore element whit status remove
                    s.setId(ds.getKey());
                    listSheets.add(s);
                }
                BackupList.listSheetsBackup.clear();
                BackupList.listSheetsBackup.addAll(listSheets);
                //order(sp.getSelectedItemPosition());
                order(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });

        //id
        db.getReference("idCont").child("sheets").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idCount = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error+"");
            }
        });

        db.getReference("Races").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listRaces.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Race s = ds.getValue(Race.class);
                    //if(s.getStatus().equals("*") || s.getStatus().equals("Inactivo"))  continue;//ignore element whit status remove
                    s.setId(ds.getKey());
                    listRaces.add(s);
                    Log.e("fwe", s.getName());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });

        return root;
    }

    public void createAddDialog(){
        aBuilder = new AlertDialog.Builder(getContext());
        View addSheetPopup = getLayoutInflater().inflate(R.layout.add_sheet, null);
        aBuilder.setView(addSheetPopup);
        aBuilder.setCancelable(false);
        dialog = aBuilder.create();
        dialog.show();
        addSheetPopup.findViewById(R.id.addSheetSaveButton).setOnClickListener(view -> {
            nameET = addSheetPopup.findViewById(R.id.addSheetNameEditText);
            codeET = addSheetPopup.findViewById(R.id.addSheetCodeEditText);
            fatherET = addSheetPopup.findViewById(R.id.addSheetFatherEditText);
            matherET = addSheetPopup.findViewById(R.id.addSheetMatherEditText);
            weightET = addSheetPopup.findViewById(R.id.addSheetWeightEditText);
            ageET = addSheetPopup.findViewById(R.id.addSheetAgeEditText);
            statusCB = addSheetPopup.findViewById(R.id.addSheetStatusCheckBox);

            Sheet nS = new Sheet();
            nS.setName(nameET.getText().toString());
            nS.setCode(codeET.getText().toString());
            nS.setFather(fatherET.getText().toString());
            nS.setMather(matherET.getText().toString());
            nS.setWeight(weightET.getText().toString());
            nS.setAge(ageET.getText().toString());

            if(statusCB.isChecked()){
                nS.setStatus("Activo");
            }else{
                nS.setStatus("Inactivo");
            }
            listSheets.add(nS);
            //Firebase save here
            add(nS);
            //end save
            dialog.dismiss();
        });
        addSheetPopup.findViewById(R.id.addSheetCancelButton).setOnClickListener(view -> {
            dialog.dismiss();
        });


        raceS = addSheetPopup.findViewById(R.id.addSheetRaceSpinner);



        optionsRaces = new String[listRaces.size()];
        for (int i = 0;i<listRaces.size();++i){
            optionsRaces[i] = listRaces.get(i).getName();
        }
        ArrayAdapter<String> optionsServiceAdapter = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, optionsRaces);
        raceS.setAdapter(optionsServiceAdapter);
        raceS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                rolT = listRaces.get(i).getId();
                //Log.e("RESULTADO", serviceT+" --> "+listServices.get(i).getName());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

    }

    public Integer generateId(){

        int idInt = Integer.parseInt(idCount);
        idInt++;
        Map<String, Object> idMap = new HashMap<>();
        idMap.put("sheets", idInt+"");
        db.getReference("idCont").updateChildren(idMap);
        return idInt;
    }

    public void add(Sheet s){
        s.setId("S"+generateId());
        //db.getReference("servicio").push().setValue(s.toMap());
        db.getReference("sheets").child(s.getId()).setValue(s.toMap());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void order(int i) {
        switch (i) {
            case 0:
                Collections.sort(listSheets, Sheet.serviceNameAZComparator);
                break;
            case 1:
                Collections.sort(listSheets, Sheet.serviceNameZAComparator);
                break;
            case 2:
                Collections.sort(listSheets, Sheet.serviceStatusComparator);
                break;

        }
        sheetAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        super.onContextItemSelected(item);
        Sheet s = listSheets.get(item.getGroupId());
        switch (item.getItemId()){
            case 100:
                //createViewDialog(s);
                break;
            case 101:
                //createEditDialog(s);
                //formAdapter.notifyItemChanged(item.getGroupId());
                break;
            case 102:
                if(s.getStatus().equals("Activo")) s.setStatus("Inactivo");
                else    s.setStatus("Activo");
                //update(s);
                //formAdapter.notifyItemChanged(item.getGroupId());
                break;
            case 103:
                s.setStatus("*");
                //listForm.remove(s);
                //delete(s);
                //formAdapter.notifyItemRemoved(item.getGroupId());
                break;
        }
        BackupList.listSheetsBackup.clear();
        BackupList.listSheetsBackup.addAll(listSheets);
        //order(sp.getSelectedItemPosition());
        return true;
    }
}