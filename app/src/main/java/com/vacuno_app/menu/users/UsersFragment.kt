package com.vacuno_app.menu.users;

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.vacuno_app.R
import com.vacuno_app.data.remote.model.UserToFarm
import com.vacuno_app.databinding.EditUserBinding
import com.vacuno_app.databinding.FragmentUsersBinding
import com.vacuno_app.domain.model.User
import com.vacuno_app.menu.users.adapter.UserAdapter
import com.vacuno_app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UsersFragment: Fragment() {

    val viewModel: UsersViewModel by activityViewModels()
    lateinit var binding: FragmentUsersBinding
    private lateinit var aBuilder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog
    private lateinit var sp: Spinner

    /*SearchView search;*/

    private lateinit var emailET: EditText
    private lateinit var rolS: Spinner
    private lateinit var statusCB: CheckBox

    private lateinit var rolT: String

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter

    private lateinit var listUsers: MutableList<User>
    private lateinit var listUsersToFarm: MutableList<UserToFarm>

    //In future will be with Firebase
    private lateinit var listRoles: List<String>

    private lateinit var myToast: Toast

    //private lateinit var optionsRoles: String[]
    //String idCount;
    /*override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }*/

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

        binding = FragmentUsersBinding.inflate(layoutInflater);
        val root = binding.root
        listUsers = mutableListOf()
        listUsersToFarm = mutableListOf()
        listRoles = listOf("admin", "vaquero", "observador")

        myToast = Toast.makeText(context, null, Toast.LENGTH_SHORT)

        binding.addUserButton.setOnClickListener{
            createAddDialog()
        }

        recyclerView = binding.usersRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        userAdapter = UserAdapter(listUsers, listUsersToFarm)
        recyclerView.adapter = userAdapter

        viewModel.getUsers()

        viewModel.users.observe(viewLifecycleOwner){
            print45(it)
            listUsers.clear()
            listUsers.addAll(it)
            userAdapter.notifyDataSetChanged()
        }

        viewModel.usersFarm.observe(viewLifecycleOwner){
            listUsersToFarm.clear()
            listUsersToFarm.addAll(it)
            userAdapter.notifyDataSetChanged()
        }

        /*db.getReference("roles").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listRoles.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                Rol s = ds.getValue(Rol.class);
                //if(s.getStatus().equals("*") || s.getStatus().equals("Inactivo"))  continue;//ignore element whit status remove
                s.setId(ds.getKey());
                listRoles.add(s);
            }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });*/

        return root;
    }

    private fun print45(it: List<User>?) {
        if (it != null) {
            for(s in it){
                Log.e("LISTA ", ""+s.name)
            }
        }
    }


    private fun createAddDialog(){
        aBuilder = AlertDialog.Builder(context);
        val addUserPopup: View = layoutInflater.inflate(R.layout.add_user, null);
        aBuilder.setView(addUserPopup)
        aBuilder.setCancelable(false)
        dialog = aBuilder.create()
        dialog.show()

        rolS = addUserPopup.findViewById(R.id.addRolSpinner)

        val optionsServiceAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listRoles);
        rolS.adapter = optionsServiceAdapter

        rolS.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                rolT = listRoles[i]
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}

        }

        addUserPopup.findViewById<Button>(R.id.addSaveButton).setOnClickListener {

            if(viewModel.users.value == null){
                toast(getString(R.string.loading))
                return@setOnClickListener
            }

            if(viewModel.users.value?.size!! >= Constants.MAX_USERS){
                toast(getString(R.string.limit_users))
                return@setOnClickListener
            }

            emailET = addUserPopup.findViewById(R.id.addEmailEditText)
            statusCB = addUserPopup.findViewById(R.id.addStatusCheckBox)

            var isValid = true
            emailET.let {
                val text = it.text.toString()
                if(text.isBlank()){
                    isValid = false
                    emailET.error = getString(R.string.required)
                }else if(!Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                    isValid = false
                    emailET.error = getString(R.string.invalid_email)
                }
            }
            if(!isValid) return@setOnClickListener

            add(emailET.text.toString(), rolT)

        }


        addUserPopup.findViewById<Button>(R.id.addCancelButton).setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun toast(message: String) {
        myToast.cancel()
        myToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        myToast.show()
    }


    private fun add(email: String, rol: String){

        if(FirebaseAuth.getInstance().currentUser?.email == email) {
            toast(getString(R.string.already_registered_user))
            return
        }

        for(u in viewModel.users.value!!) {
            if(u.email == email){
                toast(getString(R.string.already_registered_user))
                return
            }
        }

        val userEmailLiveData = viewModel.getUserByEmail(email)
        userEmailLiveData.observe(viewLifecycleOwner) { user ->
            if(user == null){
                //Toast.makeText(context, "Usuario no registrado en VacunoApp", Toast.LENGTH_SHORT).show()
                toast(getString(R.string.user_not_registered))

            }else if(user.uid != "0"){

                viewModel.addUserToFarm(user.uid!!, rol)
                viewModel.addFarmToUser(user.uid!!)

                Toast.makeText(context, getString(R.string.success), Toast.LENGTH_SHORT).show()

                dialog.dismiss()
            }
        }

    }

    private fun createEditDialog(key: String, emailUser: String) {
        val binding = EditUserBinding.inflate(layoutInflater)
        aBuilder = AlertDialog.Builder(context);
        aBuilder.setView(binding.root)
        aBuilder.setCancelable(false)
        dialog = aBuilder.create()
        dialog.show()

        rolS = binding.editRolSpinner

        val optionsServiceAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listRoles);
        rolS.adapter = optionsServiceAdapter

        rolS.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                rolT = listRoles[i]
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}

        }
        binding.editUserEmailRolTextView.text = emailUser

        binding.editRolSaveButton.setOnClickListener {

            edit(key, rolT)
            dialog.dismiss()
        }

        binding.editRolCancelButton.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun edit(uid: String, newRol: String) {
        viewModel.userSetRoleInFarm(uid, newRol)
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {

        val u: User = viewModel.users.value?.get(item.groupId)!!
        val uF: UserToFarm = viewModel.usersFarm.value?.get(item.groupId)!!

        when(item.itemId) {
            101 -> {
                //viewModel.userChangeRoleInFarm(u.uid)
                createEditDialog(uF.key!!, u.email!!)
            }

            102 -> {
                if(uF.status == "A")
                    viewModel.userSetStatusInFarm(uF.key!!, "I")
                else
                    viewModel.userSetStatusInFarm(uF.key!!, "A")
            }

        }
        //Toast.makeText(context, uF.status, Toast.LENGTH_SHORT).show()

        return  super.onContextItemSelected(item)
    }


}