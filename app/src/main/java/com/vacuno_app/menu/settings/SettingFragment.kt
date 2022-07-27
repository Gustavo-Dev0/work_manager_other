package com.vacuno_app.menu.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.vacuno_app.R
import com.vacuno_app.databinding.FragmentSettingBinding
import com.vacuno_app.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    val viewModel: SettingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingBinding.inflate(layoutInflater)

        val progressBar = binding.logoutProgressBar

        val email = FirebaseAuth.getInstance().currentUser?.email!!
        binding.emailInformationTextView.text = email


        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                progressBar.visibility = View.VISIBLE
                delay(3000)
                viewModel.logout()


                val sharedPref = activity?.getSharedPreferences("vacuno", 0)!!
                with (sharedPref.edit()) {
                    remove("currentFarmId")
                    //putString("currentFarmId", "")
                    commit()
                }



                val intent = Intent(context, LoginActivity::class.java)
                progressBar.visibility = View.INVISIBLE
                startActivity(intent)
                requireActivity().finish()
            }

        }

        return binding.root
    }




}