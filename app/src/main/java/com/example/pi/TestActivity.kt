package com.example.pi

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pi.bd.SendQuery
import com.example.pi.data.TestInformation
import com.example.pi.errors_codes.ErrorsCodes

class TestActivity : AppCompatActivity() {
    private var countStages: Int = 0
    private var dimensionSize: Int = 0
    private var tebleFragment: Fragment? = null
    private lateinit var textViewCurrentStage: TextView
    private lateinit var testTableFragment: TestTableFragment
    private lateinit var checkStageThread: CheckStageThread
    protected var mID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val intentInfo = intent.extras?.getString("sizesInfo") as String
        mID = intent.extras?.getString("id")
        if (intentInfo == null)
            finishActivity(ErrorsCodes.SIDES_INFO_NULL.code)
        val listIntentInfo = intentInfo.split(' ')
        dimensionSize = Integer.parseInt(listIntentInfo[0])
        countStages = Integer.parseInt(listIntentInfo[1])
        textViewCurrentStage = findViewById(R.id.textViewCurrentStage)
        tebleFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        if (tebleFragment == null) {
            testTableFragment = TestTableFragment(countStages, dimensionSize)
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, testTableFragment)
                .commit()
        }
        editStageTextView()
    }

    override fun onStart() {
        super.onStart()
        checkStageThread = CheckStageThread()
        checkStageThread.start()
    }

    override fun onStop() {
        super.onStop()
        checkStageThread.interrupt()
        finishActivity(0)
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
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                if (mID != null)
                    intent.putExtra("id", mID)
                startActivity(intent)
            }
        }
        return true
    }

    private fun editStageTextView() {
        textViewCurrentStage.setText(getString(R.string.stage) + ' ' + testTableFragment.getCurrentStage().toString() + ' ' +
            getString(R.string.from) + ' ' + countStages.toString())
    }

    private fun getContext(): Context { return this }

    private inner class CheckStageThread: Thread() { //thread for checking change of stage
        override fun run() {
            super.run()
            var curStage = testTableFragment.getCurrentStage()
            for (i in curStage..countStages) {
                while(curStage == testTableFragment.getCurrentStage())
                    continue
                editStageTextView()
                curStage++
            }
            val answer = SendQuery.saveResults(mID, testTableFragment.getTestInformation())
            val intent = Intent(getContext(), ResultsActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra("testInfo", TestInformation.encodeToString(testTableFragment.getTestInformation()))
            if (mID != null)
                intent.putExtra("id", mID)
            startActivity(intent)
        }
    }
}