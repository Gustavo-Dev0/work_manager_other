package com.vacuno_app.register

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.vacuno_app.R
import com.vacuno_app.databinding.ActivityRegisterBinding
import com.vacuno_app.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    private lateinit var registerBtn: Button
    private lateinit var registerPB: ProgressBar
    private lateinit var nameET: EditText
    private lateinit var lastNameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var passwordConfirmET: EditText
    private lateinit var termsCB: CheckBox
    private lateinit var signInET: TextView

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        registerBtn = binding.registerButton
        registerPB = binding.registerProgressBar
        nameET = binding.nameEditText
        lastNameET = binding.lastNameEditText
        emailET = binding.emailEditText
        passwordET = binding.passwordEditText
        passwordConfirmET = binding.passwordConfirmEditText
        signInET = binding.signInTextView
        termsCB = binding.termsCheckBox

        startObservers()

        registerBtn.setOnClickListener {
            if(!isValidForm()) return@setOnClickListener

            uiModeLogin()

            val email = emailET.text.toString()
            val password = passwordET.text.toString()
            val name = nameET.text.toString()
            val lastName = lastNameET.text.toString()

            val newUser = User(password = password, email = email, name = name, lastName = lastName, status = "A")

            viewModel.register(newUser)
        }

        signInET.setOnClickListener {
            this.finish()
        }
        //signInET.isEnabled = false


    }

    private fun uiModeLogin() {
        registerPB.visibility = View.VISIBLE
        registerBtn.visibility = View.INVISIBLE
        nameET.isEnabled = false
        lastNameET.isEnabled = false
        emailET.isEnabled = false
        passwordET.isEnabled = false
        signInET.isEnabled = false
    }
    private fun uiModeEdit() {
        registerPB.visibility = View.GONE
        registerBtn.visibility = View.VISIBLE
        nameET.isEnabled = true
        lastNameET.isEnabled = true
        emailET.isEnabled = true
        passwordET.isEnabled = true
        signInET.isEnabled = true
    }


    private fun isValidForm(): Boolean {
        var isValid = true

        if(nameET.text.toString().isBlank()){
            isValid = false
            nameET.error = getString(R.string.required)
        }

        if(lastNameET.text.toString().isBlank()){
            isValid = false
            lastNameET.error = getString(R.string.required)
        }


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

        passwordET.let {
            val text = it.text.toString()

            if(text.isBlank()){
                isValid = false
                passwordET.error = getString(R.string.required)
            } else if(text.length < 6){
                isValid = false
                passwordET.error = getString(R.string.invalid_password)
            }
        }

        if(passwordConfirmET.text.toString() != passwordET.text.toString()){
            isValid = false
            passwordConfirmET.error = getString(R.string.invalid_password_confirm)
        }

        if(!termsCB.isChecked){
            isValid = false
            termsCB.error = getString(R.string.required_terms)
        }

        return isValid
    }

    private fun startObservers() {

        lifecycleScope.launch {
            viewModel.state.collect {
                if(it.isLoading != null && !it.isLoading!!){
                    if(it.isRegistered){
                        Toast.makeText(applicationContext, getString(R.string.success_registration), Toast.LENGTH_SHORT).show()
                        delay(2000)
                        this@RegisterActivity.finish()
                    }else{
                        Toast.makeText(applicationContext, getString(R.string.error), Toast.LENGTH_SHORT).show()
                        uiModeEdit()
                    }
                }

            }
        }
    }
}