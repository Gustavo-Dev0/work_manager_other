package com.vacuno_app.menu.production

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vacuno_app.R
import com.vacuno_app.databinding.FragmentProductionBinding
import com.vacuno_app.domain.model.Production
import com.vacuno_app.domain.model.Race
import com.vacuno_app.domain.model.Sheet
import com.vacuno_app.menu.production.adapter.ProductionAdapter
import com.vacuno_app.menu.production.utils.DatePickerFragment
import com.vacuno_app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class ProductionFragment : Fragment() {


    private val viewModel: ProductionViewModel by viewModels()

    private lateinit var binding: FragmentProductionBinding

    private lateinit var aBuilder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog
    //private Spinner sp;

    //SearchView search;

    private lateinit var dateET: EditText
    private lateinit var turnMRB: RadioButton
    private lateinit var turnTRB: RadioButton

    private lateinit var userTV: TextView
    private lateinit var totalET: EditText

    private lateinit var cowS: Spinner

    private lateinit var cowSelected: Sheet

    private lateinit var db: FirebaseDatabase

    private lateinit var recyclerView: RecyclerView
    private lateinit var productionAdapter: ProductionAdapter
    private lateinit var listProductions: ArrayList<Production>

    private lateinit var listCows: MutableList<Sheet>
    private lateinit var optionsCowsNames: MutableList<String>

    private lateinit var currentUserId: String

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductionBinding.inflate(layoutInflater)
        val root = binding.root

        binding.addProductionButton.setOnClickListener {
            createAddDialog()
        }


        recyclerView = binding.productionRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =  LinearLayoutManager(context)
        listProductions = ArrayList()
        productionAdapter = ProductionAdapter(listProductions)
        recyclerView.adapter = productionAdapter

        viewModel.getProductions()

        viewModel.productions.observe(viewLifecycleOwner){
            listProductions.clear()
            listProductions.addAll(it)
            productionAdapter.notifyDataSetChanged()
        }


        listCows = mutableListOf()
        optionsCowsNames = mutableListOf()

        db = FirebaseDatabase.getInstance()

        db.getReference("sheets").child(Constants.APP_FARM_ID).addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listCows.clear()
                for (ds in snapshot.children) {
                    val c: Sheet? = ds.getValue(Sheet::class.java)
                    //if(s.getStatus().equals("*") || s.getStatus().equals("Inactivo"))  continue;//ignore element whit status remove
                    if(c != null){
                        c.setId(ds.key)
                        listCows.add(c)
                        //Log.e("fwe", r.getName());
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PRUEBA", ": $error")
            }
        })


        currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!


        return root
    }

    private fun createAddDialog(){
        aBuilder = AlertDialog.Builder(context)
        val addSheetPopup: View = layoutInflater.inflate(R.layout.add_production, null)
        aBuilder.setView(addSheetPopup)
        aBuilder.setCancelable(false)
        dialog = aBuilder.create()
        dialog.show()

        addSheetPopup.findViewById<EditText>(R.id.addProductionDateEditText).setOnClickListener {
            showDatePickerDialog(addSheetPopup)
        }


        addSheetPopup.findViewById<Button>(R.id.addProductionSaveButton).setOnClickListener {
            turnMRB = addSheetPopup.findViewById(R.id.addMProductionRadioButton)
            turnTRB = addSheetPopup.findViewById(R.id.addTProductionRadioButton)
            dateET = addSheetPopup.findViewById(R.id.addProductionDateEditText)
            userTV = addSheetPopup.findViewById(R.id.addProductionUserTextView)
            totalET = addSheetPopup.findViewById(R.id.addProductionTotalEditText)


            val nP = Production()
            nP.total = totalET.text.toString().toDouble()
            nP.dateCreated = dateET.text.toString()
            nP.userId = currentUserId
            nP.sheetId = cowSelected.id
            nP.sheetName = cowSelected.name

            if(turnMRB.isChecked){
                nP.turn = "Mañana"
            }else{
                nP.turn = "Tarde"
            }

            //listProductions.add(nP)
            //Firebase save here
            add(nP)
            //end save
            //dialog.dismiss() //BORRAR
        }

        addSheetPopup.findViewById<Button>(R.id.addProductionCancelButton).setOnClickListener {
            dialog.dismiss()
        }

        cowS = addSheetPopup.findViewById(R.id.addNameCowProductionSpinner)
        optionsCowsNames.clear()
        listCows.map { cow ->
            optionsCowsNames.add(cow.name)
        }

        val optionsServiceAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, optionsCowsNames)
        cowS.adapter = optionsServiceAdapter
        cowS.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                cowSelected = listCows[i]
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

    }

    private fun showDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment.newInstance { _, year, month, day ->
            val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
            //dateET.setText(selectedDate)
            v.findViewById<EditText>(R.id.addProductionDateEditText).setText(selectedDate)
        }

        newFragment.show(parentFragmentManager, "datePicker")
    }


    private fun add(p: Production){

        val addLiveData = viewModel.addProduction(p)
        addLiveData.observe(viewLifecycleOwner) { r ->
            if(r != null) {
                if(r){
                    Toast.makeText(requireContext(), "Production added", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }else{
                    Toast.makeText(requireContext(), "Error while to adding", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}