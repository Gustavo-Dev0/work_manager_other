package com.vacuno_app.menu.users;

import android.annotation.SuppressLint;
import android.app.AlertDialog
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.vacuno_app.R
import com.vacuno_app.databinding.FragmentUsersBinding;
import com.vacuno_app.domain.model.User
import com.vacuno_app.menu.users.adapter.UserAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.ArrayList


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

    //In future will be with Firebase
    private lateinit var listRoles: List<String>

    //private lateinit var optionsRoles: String[]
    //String idCount;

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentUsersBinding.inflate(layoutInflater);
        val root = binding.root
        listUsers = mutableListOf()
        listRoles = listOf("admin", "vaquero", "observador")

        binding.addUserButton.setOnClickListener{
            createAddDialog()
        }

        recyclerView = binding.usersRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        userAdapter = UserAdapter(listUsers)
        recyclerView.adapter = userAdapter

        viewModel.getUsers()

        viewModel.users.observe(viewLifecycleOwner){
            print45(it)
            listUsers.clear()
            listUsers.addAll(it)
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

            emailET = addUserPopup.findViewById(R.id.addEmailEditText)
            statusCB = addUserPopup.findViewById(R.id.addStatusCheckBox)

            /*User nS = new User();
            nS.setName(nameET.getText().toString());
            nS.setEmail(emailET.getText().toString());
            nS.setLast(lastET.getText().toString());
            nS.setLast2(last2ET.getText().toString());
            nS.setPassword(passwordET.getText().toString());
            nS.setUsername(nameET.getText().toString());
            if(statusCB.isChecked()){
                nS.setStatus("Activo");
            }else{
                nS.setStatus("Inactivo");
            }*/

            //Firebase save here

            if(add(emailET.text.toString())){
                dialog.dismiss()
            }
            //end save

        }

        addUserPopup.findViewById<Button>(R.id.addCancelButton).setOnClickListener {
            dialog.dismiss()
        }

    }



    /*
    public Integer generateId(){

        int idInt = Integer.parseInt(idCount);
        idInt++;
        Map<String, Object> idMap = new HashMap<>();
        idMap.put("users", idInt+"");
        db.getReference("idCont").updateChildren(idMap);
        return idInt;
    }*/

    private fun add(email: String): Boolean{

        val user: User? = viewModel.getUserByEmail(email)

        if(user == null){

            Toast.makeText(context, "Usuario no registrado", Toast.LENGTH_SHORT).show()

            return false
        }
        //Falta revisar que ya existe en la granja y que no sea el dueño

        Toast.makeText(context, "Usuario encontrado"+user.uid, Toast.LENGTH_SHORT).show()
        return false

    }
/*
    @SuppressLint("NotifyDataSetChanged")
    public void order(int i) {
        switch (i) {
            case 0:
            Collections.sort(listUsers, User.serviceNameAZComparator);
            break;
            case 1:
            Collections.sort(listUsers, User.serviceNameZAComparator);
            break;
            case 2:
            Collections.sort(listUsers, User.serviceStatusComparator);
            break;

        }
        userAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        super.onContextItemSelected(item);
        User s = listUsers.get(item.getGroupId());
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
        BackupList.listUsersBackup.clear();
        BackupList.listUsersBackup.addAll(listUsers);
        order(sp.getSelectedItemPosition());
        return true;
    }


    */
}