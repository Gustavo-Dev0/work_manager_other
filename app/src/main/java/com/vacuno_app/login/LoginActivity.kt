package com.vacuno_app.login;

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import com.vacuno_app.MainActivity
import com.vacuno_app.databinding.ActivityLoginBinding
import com.vacuno_app.register.RegisterActivity
import com.vacuno_app.select_farm.SelectFarmActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity(): AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var loginBtn: Button
    private lateinit var loginPB: ProgressBar
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var signUpET: TextView

    @SuppressLint("SourceLockedOrientationActivity")
    override fun  onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);


        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root);
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if(viewModel.getCurrentUser() != null){
            val intent = Intent(applicationContext, SelectFarmActivity::class.java)
            intent.putExtra("userId", viewModel.getCurrentUser()?.uid)
            startActivity(intent)
            this@LoginActivity.finish()
        }


        loginBtn = binding.loginButton
        loginPB = binding.loginProgressBar
        emailET = binding.emailEditText
        passwordET = binding.passwordEditText
        signUpET = binding.signUpTextView

        startObservers()

        loginBtn.setOnClickListener {

            if(!isValidForm()) return@setOnClickListener

            uiModeLogin()

            val email = emailET.text.toString();
            val password = passwordET.text.toString();
            viewModel.login(email, password)
        }

        signUpET.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun isValidForm(): Boolean {
        var isValid = true


        emailET.let {
            val text = it.text.toString()
            if(text.isBlank()){
                isValid = false
                emailET.error = "Required"
            }else if(!Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                isValid = false
                emailET.error = "Invalid email"
            }
        }

        if(passwordET.text.toString().isBlank()){
            isValid = false
            passwordET.error = "Required"
        }

        return isValid
    }

    private fun uiModeLogin() {
        loginPB.visibility = View.VISIBLE
        loginBtn.visibility = View.INVISIBLE
        emailET.isEnabled = false
        passwordET.isEnabled = false
        signUpET.isEnabled = false
    }
    private fun uiModeEdit() {
        loginPB.visibility = View.GONE
        loginBtn.visibility = View.VISIBLE
        emailET.isEnabled = true
        passwordET.isEnabled = true
        signUpET.isEnabled = true
    }


    private fun startObservers() {

        lifecycleScope.launch {
            viewModel.state.collect {
                if(it.isLoading != null && !it.isLoading!!){
                    if(it.isLogged){
                        val intent = Intent(applicationContext, SelectFarmActivity::class.java)
                        intent.putExtra("userId", viewModel.getCurrentUser()?.uid)
                        startActivity(intent)
                        this@LoginActivity.finish()
                    }else{
                        Toast.makeText(applicationContext, "The email or password are incorrect.", Toast.LENGTH_SHORT).show()
                        uiModeEdit()
                    }
                }

            }
        }
    }

}