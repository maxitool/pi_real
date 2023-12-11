package com.example.pi

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.pi.bd.SendQuery
import com.example.pi.data.TestTakerInformation
import com.example.pi.work_with_files.JSONFiles
import kotlinx.serialization.json.Json

class AuthorizationActivity : AppCompatActivity() {
    lateinit var textViewRegistration: TextView
    lateinit var textViewLogin: TextView
    lateinit var textViewPassword: TextView
    lateinit var editTextLogin: EditText
    lateinit var editTextPassword: EditText
    lateinit var buttonNext: Button
    lateinit var login: String
    lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        textViewLogin = findViewById(R.id.textViewLogin)
        textViewPassword = findViewById(R.id.textViewPassword)
        editTextLogin = findViewById(R.id.editTextLogin)
        editTextPassword = findViewById(R.id.editTextPassword)
        textViewRegistration = findViewById(R.id.textViewRegistration)
        textViewRegistration.setOnClickListener{
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
        buttonNext = findViewById(R.id.buttonNext)
        buttonNext.setOnClickListener {
            if (getInfo()) {
                val id = SendQuery.checkAccountData(login, password)
                if (id > 0) {
                    JSONFiles.WriteIntoJSON(this, "account_data.json", login, password)
                    val intent = Intent(this, PersonalAccountActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra("id", id.toString())
                    startActivity(intent)
                }
                else {
                    textViewLogin.setTextColor(Color.RED)
                    textViewPassword.setTextColor(Color.RED)
                    Toast.makeText(this, "login or password is uncorrect", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun getInfo(): Boolean {
        clearColorsTextViews()
        if (editTextLogin.text.toString() == "") {
            textViewLogin.setTextColor(Color.RED)
            Toast.makeText(this, "min login length is 4", Toast.LENGTH_LONG).show()
            return false
        }
        login = editTextLogin.text.toString()
        if (editTextPassword.text.toString().length < 6) {
            textViewPassword.setTextColor(Color.RED)
            Toast.makeText(this, "min password length is 6", Toast.LENGTH_LONG).show()
            return false
        }
        password = editTextPassword.text.toString()
        return true
    }
    private fun clearColorsTextViews() {
        textViewPassword.setTextColor(Color.BLACK)
        textViewLogin.setTextColor(Color.BLACK)
    }
}