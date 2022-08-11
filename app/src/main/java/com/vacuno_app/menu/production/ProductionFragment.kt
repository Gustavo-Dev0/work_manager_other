package com.vacuno_app.menu.production

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
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
    private lateinit var currentUserEmail: String

    private lateinit var myToast: Toast

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        binding = FragmentProductionBinding.inflate(layoutInflater)
        val root = binding.root

        myToast = Toast.makeText(context, null, Toast.LENGTH_SHORT)

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
                //Log.e("PRUEBA", ": $error")
            }
        })


        currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email!!


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

        addSheetPopup.findViewById<TextView>(R.id.addProductionUserTextView).text = currentUserEmail

        addSheetPopup.findViewById<Button>(R.id.addProductionSaveButton).setOnClickListener {


            if(viewModel.productions.value == null){
                toast(getString(R.string.loading))
                return@setOnClickListener
            }

            if(viewModel.productions.value?.size!! >= Constants.MAX_PRODUCTIONS){
                toast(getString(R.string.limit_productions))
                return@setOnClickListener
            }

            if(listCows.size == 0){
                toast(getString(R.string.first_sheet))
                return@setOnClickListener
            }

            turnMRB = addSheetPopup.findViewById(R.id.addMProductionRadioButton)
            turnTRB = addSheetPopup.findViewById(R.id.addTProductionRadioButton)
            dateET = addSheetPopup.findViewById(R.id.addProductionDateEditText)
            userTV = addSheetPopup.findViewById(R.id.addProductionUserTextView)
            totalET = addSheetPopup.findViewById(R.id.addProductionTotalEditText)

            var isValid = true

            totalET.let {
                val text = it.text.toString()
                if(text.isBlank()){
                    isValid = false
                    totalET.error = getString(R.string.required)
                } else if (text.length > 6) {
                    isValid = false
                    totalET.error = getString(R.string.invalid_total)
                } else if (text.toDouble() > 100){
                    isValid = false
                    totalET.error = getString(R.string.max_100_l)
                }
            }

            dateET.let {
                val text = it.text.toString()
                if(text.isBlank()){
                    isValid = false
                    dateET.error = getString(R.string.required)
                }
            }

            if(!isValid) return@setOnClickListener

            val nP = Production()
            nP.total = totalET.text.toString().toDouble()
            nP.dateCreated = dateET.text.toString()
            nP.userId = currentUserId
            nP.sheetId = cowSelected.id
            nP.sheetName = cowSelected.name

            if(turnMRB.isChecked){
                nP.turn = "M"
            }else{
                nP.turn = "T"
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
                    Toast.makeText(requireContext(), getString(R.string.success), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }else{
                    Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createEditDialog(p: Production){
        aBuilder = AlertDialog.Builder(context)
        val addSheetPopup: View = layoutInflater.inflate(R.layout.add_production, null)
        aBuilder.setView(addSheetPopup)
        aBuilder.setCancelable(false)
        dialog = aBuilder.create()
        dialog.show()

        addSheetPopup.findViewById<EditText>(R.id.addProductionDateEditText).setOnClickListener {
            showDatePickerDialog(addSheetPopup)
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
        var sCow = 0
        for(i in 0 until listCows.size) {
            if(listCows[i].id == p.sheetId){
                sCow = i
                break
            }
        }
        cowS.setSelection(sCow)




        turnMRB = addSheetPopup.findViewById(R.id.addMProductionRadioButton)
        turnTRB = addSheetPopup.findViewById(R.id.addTProductionRadioButton)
        dateET = addSheetPopup.findViewById(R.id.addProductionDateEditText)
        userTV = addSheetPopup.findViewById(R.id.addProductionUserTextView)
        totalET = addSheetPopup.findViewById(R.id.addProductionTotalEditText)

        dateET.setText(p.dateCreated)
        if(p.turn == "T")
            turnTRB.isChecked = true
        else
            turnMRB.isChecked = true


        totalET.setText(p.total.toString())


        addSheetPopup.findViewById<TextView>(R.id.addProductionUserLabel).visibility = View.INVISIBLE
        userTV.visibility = View.INVISIBLE

        addSheetPopup.findViewById<Button>(R.id.addProductionSaveButton).setOnClickListener {


            if(viewModel.productions.value == null){
                toast(getString(R.string.loading))
                return@setOnClickListener
            }

            if(viewModel.productions.value?.size!! >= Constants.MAX_PRODUCTIONS){
                toast(getString(R.string.limit_productions))
                //return@setOnClickListener
            }else if(listCows.size == 0){
                toast(getString(R.string.first_sheet))
                return@setOnClickListener
            }

            var isValid = true

            totalET.let {
                val text = it.text.toString()
                if(text.isBlank()){
                    isValid = false
                    totalET.error = getString(R.string.required)
                } else if (text.length > 6) {
                    isValid = false
                    totalET.error = getString(R.string.invalid_total)
                } else if (text.toDouble() > 100){
                    isValid = false
                    totalET.error = getString(R.string.max_100_l)
                }
            }

            dateET.let {
                val text = it.text.toString()
                if(text.isBlank()){
                    isValid = false
                    dateET.error = "Required"
                }
            }

            if(!isValid) return@setOnClickListener

            p.total = totalET.text.toString().toDouble()
            p.dateCreated = dateET.text.toString()
            p.sheetId = cowSelected.id
            p.sheetName = cowSelected.name

            if(turnMRB.isChecked){
                p.turn = "M"
            }else{
                p.turn = "T"
            }

            edit(p)
        }

        addSheetPopup.findViewById<Button>(R.id.addProductionCancelButton).setOnClickListener {
            dialog.dismiss()
        }



    }

    private fun edit(p: Production) {
        viewModel.editProduction(p)
        dialog.dismiss()
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {

        val p: Production = viewModel.productions.value?.get(item.groupId)!!

        when(item.itemId) {
            101 -> {
                createEditDialog(p)
            }

            102 -> {
                val alertDialogDelete: AlertDialog? = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setPositiveButton(getString(R.string.yes)) { dialog, id ->
                            viewModel.productionDeleteInFarm(p.id!!)
                            dialog.dismiss()
                        }

                        setNegativeButton(getString(R.string.no)) { dialog, id ->
                            dialog.dismiss()
                        }

                        setTitle(getString(R.string.delete_production))
                    }
                    builder.create()
                }
                alertDialogDelete?.show()
            }

        }
        //Toast.makeText(context, uF.status, Toast.LENGTH_SHORT).show()

        return  super.onContextItemSelected(item)
    }


    private fun toast(message: String) {
        myToast.cancel()
        myToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        myToast.show()
    }

}