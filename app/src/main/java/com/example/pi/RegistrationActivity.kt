package com.example.pi

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.JsonWriter
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.pi.bd.SendQuery
import com.example.pi.data.TestTakerInformation
import com.example.pi.databases.DBAccess
import com.example.pi.work_with_files.JSONFiles
import com.patrykandpatrick.vico.core.extension.setFieldValue
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.util.ArrayList

class RegistrationActivity : AppCompatActivity() {
    lateinit var spinner: Spinner
    lateinit var editTextLogin: EditText
    lateinit var editTextPassword: EditText
    lateinit var editTextAge: EditText
    lateinit var radioGroup: RadioGroup
    lateinit var checkBoxIsSchoolchild: CheckBox
    lateinit var checkBoxIsStudent: CheckBox
    lateinit var buttonNext: Button
    lateinit var textViewLogin: TextView
    lateinit var textViewPassword: TextView
    lateinit var textViewAge: TextView
    var isAllFieldsFull: Boolean = false
    private var selectedArea: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        val userInfoArray = intent.extras?.getStringArray("userInfo")
        val listAreas = SendQuery.getAreas()
        spinner = findViewById(R.id.spinner) as Spinner
        spinner.adapter = ArrayAdapter(this, R.layout.spinner_item, listAreas)
        editTextLogin = findViewById(R.id.editTextLogin)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextAge = findViewById(R.id.editTextAge)
        radioGroup = findViewById(R.id.radioGroup)
        checkBoxIsSchoolchild = findViewById(R.id.checkBoxIsSchoolchild)
        checkBoxIsStudent = findViewById(R.id.checkBoxIsStudent)
        buttonNext = findViewById(R.id.buttonNext)
        textViewLogin = findViewById(R.id.textViewLogin)
        textViewPassword = findViewById(R.id.textViewPassword)
        textViewAge = findViewById(R.id.textViewAge)
        if (userInfoArray != null) {
            val userInfo = Transformations.ArrayToTestTakerInfo(userInfoArray)
            if (userInfo.age != -1)
                editTextAge.setText(userInfo.age.toString())
            radioGroup.check(radioGroup.getChildAt(userInfo.gender).id)
            spinner.setSelection(userInfo.area)
            checkBoxIsSchoolchild.isChecked = userInfo.isSchoolchild
            checkBoxIsStudent.isChecked = userInfo.isStudent
        }
        checkBoxIsSchoolchild.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectedArea = spinner.selectedItemPosition
                spinner.setSelection(0)
                spinner.isEnabled = false
                checkBoxIsStudent.isEnabled = false
            }
            else {
                spinner.setSelection(selectedArea)
                spinner.isEnabled = true
                checkBoxIsStudent.isEnabled = true
            }
        }
        checkBoxIsStudent.setOnCheckedChangeListener { buttonView, isChecked ->
            checkBoxIsSchoolchild.isEnabled = !isChecked
        }
        buttonNext.setOnClickListener() {
            val testTakerInfo = getTestTakerInfo()
            if (isAllFieldsFull) {
                val id = SendQuery.addUser(testTakerInfo)
                if (id > 0) {
                    JSONFiles.WriteIntoJSON(this,"account_data.json", testTakerInfo.login, testTakerInfo.password)
                    val intent = Intent(this, InstructionActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra("id", id.toString())
                    startActivity(intent)
                }
                else {
                    textViewLogin.setTextColor(Color.RED)
                    Toast.makeText(this, "login already exists", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun getTestTakerInfo(): TestTakerInformation {
        clearColorsTextViews()
        val info = TestTakerInformation()
        info.area = spinner.selectedItemPosition
        info.gender = radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.checkedRadioButtonId))
        info.isStudent = checkBoxIsStudent.isChecked
        info.isSchoolchild = checkBoxIsSchoolchild.isChecked
        if (editTextLogin.text.toString() == "") {
            textViewLogin.setTextColor(Color.RED)
            Toast.makeText(this, "min login length is 4", Toast.LENGTH_LONG).show()
            return info
        }
        info.login = editTextLogin.text.toString()
        if (editTextPassword.text.toString().length < 6) {
            textViewPassword.setTextColor(Color.RED)
            Toast.makeText(this, "min password length is 6", Toast.LENGTH_LONG).show()
            return info
        }
        info.password = editTextPassword.text.toString()
        if (editTextAge.text.toString() == "" || editTextAge.text.toString() == "0") {
            textViewAge.setTextColor(Color.RED)
            Toast.makeText(this, "age is empty or is 0", Toast.LENGTH_LONG).show()
            return info
        }
        info.age = Integer.parseInt(editTextAge.text.toString())
        isAllFieldsFull = true
        return info
    }
    private fun clearColorsTextViews() {
        textViewPassword.setTextColor(Color.BLACK)
        textViewLogin.setTextColor(Color.BLACK)
        textViewAge.setTextColor(Color.BLACK)
    }
}