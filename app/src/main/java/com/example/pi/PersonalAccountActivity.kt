package com.example.pi

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pi.adapters.RecyclerViewTestsAdapter
import com.example.pi.bd.SendQuery
import com.example.pi.data.ResultTest
import com.example.pi.data.TestInformation
import com.example.pi.data.TestTakerInformation
import com.example.pi.work_with_files.JSONFiles
import java.net.PasswordAuthentication

class PersonalAccountActivity : AppCompatActivity() {
    protected var mID: String? = null
    private var userInfo: TestTakerInformation? = null
    private var resultsTestsArray: ArrayList<ResultTest>? = null
    private var resultsElTestArray: ArrayList<TestInformation?> = ArrayList<TestInformation?>(0)
    private lateinit var imageButtonCancel: ImageButton
    private lateinit var imageButtonEdit: ImageButton
    private lateinit var editTextLogin: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextAge: EditText
    private lateinit var spinner: Spinner
    private lateinit var radioGroup: RadioGroup
    private lateinit var checkBoxIsSchoolchild: CheckBox
    private lateinit var checkBoxIsStudent: CheckBox
    private lateinit var textViewMaxRating: TextView
    private lateinit var textViewAverageRating: TextView
    private lateinit var recyclerViewTests: RecyclerView
    private lateinit var buttonDoTest: Button
    private lateinit var textViewLogin: TextView
    private lateinit var textViewPassword: TextView
    private lateinit var textViewAge: TextView
    private lateinit var radioButtonFemale: RadioButton
    private lateinit var radioButtonMale: RadioButton
    private lateinit var radioButtonAnother: RadioButton
    private lateinit var imageViewPasswordEye: ImageButton
    private var isEditing: Boolean = false
    private var isAllFieldsFull: Boolean = false
    private var selectedArea: Int = 0
    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_account)
        mID = intent.extras?.getString("id")

        //findViewById
        imageButtonCancel = findViewById(R.id.imageButtonCancel)
        imageButtonEdit = findViewById(R.id.imageButtonEdit)
        editTextLogin = findViewById(R.id.editTextLogin)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextAge = findViewById(R.id.editTextAge)
        spinner = findViewById(R.id.spinner)
        radioGroup = findViewById(R.id.radioGroup)
        checkBoxIsSchoolchild = findViewById(R.id.checkBoxIsSchoolchild)
        checkBoxIsStudent = findViewById(R.id.checkBoxIsStudent)
        textViewMaxRating = findViewById(R.id.textViewMaxRating)
        textViewAverageRating = findViewById(R.id.textViewAverageRating)
        recyclerViewTests = findViewById(R.id.recyclerViewTests)
        buttonDoTest = findViewById(R.id.buttonDoTest)
        textViewLogin = findViewById(R.id.textViewLogin)
        textViewPassword = findViewById(R.id.textViewPassword)
        textViewAge = findViewById(R.id.textViewAge)
        radioButtonFemale = findViewById(R.id.radioButtonFemale)
        radioButtonMale = findViewById(R.id.radioButtonMale)
        radioButtonAnother = findViewById(R.id.radioButtonAnother)
        imageViewPasswordEye = findViewById(R.id.imageViewPasswordEye)

        val areas = SendQuery.getAreas()
        spinner.adapter = ArrayAdapter(this, R.layout.spinner_item, areas)
        userInfo = SendQuery.getUserInfoFromID(mID)
        if (userInfo == null)
            finish()
        fillingInInfo()

        //set OnCheckedChangeListener to CheckBoxes
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
        //set OnClickListeners on buttons
        imageButtonEdit.setOnClickListener {
            if (isEditing) {
                val userInfo = getTestTakerInfo()
                if (isAllFieldsFull)
                    if (isChangedUserInfo(userInfo)) {
                        val answer = SendQuery.UpdateAccountInfo(mID, userInfo)
                        if (answer == 0) {
                            JSONFiles.WriteIntoJSON(this, "account_data.json", userInfo.login, userInfo.password)
                            val intent = Intent(this, PersonalAccountActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            if (mID != null)
                                intent.putExtra("id", mID)
                            startActivity(intent)
                        }
                        else
                            Toast.makeText(this, "error of update info", Toast.LENGTH_SHORT).show()
                    }
                    else
                        Toast.makeText(this, "information didn't changed", Toast.LENGTH_SHORT).show()
            }
            else {
                isEditing = true
                imageButtonEdit.setImageResource(R.drawable.baseline_check_24)
                changeStateUserInfoElements()
            }
        }
        imageButtonCancel.setOnClickListener {
            isEditing = false
            imageButtonEdit.setImageResource(R.drawable.baseline_edit_24)
            changeStateUserInfoElements()
            fillingInInfo()
        }
        buttonDoTest.setOnClickListener {
            val intent = Intent(this, InstructionActivity::class.java)
            if (mID != null)
                intent.putExtra("id", mID)
            startActivity(intent)
        }
        imageViewPasswordEye.setOnClickListener {
            if (!isPasswordVisible) {
                isPasswordVisible = true
                editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            else {
                isPasswordVisible = false
                editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        changeStateUserInfoElements()
        resultsTestsArray = SendQuery.getResults(mID)
        var maxRating = 0f
        var averageRating = 0f
        if (resultsTestsArray != null) {
            for (i in 0..resultsTestsArray!!.size - 1) {
                if (resultsTestsArray!![i].rating > maxRating)
                    maxRating = resultsTestsArray!![i].rating
                averageRating += resultsTestsArray!![i].rating
                val resultEl = SendQuery.getTimesAndMistakes(resultsTestsArray!![i].testId)
                resultsElTestArray.add(resultEl)
            }
            textViewMaxRating.setText(String.format("%.2f", maxRating))
            textViewAverageRating.setText(String.format("%.2f", averageRating / resultsElTestArray.size))
            var myAdapter = RecyclerViewTestsAdapter()
            myAdapter.setSettings(mID, resultsTestsArray, resultsElTestArray)
            recyclerViewTests.layoutManager = LinearLayoutManager(this)
            recyclerViewTests.adapter = myAdapter
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = getMenuInflater()
        inflater.inflate(R.menu.header_personal_account, menu)
        return true;
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.exitButton -> {
                JSONFiles.WriteIntoJSON(this, "account_data.json", "", "")
                val intent = Intent(this, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
        return true
    }
    private fun changeStateUserInfoElements() {
        imageButtonCancel.isVisible = isEditing
        editTextLogin.isEnabled = isEditing
        editTextPassword.isEnabled = isEditing
        editTextAge.isEnabled = isEditing
        radioGroup.isEnabled = isEditing
        radioButtonFemale.isEnabled = isEditing
        radioButtonMale.isEnabled = isEditing
        radioButtonAnother.isEnabled = isEditing
        checkBoxIsSchoolchild.isEnabled = isEditing
        checkBoxIsStudent.isEnabled = isEditing
        imageViewPasswordEye.isEnabled = isEditing
        spinner.isEnabled = isEditing
        if (checkBoxIsSchoolchild.isChecked) {
            spinner.isEnabled = false
            checkBoxIsStudent.isEnabled = false
        }
        if (checkBoxIsStudent.isChecked) {
            spinner.isEnabled = false
            checkBoxIsSchoolchild.isEnabled = false
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
        if (editTextLogin.text.toString().indexOf("Anonymous") >= 0) {
            textViewLogin.setTextColor(Color.RED)
            Toast.makeText(this, "Do not use the word \'Anonymous\' in your login", Toast.LENGTH_LONG).show()
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
    private fun isChangedUserInfo(userInfo: TestTakerInformation) : Boolean {
        if (this.userInfo!!.login != userInfo.login)
            return true
        if (this.userInfo!!.password != userInfo.password)
            return true
        if (this.userInfo!!.age != userInfo.age)
            return true
        if (this.userInfo!!.area != userInfo.area)
            return true
        if (this.userInfo!!.gender != userInfo.gender)
            return true
        if (this.userInfo!!.isSchoolchild != userInfo.isSchoolchild)
            return true
        if (this.userInfo!!.isStudent != userInfo.isStudent)
            return true
        return false
    }
    private fun fillingInInfo() {
        editTextLogin.setText(userInfo!!.login)
        editTextPassword.setText(userInfo!!.password)
        editTextAge.setText(userInfo!!.age.toString())
        spinner.setSelection(userInfo!!.area)
        radioGroup.check(radioGroup.getChildAt(userInfo!!.gender).id)
        checkBoxIsSchoolchild.isChecked = userInfo!!.isSchoolchild
        checkBoxIsStudent.isChecked = userInfo!!.isStudent
    }
}