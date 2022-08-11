package com.vacuno_app.menu.reports

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vacuno_app.R
import com.vacuno_app.databinding.FragmentReportBinding
import com.vacuno_app.domain.model.Production
import com.vacuno_app.utils.Constants
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding

    var barChart: BarChart? = null

    var barData: BarData? = null

    var barDataSet: BarDataSet? = null
    var barDataSet2: BarDataSet? = null

    var barEntriesArrayList: MutableList<BarEntry>? = null
    var barEntriesArrayList2: MutableList<BarEntry>? = null

    lateinit var labels: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        binding = FragmentReportBinding.inflate(layoutInflater)
        val root = binding.root

        barChart = binding.idBarChart

        getBarEntries()

        return root
    }

    private fun getBarEntries() {
        // creating a new array list
        barEntriesArrayList = mutableListOf()
        barEntriesArrayList2 = mutableListOf()

        labels = ArrayList()

        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/y")

        FirebaseDatabase.getInstance()
            .getReference("productions")
            .child(Constants.APP_FARM_ID)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    barEntriesArrayList!!.clear()
                    barEntriesArrayList2!!.clear()

                    var productionItems: List<Production> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(Production::class.java)!!
                    }

                    if(productionItems.isEmpty()) return
                    //filtrar
                    labels.clear()
                    Collections.sort(productionItems) { o1, o2 ->
                        var date1 = o1.dateCreated.toString()
                        var date2 = o2.dateCreated.toString()

                        date1 = date1.replace(" ", "")
                        date2 = date2.replace(" ", "")

                        val dateTime1: LocalDate = LocalDate.parse(date1, formatter)
                        val dateTime2: LocalDate = LocalDate.parse(date2, formatter)

                        dateTime2.compareTo(dateTime1)
                    }


                    var mList: MutableList<Production> = mutableListOf()
                    var tList: MutableList<Production> = mutableListOf()

                    productionItems.map { production ->
                        if(production.turn.equals("T")) {
                            tList.add(production)
                        } else mList.add(production)
                    }

                    if(mList.size > 100) {
                        mList = mList.subList(productionItems.size-101, productionItems.size);
                    }

                    if(tList.size > 100) {
                        tList = tList.subList(productionItems.size-101, productionItems.size);
                    }

                    var mArray = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                    var tArray = arrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

                    var cMA = 0
                    var cTA = 0

                    for (i in 0..6) {

                        var pointer = ""

                        if(mList.size > cMA && tList.size > cTA){

                            val mDsr = mList[cMA].dateCreated.toString()
                            val tDsr = tList[cTA].dateCreated.toString()

                            val mD = mDsr.replace(" ", "")
                            val tD = tDsr.replace(" ", "")

                            val dTimeM: LocalDate = LocalDate.parse(mD, formatter)
                            val dTimeT: LocalDate = LocalDate.parse(tD, formatter)

                            val r = dTimeM.compareTo(dTimeT)

                            pointer = mDsr

                            if(r < 0){
                                pointer = tDsr
                            }
                        } else if (mList.size > cMA){
                            pointer = mList[cMA].dateCreated.toString()
                        } else if(tList.size > cTA){
                            pointer = tList[cTA].dateCreated.toString()
                        } else {
                            break
                        }

                        while(mList.size > cMA && mList[cMA].dateCreated.toString() == pointer){
                            mArray[i] = mArray[i] + mList[cMA].total!!
                            cMA++
                        }

                        while(tList.size > cTA && tList[cTA].dateCreated.toString() == pointer){
                            tArray[i] = tArray[i] + tList[cTA].total!!
                            cTA++
                        }
                        labels.add(pointer)

                    }


                    mArray = mArray.copyOfRange(0, labels.size)
                    tArray = tArray.copyOfRange(0, labels.size)

                    //Log.e("M", ""+mArray.joinToString())
                    //Log.e("T", ""+tArray.joinToString())
                    //Log.e("L", ""+labels.toString())

                    var ind = 0f
                    mArray.map {
                        barEntriesArrayList!!.add(BarEntry(ind, it.toFloat()))
                        ind++
                    }

                    var ind2 = 0f
                    tArray.map {
                        barEntriesArrayList2!!.add(BarEntry(ind2, it.toFloat()))
                        ind2++
                    }


///GRAF
                    barDataSet = BarDataSet(barEntriesArrayList, getString(R.string.morning))
                    barDataSet!!.color = ColorTemplate.MATERIAL_COLORS[0]
                    barDataSet2 = BarDataSet(barEntriesArrayList2, getString(R.string.afternoon))
                    barDataSet2!!.color = ColorTemplate.MATERIAL_COLORS[1]


                    barData = BarData(barDataSet, barDataSet2)

                    barChart!!.data = barData

                    //barDataSet!!.setColors(*ColorTemplate.MATERIAL_COLORS)


                    val xAxis = barChart!!.xAxis
                    xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                    xAxis.setCenterAxisLabels(true)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.granularity = 1f
                    xAxis.isGranularityEnabled = true

                    val barSpace = 0.1f
                    val groupSpace = 0.5f
                    barData!!.barWidth = 0.15f
                    barChart!!.xAxis.axisMinimum = 0f
                    barChart!!.animate()
                    barChart!!.groupBars(0f, groupSpace, barSpace)

                    //barDataSet!!.valueTextColor = Color.BLACK

                    //barDataSet!!.valueTextSize = 16f
                    barChart!!.description.isEnabled = false

                    barChart!!.invalidate()

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


    }

}