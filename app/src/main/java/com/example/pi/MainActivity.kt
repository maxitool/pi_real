package com.example.pi

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pi.bd.SendQuery
import com.example.pi.data.TestTakerInformation
import com.example.pi.work_with_files.JSONFiles
import com.patrykandpatrick.vico.core.extension.setFieldValue
import org.json.JSONObject
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {
    private lateinit var spinner: Spinner
    private lateinit var editTextAge: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var checkBoxIsSchoolchild: CheckBox
    private lateinit var checkBoxIsStudent: CheckBox
    private lateinit var buttonNext: Button
    private lateinit var textViewAuthorization: TextView
    private lateinit var textViewRegistration: TextView
    private lateinit var textViewAge: TextView
    private var isAllFieldsFull = false
    private var selectedArea: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //checking if the json file exists
        try {
            val inputStream = openFileInput("account_data.json")
            inputStream.close()
        }
        catch (e : IOException) {
            e.printStackTrace()
            JSONFiles.createJSONFile(this, "account_data.json")
        }
        //automatic authorization attempt
        try {
            val jsonObj = JSONObject(JSONFiles.readFromFile(this, "account_data.json"))
            val login = jsonObj.getString("login")
            val password = jsonObj.getString("password")
            if (login != "" && password != "") {
                val id = SendQuery.checkAccountData(login, password)
                if (id > 0) {
                    val intent = Intent(this, PersonalAccountActivity::class.java)
                    intent.putExtra("id",id.toString())
                    startActivity(intent)
                }
                else
                    JSONFiles.WriteIntoJSON(this, "account_data.json", "", "")
            }
        }
        catch (e: IOException) {
            e.printStackTrace()
            JSONFiles.deleteFile(this, "account_data.json")
            finish()
        }
        val listAreas = SendQuery.getAreas()
        spinner = findViewById(R.id.spinner) as Spinner
        spinner.adapter = ArrayAdapter(this, R.layout.spinner_item, listAreas)
        editTextAge = findViewById(R.id.editTextAge) as EditText
        radioGroup = findViewById(R.id.radioGroup) as RadioGroup
        checkBoxIsSchoolchild = findViewById(R.id.checkBoxIsSchoolchild) as CheckBox
        checkBoxIsStudent = findViewById(R.id.checkBoxIsStudent) as CheckBox
        textViewAge = findViewById(R.id.textViewAge) as TextView
        buttonNext = findViewById(R.id.buttonNext) as Button
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
        buttonNext.setOnClickListener {
            val testTakerInfo = getTestTakerInfo()
            if (isAllFieldsFull) {
                val id = SendQuery.addUser(testTakerInfo)
                if (id > 0) {
                    val intent = Intent(this, InstructionActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra("id", id.toString())
                    startActivity(intent)
                }
                //val intent = Intent(this, InstructionActivity::class.java) //del
                //startActivity(intent) //del
            }
        }
        textViewAuthorization = findViewById(R.id.textViewAuthorization) as TextView
        textViewAuthorization.setOnClickListener {
            val intent = Intent(this, AuthorizationActivity::class.java)
            startActivity(intent)
        }
        textViewRegistration = findViewById(R.id.textViewRegistration) as TextView
        textViewRegistration.setOnClickListener {
            val testTakerInfo = getTestTakerInfo()
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.putExtra("userInfo", Transformations.TestTakerInfoToArray(testTakerInfo))
            startActivity(intent)
        }
    }
    private fun getTestTakerInfo(): TestTakerInformation {
        val info = TestTakerInformation()
        info.area = spinner.selectedItemPosition
        info.gender = radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.checkedRadioButtonId))
        info.isStudent = checkBoxIsStudent.isChecked
        info.isSchoolchild = checkBoxIsSchoolchild.isChecked
        if (editTextAge.text.toString() == "" || editTextAge.text.toString() == "0") {
            textViewAge.setTextColor(Color.RED)
            Toast.makeText(this, "age is empty or is 0", Toast.LENGTH_LONG).show()
            return info
        }
        info.age = Integer.parseInt(editTextAge.text.toString())
        isAllFieldsFull = true
        return info
    }

}
