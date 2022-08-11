package com.vacuno_app.menu.sheets

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Bundle

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vacuno_app.R
import com.vacuno_app.databinding.AddSheetBinding
import com.vacuno_app.domain.model.Race
import com.vacuno_app.domain.model.Sheet
import com.vacuno_app.menu.sheets.adapter.SheetAdapter
import com.vacuno_app.databinding.FragmentSheetBinding
import com.vacuno_app.domain.model.Production
import com.vacuno_app.menu.production.utils.DatePickerFragment
import com.vacuno_app.utils.Constants

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
    private lateinit var fatherET: EditText
    private lateinit var matherET: EditText
    private lateinit var weightET: EditText
    private lateinit var ageET: EditText
    private lateinit var birthET: EditText
    private lateinit var sexMRB: RadioButton
    private lateinit var sexHRB: RadioButton
    private lateinit var raceS: Spinner

    private lateinit var raceT: String

    private lateinit var db: FirebaseDatabase

    private lateinit var recyclerView: RecyclerView
    private lateinit var sheetAdapter: SheetAdapter
    private lateinit var listSheets: ArrayList<Sheet>
    private lateinit var listRaces: MutableList<Race>
    private lateinit var optionsRaces: MutableList<String>

    private lateinit var myToast: Toast

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

        binding = FragmentSheetBinding.inflate(inflater, container, false)
        val root = binding.root


        myToast = Toast.makeText(context, null, Toast.LENGTH_SHORT)

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
                //Log.e("PRUEBA", ": "+error)
            }
        });

        return root;
    }

    private fun createAddDialog(){
        aBuilder = AlertDialog.Builder(context)
        val dialogBinding = AddSheetBinding.inflate(layoutInflater)
        val addSheetPopup: View = dialogBinding.root

        aBuilder.setView(addSheetPopup)
        aBuilder.setCancelable(false)
        dialog = aBuilder.create()
        dialog.show()


        raceS = dialogBinding.addSheetRaceSpinner

        optionsRaces.clear()
        listRaces.map { race ->
            optionsRaces.add(race.name)
        }

        val optionsRaceAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, optionsRaces)
        raceS.adapter = optionsRaceAdapter
        raceS.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                raceT = listRaces[i].getName()
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        nameET = dialogBinding.addSheetNameEditText
        fatherET = dialogBinding.addSheetFatherEditText
        matherET = dialogBinding.addSheetMatherEditText
        weightET = dialogBinding.addSheetWeightEditText
        ageET = dialogBinding.addSheetAgeEditText
        birthET = dialogBinding.addSheetDateBirthEditText
        sexMRB = dialogBinding.addMSheetRadioButton
        sexHRB = dialogBinding.addHSheetRadioButton

        dialogBinding.addSheetDateBirthEditText.setOnClickListener {
            showDatePickerDialog(addSheetPopup)
        }


        addSheetPopup.findViewById<Button>(R.id.addSheetSaveButton).setOnClickListener {

            if(viewModel.sheets.value == null){
                toast(getString(R.string.loading))
                return@setOnClickListener
            }

            if(viewModel.sheets.value?.size!! >= Constants.MAX_SHEETS){
                toast(getString(R.string.limit_sheets))
                return@setOnClickListener
            }

            var isValid = true

            nameET.let {
                val text = it.text.toString()
                if(text.isBlank()){
                    isValid = false
                    nameET.error = getString(R.string.required)
                } else if (text.length > 20) {
                    isValid = false
                    nameET.error = getString(R.string.max_20)
                }
            }

            birthET.let {
                val text = it.text.toString()
                if(text.isBlank()){
                    isValid = false
                    birthET.error = getString(R.string.required)
                }
            }

            fatherET.apply {
                val text = text.toString()
                if(text.isBlank()){
                    isValid = false
                    error = getString(R.string.required)
                } else if (text.length > 20) {
                    isValid = false
                    error = getString(R.string.max_20)
                }
            }

            matherET.apply {
                val text = text.toString()
                if(text.isBlank()){
                    isValid = false
                    error = getString(R.string.required)
                } else if (text.length > 20) {
                    isValid = false
                    error = getString(R.string.max_20)
                }
            }

            weightET.apply {
                val text = text.toString()
                if(text.isBlank()){
                    isValid = false
                    error = getString(R.string.required)
                } else if (text.toDouble() > 1500){
                    isValid = false
                    error = getString(R.string.max_1500_k)
                }
            }


            if(!isValid) return@setOnClickListener


            val nS = Sheet()
            nS.setName(nameET.text.toString())
            nS.setDateBirth(birthET.text.toString())
            nS.setFather(fatherET.text.toString())
            nS.setMather(matherET.text.toString())
            nS.setWeight(weightET.text.toString())
            nS.setAge(ageET.text.toString())
            nS.setRace(raceT)
            nS.setStatus("A")

            if(sexHRB.isChecked){
                nS.setSex("H")
            }else{
                nS.setSex("M")
            }

            add(nS)
        }

        addSheetPopup.findViewById<Button>(R.id.addSheetCancelButton).setOnClickListener {
            dialog.dismiss()
        }

    }


    private fun add(s: Sheet){

        val addLiveData = viewModel.addSheet(s)
        addLiveData.observe(viewLifecycleOwner) { r ->
            if(r != null) {
                if(r){
                    Toast.makeText(requireContext(), getString(R.string.success), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }else{
                    Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment.newInstance { _, year, month, day ->
            val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
            //dateET.setText(selectedDate)
            v.findViewById<EditText>(R.id.addSheetDateBirthEditText).setText(selectedDate)
        }

        newFragment.show(parentFragmentManager, "datePicker")
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {

        val s: Sheet = viewModel.sheets.value?.get(item.groupId)!!

        when (item.itemId) {
            101 -> {
                createEditDialog(s)
            }
        }

        return  super.onContextItemSelected(item)
    }

    private fun createEditDialog(s: Sheet) {
        aBuilder = AlertDialog.Builder(context)
        val dialogBinding = AddSheetBinding.inflate(layoutInflater)
        val addSheetPopup: View = dialogBinding.root

        aBuilder.setView(addSheetPopup)
        aBuilder.setCancelable(false)
        dialog = aBuilder.create()
        dialog.show()


        raceS = dialogBinding.addSheetRaceSpinner

        optionsRaces.clear()
        listRaces.map { race ->
            optionsRaces.add(race.name)
        }

        val optionsRaceAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, optionsRaces)
        raceS.adapter = optionsRaceAdapter
        raceS.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                raceT = listRaces[i].getName()
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        var sRace = 0
        for(i in 0 until listRaces.size) {
            if(listRaces[i].name == s.race){
                sRace = i
                break
            }
        }
        raceS.setSelection(sRace)

        nameET = dialogBinding.addSheetNameEditText
        fatherET = dialogBinding.addSheetFatherEditText
        matherET = dialogBinding.addSheetMatherEditText
        weightET = dialogBinding.addSheetWeightEditText
        ageET = dialogBinding.addSheetAgeEditText
        birthET = dialogBinding.addSheetDateBirthEditText
        sexMRB = dialogBinding.addMSheetRadioButton
        sexHRB = dialogBinding.addHSheetRadioButton


        nameET.setText(s.name)
        //raza
        birthET.setText(s.dateBirth)
        fatherET.setText(s.father)
        matherET.setText(s.mather)
        weightET.setText(s.weight)
        ageET.setText(s.age)

        if(s.sex == "H")
            sexHRB.isChecked = true
        else
            sexMRB.isChecked = true

        dialogBinding.addSheetDateBirthEditText.setOnClickListener {
            showDatePickerDialog(addSheetPopup)
        }


        addSheetPopup.findViewById<Button>(R.id.addSheetSaveButton).setOnClickListener {

            if(viewModel.sheets.value == null){
                toast(getString(R.string.loading))
                return@setOnClickListener
            }

            if(viewModel.sheets.value?.size!! >= Constants.MAX_SHEETS){
                toast(getString(R.string.limit_sheets))
                //return@setOnClickListener
            }

            var isValid = true

            nameET.let {
                val text = it.text.toString()
                if(text.isBlank()){
                    isValid = false
                    nameET.error = getString(R.string.required)
                } else if (text.length > 20) {
                    isValid = false
                    nameET.error = getString(R.string.max_20)
                }
            }

            birthET.let {
                val text = it.text.toString()
                if(text.isBlank()){
                    isValid = false
                    birthET.error = getString(R.string.required)
                }
            }

            fatherET.apply {
                val text = text.toString()
                if(text.isBlank()){
                    isValid = false
                    error = getString(R.string.required)
                } else if (text.length > 20) {
                    isValid = false
                    error = getString(R.string.max_20)
                }
            }

            matherET.apply {
                val text = text.toString()
                if(text.isBlank()){
                    isValid = false
                    error = getString(R.string.required)
                } else if (text.length > 20) {
                    isValid = false
                    error = getString(R.string.max_20)
                }
            }

            weightET.apply {
                val text = text.toString()
                if(text.isBlank()){
                    isValid = false
                    error = getString(R.string.required)
                } else if (text.toDouble() > 1500){
                    isValid = false
                    error = getString(R.string.max_1500_k)
                }
            }


            if(!isValid) return@setOnClickListener


            s.setName(nameET.text.toString())
            s.setDateBirth(birthET.text.toString())
            s.setFather(fatherET.text.toString())
            s.setMather(matherET.text.toString())
            s.setWeight(weightET.text.toString())
            s.setAge(ageET.text.toString())
            s.setRace(raceT)
            s.setStatus("A")

            if(sexHRB.isChecked){
                s.setSex("H")
            }else{
                s.setSex("M")
            }

            edit(s)
        }

        addSheetPopup.findViewById<Button>(R.id.addSheetCancelButton).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun edit(s: Sheet) {
        viewModel.updateSheet(s)
        dialog.dismiss()
    }

    private fun toast(message: String) {
        myToast.cancel()
        myToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        myToast.show()
    }
}