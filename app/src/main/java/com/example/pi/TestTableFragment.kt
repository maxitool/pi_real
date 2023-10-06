package com.example.pi

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class TestTableFragment : Fragment() {
    val TABLE_SIZE = 25
    private lateinit var view: View
    private lateinit var tableLayout: TableLayout
    private lateinit var tableRow: TableRow
    private lateinit var textView: TextView
    private lateinit var arrayNumbers: Array<Int>
    private lateinit var clickedTextView: TextView
    private var currentNumber: Int = 0
    private lateinit var returnStateTableElThread: ReturnStateTableElThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrayNumbers = Array<Int>(TABLE_SIZE){0}
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
        for (i in 0..kotlin.math.sqrt(TABLE_SIZE.toDouble()).toInt() - 1) {
            tableRow = TableRow(view.context)
            tableRow.setLayoutParams(TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT))
            for (j in 0..kotlin.math.sqrt(TABLE_SIZE.toDouble()).toInt() - 1) {
                textView = TextView(view.context)
                textView.id = i * kotlin.math.sqrt(TABLE_SIZE.toDouble()).toInt() + j + 1
                textView.setLayoutParams(TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT))
                textView.setPadding(10,10,10,10)
                textView.gravity = Gravity.CENTER
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 55f)
                textView.setBackgroundResource(R.drawable.border_table_item)
                textView.setOnClickListener{
                    clickedTextView = view.findViewById(it.id) as TextView
                    if (currentNumber == Integer.parseInt(clickedTextView.text.toString())) {
                        currentNumber++
                        clickedTextView.setBackgroundColor(ContextCompat.getColor(view.context, R.color.correct))
                    }
                    else
                        clickedTextView.setBackgroundColor(ContextCompat.getColor(view.context, R.color.incorrect))
                    returnStateTableElThread.addId(it.id)
                }
                tableRow.addView(textView)
            }
            tableLayout.addView(tableRow)
        }
        randTableElements()
    }

    override fun onStart() {
        super.onStart()
        returnStateTableElThread = ReturnStateTableElThread()
        returnStateTableElThread.start()
    }

    override fun onStop() {
        super.onStop()
        returnStateTableElThread.stopThread()
    }

    fun isLastNumber(): Boolean { if (currentNumber >= TABLE_SIZE) return true; return false }

    fun randTableElements() {
        randArrayNumbers()
        for (i in 0..TABLE_SIZE - 1) {
            textView = this.view.findViewById(i + 1)
            textView.setText(arrayNumbers[i].toString())
        }
    }

    private fun randArrayNumbers() {
        var arrayIndex = Array<Boolean>(TABLE_SIZE) {false}
        var pointer: Int = 0
        for (i in 1..TABLE_SIZE / 2) {
            do {
                pointer = Random.nextInt(0, TABLE_SIZE)
            } while(arrayIndex[pointer])
            arrayIndex[pointer] = true
            arrayNumbers[pointer] = i
        }
        pointer = 0
        for (i in TABLE_SIZE / 2 + 1..TABLE_SIZE - 1)
            for (j in pointer..TABLE_SIZE - 1)
                if (!arrayIndex[j]) {
                    arrayNumbers[j] = i
                    pointer = j + 1
                    break
                }
    }

    private inner class ReturnStateTableElThread: Thread() {
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
}