package com.example.pi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class TestActivity : AppCompatActivity() {
    private val COUNT_STAGES: Int = 5
    private lateinit var textViewStage: TextView
    private var currentStage: Int = 1
    private lateinit var testTableFragment: TestTableFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        textViewStage = findViewById(R.id.textViewStage)
        testTableFragment = TestTableFragment()
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, testTableFragment).commit()
    }
}