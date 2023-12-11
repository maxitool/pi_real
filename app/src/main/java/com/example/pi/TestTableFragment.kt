package com.example.pi

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.pi.calculations.Calculates
import com.example.pi.data.FontTableSizes
import com.example.pi.data.TestInformation
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class TestTableFragment(countStages: Int, dimensionSize: Int) : Fragment() {
    private var dimensionSize: Int = 0
    private var countStages: Int = 0
    private var currentNumber: Int = 1
    private var currentStage: Int = 1
    private var beginTime: Long = 0
    private var currentErrors: Int = 0
    private var testInformation: TestInformation
    private lateinit var view: View
    private lateinit var tableLayout: TableLayout
    private lateinit var tableRow: TableRow
    private lateinit var textView: TextView
    private lateinit var arrayNumbers: Array<Int>
    private lateinit var clickedTextView: TextView
    private lateinit var returnStateTableElThread: ReturnStateTableElThread
    private lateinit var checkTimePressingThread: CheckTimePressingThread
    private lateinit var frameLayout: FrameLayout

    init{
        if (countStages > 0)
            this.countStages = countStages
        if (dimensionSize > 0)
            this.dimensionSize = dimensionSize
        testInformation = TestInformation(LocalDate.now().toString(), countStages,dimensionSize,
            0f, ArrayList(0), ArrayList(0))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrayNumbers = Array<Int>(dimensionSize * dimensionSize){0}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_test_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view
        tableLayout = view.findViewById(R.id.tableLayout) as TableLayout
        frameLayout = view.findViewById(R.id.frameLayout) as FrameLayout
        tableLayout.post {
            var widthNumber = frameLayout.width / dimensionSize - 10
            val fontSize = FontTableSizes.getFontSize(dimensionSize)
            for (i in 0..dimensionSize - 1) {
                tableRow = TableRow(view.context)
                tableRow.setLayoutParams(TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT))
                for (j in 0..dimensionSize - 1) {
                    textView = TextView(view.context)
                    textView.id = i * dimensionSize + j + 1
                    textView.setLayoutParams(TableRow.LayoutParams(widthNumber, TableLayout.LayoutParams.MATCH_PARENT))
                    textView.setPadding(5,5,5,5)
                    textView.gravity = Gravity.CENTER
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize)
                    textView.setBackgroundResource(R.drawable.border_table_item)
                    textView.setOnClickListener {
                        if (currentStage != countStages + 1) {
                            clickedTextView = view.findViewById(it.id) as TextView
                            if (currentNumber == Integer.parseInt(clickedTextView.text.toString())) {
                                testInformation.timeList.add(System.currentTimeMillis() - beginTime)
                                clickedTextView.setBackgroundColor(
                                    ContextCompat.getColor(
                                        view.context,
                                        R.color.correct
                                    )
                                )
                                currentNumber++
                                testInformation.errorsList.add(currentErrors)
                                if (currentNumber > dimensionSize * dimensionSize) {
                                    if (currentStage != countStages) {
                                        currentNumber = 1
                                        updateTable()
                                    } else
                                        testInformation.rating = Calculates.calculateRating(
                                            testInformation.countStages,
                                            testInformation.timeList,
                                            testInformation.errorsList
                                        )
                                    currentStage++
                                }
                                currentErrors = 0
                                beginTime = System.currentTimeMillis()
                            } else {
                                clickedTextView.setBackgroundColor(ContextCompat.getColor(view.context,R.color.incorrect))
                                currentErrors++
                            }
                            returnStateTableElThread.addId(it.id)
                        }
                    }
                    tableRow.addView(textView)
                }
                tableLayout.addView(tableRow)
            }
            updateTable()
        }
    }

    override fun onStart() {
        super.onStart()
        returnStateTableElThread = ReturnStateTableElThread()
        returnStateTableElThread.start()
        checkTimePressingThread = CheckTimePressingThread()
        checkTimePressingThread.start()
    }

    override fun onResume() {
        super.onResume()
        if (beginTime == 0L)
            beginTime = System.currentTimeMillis()
    }

    override fun onStop() {
        super.onStop()
        returnStateTableElThread.stopThread()
    }

    fun getCurrentStage(): Int { return currentStage }

    fun getTestInformation(): TestInformation { return testInformation }

    private fun updateTable() {
        randArrayNumbers()
        for (i in 0..dimensionSize * dimensionSize - 1) {
            textView = this.view.findViewById(i + 1)
            textView.setText(arrayNumbers[i].toString())
        }
    }

    private fun randArrayNumbers() {
        var indexesArray = ArrayList<Int>(0)
        for (i in 0..dimensionSize * dimensionSize - 1)
            indexesArray.add(i + 1)
        var pointer: Int = 0
        for (i in 0..dimensionSize * dimensionSize - 1) {
            pointer = Random.nextInt(0, indexesArray.size)
            arrayNumbers[i] = indexesArray[pointer]
            indexesArray.removeAt(pointer)
        }
    }

    private inner class ReturnStateTableElThread: Thread() { //for returning default background state of table elements
        var isActive: Boolean = true
        var isBusy: Boolean = false
        var idsList: ArrayList<Int> = arrayListOf()
        private lateinit var textView: TextView

        fun addId(id: Int) {
            while(isBusy)
                TimeUnit.MILLISECONDS.sleep(100)
            isBusy = true
            idsList.add(id)
            isBusy = false
        }

        fun stopThread() { isActive = false }

        override fun run() {
            super.run()
            while (isActive)
                if (!idsList.isEmpty()) {
                    textView = view.findViewById(idsList.get(0))
                    idsList.removeAt(0)
                    TimeUnit.MILLISECONDS.sleep(100)
                    textView.setBackgroundResource(R.drawable.border_table_item)
                }
        }
    }
    private inner class CheckTimePressingThread: Thread() { //Thread to restart the test when there are a lot of errors or a long press
        val MAX_TIME: Long = 40000L //in milliseconds
        val MAX_ERRORS: Int = 30

        override fun run() {
            super.run()
            while(beginTime == 0L || (System.currentTimeMillis() - beginTime < MAX_TIME && currentErrors < MAX_ERRORS))
                continue
            activity?.finish()
        }
    }
}