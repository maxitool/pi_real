package com.example.pi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import com.example.pi.databases.DBAccess

class RegistrationActivity : AppCompatActivity() {
    lateinit var spinner: Spinner
    lateinit var editTextLogin: EditText
    lateinit var editTextPassword: EditText
    lateinit var editTextAge: EditText
    lateinit var radioGroup: RadioGroup
    lateinit var checkBoxIsSchoolchild: CheckBox
    lateinit var checkBoxIsStudent: CheckBox
    lateinit var  buttonNext: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        val userInfoArray = intent.extras?.getStringArray("userInfo")
        val dbAccess = DBAccess(this)
        val listAreas = dbAccess.areasOfActivity as ArrayList<String>
        spinner = findViewById(R.id.spinner) as Spinner
        spinner.adapter = ArrayAdapter(this, R.layout.spinner_item, listAreas)
        editTextLogin = findViewById(R.id.editTextLogin)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextAge = findViewById(R.id.editTextAge)
        radioGroup = findViewById(R.id.radioGroup)
        checkBoxIsSchoolchild = findViewById(R.id.checkBoxIsSchoolchild)
        checkBoxIsStudent = findViewById(R.id.checkBoxIsStudent)
        buttonNext = findViewById(R.id.buttonNext)
        val k = 1
        if (userInfoArray != null) {
            val userInfo = Transformations.ArrayToTestTakerInfo(userInfoArray)
            if (userInfo.age != -1)
                editTextAge.setText(userInfo.age.toString())
            radioGroup.check(radioGroup.getChildAt(userInfo.gender).id)
            spinner.setSelection(userInfo.area)
            checkBoxIsSchoolchild.isChecked = userInfo.isSchoolchild
            checkBoxIsStudent.isChecked = userInfo.isStudent
        }
    }
}