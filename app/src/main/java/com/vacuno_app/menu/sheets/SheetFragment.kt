package com.vacuno_app.menu.sheets

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vacuno_app.R
import com.vacuno_app.domain.model.Race
import com.vacuno_app.domain.model.Sheet
import com.vacuno_app.menu.sheets.adapter.SheetAdapter
import com.vacuno_app.databinding.FragmentSheetBinding

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.GlobalScope;
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SheetFragment():Fragment() {

    private val viewModel: SheetViewModel by viewModels()


    private lateinit var binding: FragmentSheetBinding
    private lateinit var aBuilder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog
    //private Spinner sp;

    //SearchView search;

    private lateinit var nameET: EditText
    private lateinit var codeET: EditText
    private lateinit var fatherET: EditText
    private lateinit var matherET: EditText
    private lateinit var weightET: EditText
    private lateinit var ageET: EditText
    private lateinit var raceS: Spinner
    private lateinit var statusCB: CheckBox

    private lateinit var raceT: String

    private lateinit var db: FirebaseDatabase

    private lateinit var recyclerView: RecyclerView
    private lateinit var sheetAdapter: SheetAdapter
    private lateinit var listSheets: ArrayList<Sheet>
    private lateinit var listRaces: MutableList<Race>
    private lateinit var optionsRaces: MutableList<String>
    private lateinit var idCount: String

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSheetBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.addSheetButton.setOnClickListener {
            createAddDialog()
        }


        recyclerView = binding.sheetsRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =  LinearLayoutManager(context)
        listSheets = ArrayList()
        sheetAdapter = SheetAdapter(context, listSheets)
        recyclerView.adapter = sheetAdapter

        viewModel.getSheets()

        viewModel.sheets.observe(viewLifecycleOwner){
            //print45(it)
            listSheets.clear()
            listSheets.addAll(it)
            sheetAdapter.notifyDataSetChanged()
        }


        listRaces = mutableListOf()
        optionsRaces = mutableListOf()

        db = FirebaseDatabase.getInstance()

        db.getReference("Races").addValueEventListener(object:  ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listRaces.clear()
                for (ds in snapshot.children) {
                    val r: Race? = ds.getValue(Race::class.java)
                    //if(s.getStatus().equals("*") || s.getStatus().equals("Inactivo"))  continue;//ignore element whit status remove
                    if(r != null){
                        r.setId(ds.key)
                        listRaces.add(r)
                        //Log.e("fwe", r.getName());
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PRUEBA", ": "+error)
            }
        });

        return root;
    }

    private fun createAddDialog(){
        aBuilder = AlertDialog.Builder(context)
        val addSheetPopup: View = layoutInflater.inflate(R.layout.add_sheet, null)
        aBuilder.setView(addSheetPopup)
        aBuilder.setCancelable(false)
        dialog = aBuilder.create()
        dialog.show()

        addSheetPopup.findViewById<Button>(R.id.addSheetSaveButton).setOnClickListener {
            nameET = addSheetPopup.findViewById(R.id.addSheetNameEditText);
            codeET = addSheetPopup.findViewById(R.id.addSheetCodeEditText);
            fatherET = addSheetPopup.findViewById(R.id.addSheetFatherEditText);
            matherET = addSheetPopup.findViewById(R.id.addSheetMatherEditText);
            weightET = addSheetPopup.findViewById(R.id.addSheetWeightEditText);
            ageET = addSheetPopup.findViewById(R.id.addSheetAgeEditText);
            statusCB = addSheetPopup.findViewById(R.id.addSheetStatusCheckBox);

            val nS = Sheet()
            nS.setName(nameET.text.toString())
            nS.setCode(codeET.text.toString())
            nS.setFather(fatherET.text.toString())
            nS.setMather(matherET.text.toString())
            nS.setWeight(weightET.text.toString())
            nS.setAge(ageET.text.toString())
            nS.setRace(raceT)

            if(statusCB.isChecked){
                nS.setStatus("A")
            }else{
                nS.setStatus("I")
            }

            listSheets.add(nS)
            //Firebase save here
            add(nS)
            //end save
            //dialog.dismiss();
        }

        addSheetPopup.findViewById<Button>(R.id.addSheetCancelButton).setOnClickListener {
            dialog.dismiss()
        }

        raceS = addSheetPopup.findViewById(R.id.addSheetRaceSpinner)

        listRaces.map { race ->
            optionsRaces.add(race.name)
        }

        val optionsServiceAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, optionsRaces)
        raceS.adapter = optionsServiceAdapter
        raceS.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                raceT = listRaces[i].getName()
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

    }


    private fun add(s: Sheet){

        //lifecycleScope.launch {
        val addLiveData = viewModel.addSheet(s)
        addLiveData.observe(viewLifecycleOwner) { r ->
            if(r != null) {
                if(r){
                    Toast.makeText(requireContext(), "Sheet added", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }else{
                    Toast.makeText(requireContext(), "Error while to adding", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //}

        //s.setId("S"+generateId());
        //db.getReference("servicio").push().setValue(s.toMap());
        //db.getReference("sheets").child(s.getId()).setValue(s.toMap());
        /*viewModel.addSheet(s, new Continuation<Boolean>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
            @Override
            public void resumeWith(@NotNull Object o) {
                System.out.println("Result of decode is " + o);
            }
        });*/
    }


    /*
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
    }*/
}