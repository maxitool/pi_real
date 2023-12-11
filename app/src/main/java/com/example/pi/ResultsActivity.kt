package com.example.pi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import com.example.pi.calculations.Calculates
import com.example.pi.data.TestInformation
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import com.patrykandpatrick.vico.views.chart.ChartView

class ResultsActivity : AppCompatActivity() {
    private val MAX_RATING: Float = 5f
    private var testInfo: TestInformation? = null
    private var ER: Float = 0f
    private var VR: Float = 0f
    private var PU: Float = 0f
    private lateinit var ratingBar: RatingBar
    private lateinit var textViewRating: TextView
    private lateinit var textViewER: TextView
    private lateinit var textViewVR: TextView
    private lateinit var textViewPU: TextView
    private lateinit var chartViewTimes: ChartView
    private lateinit var chartViewErrors: ChartView
    private lateinit var buttonAgain: Button
    protected var mID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        val testInfoStr = intent.extras?.getString("testInfo")
        mID = intent.extras?.getString("id")
        if (testInfoStr != null) {
            testInfo = TestInformation.decodeFromString(testInfoStr)
            ratingBar = findViewById(R.id.ratingBar)
            ratingBar.rating = testInfo!!.rating
            textViewRating = findViewById(R.id.textViewRating)
            textViewRating.text = this.getString(R.string.rating_is)  + ' ' + String.format("%.2f", testInfo!!.rating) + " из " + String.format("%.2f", MAX_RATING)
            textViewER = findViewById(R.id.textViewER)
            ER = Calculates.calculateER(testInfo?.countStages, testInfo?.timeList)
            textViewER.text = String.format("%.2f", ER)
            textViewVR = findViewById(R.id.textViewVR)
            VR = Calculates.calculateVR(ER, testInfo?.dimension, testInfo?.timeList)
            textViewVR.text = String.format("%.2f", VR)
            textViewPU = findViewById(R.id.textViewPU)
            PU = Calculates.calculatePU(ER, testInfo?.countStages, testInfo?.timeList)
            textViewPU.text = String.format("%.2f", PU)
            chartViewTimes = findViewById(R.id.chartViewTimes) as ChartView
            var list = emptyList<FloatEntry>()
            for (i in 0..testInfo!!.countStages - 1) {
                var time = 0L
                for (j in 0..testInfo!!.dimension * testInfo!!.dimension - 1)
                    time += testInfo!!.timeList[i * testInfo!!.dimension * testInfo!!.dimension + j]
                list += entryOf(i + 1, time / 1000)
            }
            chartViewTimes.setModel(entryModelOf(list))
            chartViewErrors = findViewById(R.id.chartViewErrors)
            list = emptyList<FloatEntry>()
            for (i in 0..testInfo!!.countStages - 1) {
                var error = 0
                for (j in 0..testInfo!!.dimension * testInfo!!.dimension - 1)
                    error += testInfo!!.errorsList[i * testInfo!!.dimension * testInfo!!.dimension + j]
                list += entryOf(i + 1, error)
            }
            chartViewErrors.setModel(entryModelOf(list))
            buttonAgain = findViewById(R.id.buttonAgain)
            buttonAgain.setOnClickListener() {
                intent = Intent(this, InstructionActivity::class.java)
                if (mID != null)
                    intent.putExtra("id", mID)
                startActivity(intent)
            }
        }
        else
            finish()
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