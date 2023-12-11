package com.example.pi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.example.pi.data.TableSizes

class InstructionActivity : AppCompatActivity() {
    private lateinit var spinnerSizeTable: Spinner
    private lateinit var spinnerCountTables: Spinner
    private lateinit var buttonNext: Button
    protected var mID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)
        mID = intent.extras?.getString("id")
        spinnerSizeTable = findViewById(R.id.spinnerSizeTable)
        spinnerSizeTable.adapter = ArrayAdapter(this, R.layout.spinner_item, TableSizes.sizes)
        spinnerSizeTable.setSelection(TableSizes.standartPos)
        spinnerCountTables = findViewById(R.id.spinnerCountTables)
        val listLengthSides: List<String> = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        spinnerCountTables.adapter = ArrayAdapter(this, R.layout.spinner_item, listLengthSides)
        spinnerCountTables.setSelection(4) //4 - position of 5 count tables
        buttonNext = findViewById(R.id.buttonNext)
        buttonNext.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            val lengthSides = spinnerSizeTable.selectedItem.toString().split('x')
            intent.putExtra("sizesInfo", lengthSides[0] + ' ' + spinnerCountTables.selectedItem.toString())
            if (mID != null)
                intent.putExtra("id", mID)
            startActivity(intent)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = getMenuInflater()
        inflater.inflate(R.menu.header, menu)
        return true;
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.homeButton -> {
                val intent = Intent(this, PersonalAccountActivity::class.java)
                if (mID != null)
                    intent.putExtra("id", mID)
                startActivity(intent)
            }
        }
        return true
    }
}