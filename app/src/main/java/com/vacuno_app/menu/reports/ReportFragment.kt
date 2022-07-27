package com.vacuno_app.menu.reports

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vacuno_app.databinding.FragmentReportBinding
import com.vacuno_app.domain.model.Production
import com.vacuno_app.utils.Constants


class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding

    var barChart: BarChart? = null

    var barData: BarData? = null

    var barDataSet: BarDataSet? = null

    var barEntriesArrayList: MutableList<BarEntry>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(layoutInflater)
        val root = binding.root

        barChart = binding.idBarChart

        getBarEntries()

        /*barDataSet = BarDataSet(barEntriesArrayList, "Production of milk")

        barData = BarData(barDataSet)

        barChart!!.data = barData

        barDataSet!!.setColors(*ColorTemplate.MATERIAL_COLORS)

        barDataSet!!.valueTextColor = Color.BLACK

        barDataSet!!.valueTextSize = 16f
        barChart!!.description.isEnabled = false*/

        return root
    }

    private fun getBarEntries() {
        // creating a new array list
        barEntriesArrayList = mutableListOf()

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        /*barEntriesArrayList!!.add(BarEntry(1f, 4f))
        barEntriesArrayList!!.add(BarEntry(2f, 6f))
        barEntriesArrayList!!.add(BarEntry(3f, 8f))
        barEntriesArrayList!!.add(BarEntry(4f, 2f))
        barEntriesArrayList!!.add(BarEntry(5f, 4f))
        barEntriesArrayList!!.add(BarEntry(6f, 1f))*/

        FirebaseDatabase.getInstance()
            .getReference("productions")
            .child(Constants.APP_FARM_ID)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    barEntriesArrayList!!.clear()

                    val productionItems: List<Production> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(Production::class.java)!!
                    }
                    var i = 1f

                    productionItems.map { production ->
                        barEntriesArrayList!!.add(BarEntry(i, production.total?.toFloat()!!))
                        i++
                    }

                    barDataSet = BarDataSet(barEntriesArrayList, "Production of milk")

                    barData = BarData(barDataSet)

                    barChart!!.data = barData

                    barDataSet!!.setColors(*ColorTemplate.MATERIAL_COLORS)

                    barDataSet!!.valueTextColor = Color.BLACK

                    barDataSet!!.valueTextSize = 16f
                    barChart!!.description.isEnabled = false

                    barChart!!.invalidate()

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


    }

}