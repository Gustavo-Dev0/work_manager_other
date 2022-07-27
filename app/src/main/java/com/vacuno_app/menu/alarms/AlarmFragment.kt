package com.vacuno_app.menu.alarms

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vacuno_app.R
import com.vacuno_app.databinding.FragmentAlarmBinding
import com.vacuno_app.domain.model.Alarm
import com.vacuno_app.domain.model.Sheet
import com.vacuno_app.menu.production.utils.DatePickerFragment
import com.vacuno_app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class AlarmFragment : Fragment() {

    private val viewModel: AlarmViewModel by viewModels()

    private lateinit var binding: FragmentAlarmBinding

    private lateinit var calendarView: CalendarView

    private lateinit var aBuilder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog
    //private Spinner sp;

    //SearchView search;

    private lateinit var dateET: EditText

    private lateinit var userTV: TextView
    private lateinit var typeET: EditText

    private lateinit var cowS: Spinner

    private lateinit var cowSelected: Sheet

    private lateinit var db: FirebaseDatabase

    /*private lateinit var recyclerView: RecyclerView
    private lateinit var productionAdapter: ProductionAdapter
    private lateinit var listProductions: java.util.ArrayList<Production>*/

    private lateinit var listCows: MutableList<Sheet>
    private lateinit var optionsCowsNames: MutableList<String>

    private lateinit var currentUserId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAlarmBinding.inflate(layoutInflater)
        val root = binding.root
        calendarView = binding.alertCalendarView

        binding.addAlarmButton.setOnClickListener {
            createAddDialog()
        }


        viewModel.getAlarms()

        viewModel.alarms.observe(viewLifecycleOwner){ alarms ->
            /*listProductions.clear()
            listProductions.addAll(it)
            productionAdapter.notifyDataSetChanged()*/
            val events: MutableList<EventDay> = ArrayList()
            calendarView.setEvents(events)
            val calendar: Calendar = Calendar.getInstance()

            alarms.map { alarm ->
                events.clear()

                var date = alarm.date.toString()
                date = date.replace(" ", "")
                //Log.e("into ", date)

                val formatter: DateTimeFormatter =
                    DateTimeFormatter.ofPattern("d/M/y")

                val dateTime: LocalDate = LocalDate.parse(date, formatter)
                //Log.e("current ", "" +dateTime.year +" "+ dateTime.monthValue +" "+ dateTime.dayOfMonth)

                calendar.set(dateTime.year, dateTime.monthValue-1, dateTime.dayOfMonth)
                events.add(EventDay(calendar, R.drawable.logoicon))
            }
            calendarView.setEvents(events)

        }

        listCows = mutableListOf()
        optionsCowsNames = mutableListOf()

        db = FirebaseDatabase.getInstance()

        db.getReference("sheets").child(Constants.APP_FARM_ID).addValueEventListener(object:
            ValueEventListener {

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
        val addSheetPopup: View = layoutInflater.inflate(R.layout.add_alarm, null)
        aBuilder.setView(addSheetPopup)
        aBuilder.setCancelable(false)
        dialog = aBuilder.create()
        dialog.show()

        addSheetPopup.findViewById<EditText>(R.id.addAlarmDateEditText).setOnClickListener {
            showDatePickerDialog(addSheetPopup)
        }


        addSheetPopup.findViewById<Button>(R.id.addAlarmSaveButton).setOnClickListener {

            dateET = addSheetPopup.findViewById(R.id.addAlarmDateEditText)
            //userTV = addSheetPopup.findViewById(R.id.addProductionUserTextView)
            typeET = addSheetPopup.findViewById(R.id.addAlarmTypeEditText)


            val nA = Alarm()
            nA.type = typeET.text.toString()
            nA.date = dateET.text.toString()
            nA.userId = currentUserId
            nA.sheetId = cowSelected.id
            nA.sheetName = cowSelected.name

            //listProductions.add(nP)
            //Firebase save here
            add(nA)
            //end save
            //dialog.dismiss() //BORRAR
        }

        addSheetPopup.findViewById<Button>(R.id.addAlarmCancelButton).setOnClickListener {
            dialog.dismiss()
        }

        cowS = addSheetPopup.findViewById(R.id.addAlarmCowNameSpinner)

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
            v.findViewById<EditText>(R.id.addAlarmDateEditText).setText(selectedDate)
        }

        newFragment.show(parentFragmentManager, "datePicker")
    }


    private fun add(a: Alarm){

        val addLiveData = viewModel.addAlarm(a)
        addLiveData.observe(viewLifecycleOwner) { r ->
            if(r != null) {
                if(r){
                    Toast.makeText(requireContext(), "Alarm added", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }else{
                    Toast.makeText(requireContext(), "Error while to adding", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}