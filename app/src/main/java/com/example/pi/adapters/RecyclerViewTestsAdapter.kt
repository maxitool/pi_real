package com.example.pi.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pi.R
import com.example.pi.ResultsActivity
import com.example.pi.data.ResultTest
import com.example.pi.data.TestInformation

class RecyclerViewTestsAdapter() :
RecyclerView.Adapter<RecyclerViewTestsAdapter.ViewHolder>() {
    protected val SPACE = "    "
    protected val MAX_RATING = 5f
    protected lateinit var myView: View
    protected var mID: String? = null
    protected var resultsTestsArray: ArrayList<ResultTest>? = null
    protected var resultsElTestArray: ArrayList<TestInformation?>? = null

    fun setSettings(mID: String?, resultsTestsArray: ArrayList<ResultTest>?, resultsElTestArray: ArrayList<TestInformation?>?) {
        this.mID = mID
        this.resultsTestsArray = resultsTestsArray
        this.resultsElTestArray = resultsElTestArray
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View
            myView = view
            textView = view.findViewById(R.id.textView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.test_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (resultsTestsArray != null && resultsTestsArray!![position] != null) {
            viewHolder.textView.text = (position + 1).toString() + '.' + SPACE + "date: " + resultsTestsArray!![position].date + SPACE +
                    "rating: " + String.format("%.2f", resultsTestsArray!![position].rating) + '/' + String.format("%.2f", MAX_RATING) + '✯' +
                    SPACE + "size: " + resultsTestsArray!![position].dimension.toString() + 'x' + resultsTestsArray!![position].dimension.toString() +
                    SPACE + "tables: " + resultsTestsArray!![position].countStages.toString()
            viewHolder.textView.setOnClickListener {
                val intent = Intent(myView.context, ResultsActivity::class.java)
                val testInfo = TestInformation.testInfoPlusResultTest(resultsElTestArray!![position], resultsTestsArray!![position])
                if (mID != null)
                    intent.putExtra("id", mID)
                intent.putExtra("testInfo", TestInformation.encodeToString(testInfo!!))
                myView.context.startActivity(intent)
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() : Int{
        if (resultsTestsArray != null)
            return resultsTestsArray!!.size
        return 0
    }
}
