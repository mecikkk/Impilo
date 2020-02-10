package com.met.impilo

import android.app.Activity
import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.met.impilo.adapter.MeasurementsEditAdapter
import com.met.impilo.data.BodyMeasurements
import kotlinx.android.synthetic.main.activity_body_measurement.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class BodyMeasurementActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName
    private lateinit var viewModel : BodyMeasurementActivityViewModel
    private lateinit var separatedMeasurements : List<Pair<Date, List<Float>>>
    private lateinit var measurementsEditAdapter : MeasurementsEditAdapter
    private lateinit var lastMeasurements : BodyMeasurements
    private lateinit var loadingDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_measurement)

        viewModel = ViewModelProvider(this)[BodyMeasurementActivityViewModel::class.java]
        loadingDialog = createLoadingDialog()

        viewModel.fetchLastMeasurements()

        viewModel.getLastMeasurement().observe(this, androidx.lifecycle.Observer {
            lastMeasurements = it
            viewModel.fetchSeparatedMeasurements()
        })
        viewModel.getSeparatedValues().observe(this, androidx.lifecycle.Observer {
            separatedMeasurements = it
            measurementsEditAdapter = MeasurementsEditAdapter(this, it, lastMeasurements)
            measurements_edit_recycler_view.adapter = measurementsEditAdapter
        })

        measurements_edit_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@BodyMeasurementActivity)
        }

        update_measurements_fab.setOnClickListener {
            loadingDialog.show()
            Log.d(TAG, "NewMEasurements : ${measurementsEditAdapter.newMeasurements}")
            viewModel.addNewMeasurements(measurementsEditAdapter.newMeasurements){
                loadingDialog.dismiss()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        nested_scroll_view.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            Log.i(TAG, "$scrollY")
            if(scrollY in 201..499){
                update_measurements_fab.show()
                update_measurements_fab.shrink()
            } else if (scrollY > 500)
                update_measurements_fab.hide()
            else {
                update_measurements_fab.show()
                update_measurements_fab.extend()
            }

        }
    }

    fun createLoadingDialog(): Dialog {
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.loading_dialog)
        dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.attributes.windowAnimations = android.R.anim.fade_in
        return dialog
    }
}
