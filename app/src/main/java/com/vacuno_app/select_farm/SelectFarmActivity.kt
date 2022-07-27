package com.vacuno_app.select_farm

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.vacuno_app.MainActivity
import com.vacuno_app.R
import com.vacuno_app.databinding.ActivitySelectFarmBinding
import com.vacuno_app.domain.model.Farm
import com.vacuno_app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectFarmActivity : AppCompatActivity() {

    private val viewModel: SelectFarmViewModel by viewModels()

    private lateinit var binding: ActivitySelectFarmBinding
    private lateinit var linearLayout: LinearLayout

    private lateinit var aBuilder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    private lateinit var nameET: EditText
    private lateinit var addFarmPB: ProgressBar

    private lateinit var currentUserId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(readSharedPreferences() != ""){
            val intent = Intent(applicationContext, MainActivity::class.java)
            Log.e("SP", "" + readSharedPreferences())
            intent.putExtra("app_farm_id", readSharedPreferences())
            startActivity(intent)
            this@SelectFarmActivity.finish()
        }

        binding = ActivitySelectFarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        val idUser: String = intent.extras?.getString("userId").toString()
        Log.e("Usuario recibido: ", idUser+"")
        currentUserId = idUser

        linearLayout = binding.farmsLinearLayout

        viewModel.getAllFarmsFromCurrentUser(idUser)
        viewModel.farmsFromUser.observe(this) {
            if(it.isNotEmpty()){
                /*Log.e("sizeList: ", ""+it.size)
                linearLayout.removeAllViews()
                for(fId in it){
                    createFarmTextView(fId)
                }*/
                Log.e("sizeList: ", ""+it.size)
                linearLayout.removeAllViews()
                it.map { farmId ->
                    createFarmTextView(farmId)
                }
            }

        }

        val addBtn = binding.addFarmButton
        addBtn.setOnClickListener {
            createAddDialog()
        }

    }

    private fun createFarmTextView(fId: String) {
        val fLiveData = viewModel.getFarmById(fId)
        val fBtn = Button(this)

        fLiveData.observe(this){ farm ->
            if(farm != null){
                linearLayout.removeView(fBtn)
                fBtn.text = farm.name
                fBtn.setOnClickListener{
                    Log.e("Click", farm.id!!)
                    saveInSharedPreferences(farm.id!!)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("app_farm_id", readSharedPreferences())
                    startActivity(intent)
                    this.finish()
                }
                linearLayout.addView(fBtn, 0)
            }

        }
    }

    private fun saveInSharedPreferences(id: String) {
        val sharedPref = getSharedPreferences("vacuno", 0)

        with(sharedPref.edit()){
            putString("currentFarmId", id)
            apply()
        }
    }

    private fun readSharedPreferences(): String? {
        val defaultValue = ""
        val sharedPref = getSharedPreferences("vacuno", 0)

        return sharedPref.getString("currentFarmId", defaultValue)
    }

    private fun createAddDialog() {
        aBuilder = AlertDialog.Builder(this)
        val addFarmPopup: View = layoutInflater.inflate(R.layout.add_farm, null)
        aBuilder.setView(addFarmPopup)
        aBuilder.setCancelable(false)
        dialog = aBuilder.create()
        dialog.show()


        addFarmPopup.findViewById<Button>(R.id.addFarmSaveButton)
            .setOnClickListener {

                nameET = addFarmPopup.findViewById(R.id.addFarmNameEditText)
                addFarmPB = addFarmPopup.findViewById(R.id.addFarmProgressBar)

                if(nameET.text.toString().isBlank()){
                    nameET.error = "Name required"
                    return@setOnClickListener
                }


                nameET.isEnabled = false
                addFarmPopup.findViewById<Button>(R.id.addFarmSaveButton).visibility = View.INVISIBLE
                addFarmPopup.findViewById<Button>(R.id.addFarmCancelButton).visibility = View.INVISIBLE

                addFarmPB.visibility = View.VISIBLE

                val nF = Farm()
                nF.name = nameET.text.toString()
                //Firebase save here
                addFarm(nF)
                //end save
                dialog.dismiss()
            }
        addFarmPopup.findViewById<View>(R.id.addFarmCancelButton)
            .setOnClickListener { dialog.dismiss() }

    }

    private fun addFarm(nF: Farm) {
        val result = viewModel.addFarm(nF, currentUserId)
        result.observe(this) { response ->
            if(response == null) return@observe

            if(response){
                Toast.makeText(this, "Added!!", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(this, "An occurred an error", Toast.LENGTH_SHORT).show()
            }

        }

    }


}