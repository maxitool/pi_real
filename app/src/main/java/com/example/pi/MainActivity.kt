package com.example.pi

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pi.databases.DBAccess
import com.example.pi.databases.tables.TestTakerInformation

class MainActivity : AppCompatActivity() {
    lateinit var spinner: Spinner
    lateinit var editTextAge: EditText
    lateinit var radioGroup: RadioGroup
    lateinit var checkBoxIsSchoolchild: CheckBox
    lateinit var checkBoxIsStudent: CheckBox
    lateinit var buttonNext: Button
    lateinit var textViewAuthorization: TextView
    lateinit var textViewRegistration: TextView
    var isAllFieldsFull = false
    lateinit var userInformation: UserInformation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dbAccess = DBAccess(this)
        val listAreas = dbAccess.areasOfActivity as ArrayList<String>
        spinner = findViewById(R.id.spinner) as Spinner
        spinner.adapter = ArrayAdapter(this, R.layout.spinner_item, listAreas)
        editTextAge = findViewById(R.id.editTextAge) as EditText
        radioGroup = findViewById(R.id.radioGroup) as RadioGroup
        checkBoxIsSchoolchild = findViewById(R.id.checkBoxIsSchoolchild) as CheckBox
        checkBoxIsStudent = findViewById(R.id.checkBoxIsStudent) as CheckBox
        buttonNext = findViewById(R.id.buttonNext) as Button
        buttonNext.setOnClickListener {
            val testTakerInfo = getTestTakerInfo()
            if (isAllFieldsFull) {
                val dbAccess = DBAccess(this)
                userInformation.id = dbAccess.addNewAnonymous(testTakerInfo)
                userInformation.isAnonymous = true
                // ...
            }
            }
        textViewAuthorization = findViewById(R.id.textViewAuthorization) as TextView
        textViewAuthorization.setOnClickListener{
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
        if (editTextAge.text.toString() == "")
            return info
        info.age = Integer.parseInt(editTextAge.text.toString())
        isAllFieldsFull = true
        return info
    }
}
