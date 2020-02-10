package com.met.impilo


import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hadiidbouk.charts.BarData
import com.met.impilo.adapter.MeasurementsAdapter
import com.met.impilo.data.BodyRating
import com.met.impilo.data.Demand
import com.met.impilo.data.Goal
import com.met.impilo.data.PersonalData
import com.met.impilo.utils.Const
import com.met.impilo.utils.ViewUtils
import com.met.impilo.utils.getIntDay
import com.met.impilo.utils.getIntMonth
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.fragment_home.actual_weight_text_view
import kotlinx.android.synthetic.main.fragment_home.chart_weight_progress
import kotlinx.android.synthetic.main.fragment_home.ic_arrow_weight_diff
import kotlinx.android.synthetic.main.fragment_home.weight_diff_text_view
import kotlinx.android.synthetic.main.fragment_profil.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue


class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileFragmentViewModel
    private val TAG = javaClass.simpleName
    private var changeGoalProposition: Goal = Goal.FAT_LOSE
    private var changeGoalName = ""

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ProfileFragmentViewModel::class.java]
        viewModel.fetchAllWeights()
        viewModel.fetchPersonalData()
        viewModel.fetchBmiAndBf()
        viewModel.fetchDemand()
        viewModel.fetchLastMeasurements()

        sign_out_action.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(context!!).apply {
                setTitle(getString(R.string.sign_out))
                setMessage(getString(R.string.want_to_sign_out))
                setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.signOut()
                }
                setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    Log.e(TAG, "SingOut canceled")
                }
            }
            val dialog = builder.create()
            dialog.show()
            dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_corner)
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, com.met.impilo.R.color.colorAccent))
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, com.met.impilo.R.color.textColor))
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).gravity = Gravity.START
        }

        initBfSeekbar()

        edit_measurements_button.setOnClickListener {
            startActivityForResult( Intent(context, BodyMeasurementActivity::class.java), Const.BODY_MEASUREMENTS_REQUEST)
        }

        viewModel.getAllWeights().observe(this, Observer {
            updateWeightProgress(it)
        })

        viewModel.getPersonalData().observe(this, Observer {
            updatePersonalDataInfo(it)
        })

        viewModel.getBMI().observe(this, Observer {
            bmi_data_text_view.text = "$it"
        })

        viewModel.getBF().observe(this, Observer {
            bf_data_text_view.text = "$it%"
            bf_value_text_view.text = StringBuilder(getString(R.string.body_fat_level) + " : $it%")
            viewModel.fetchThumbPosition(it)
            viewModel.fetchBodyRating(it)
        })

        viewModel.getDemand().observe(this, Observer {
            updateDemand(it)
        })

        viewModel.getLastMeasurement().observe(this, Observer {
            measurements_recycler_view.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = MeasurementsAdapter(it)
            }
        })


        change_goal.setOnClickListener {
            showGoalChangingDialog()

        }

        viewModel.getThumbPosition().observe(this, Observer {
            bf_seekbar.setProgress(it)
        })

        viewModel.getBodyRating().observe(this, Observer {
            when (it) {
                BodyRating.MAINTAIN_OK -> {
                    body_rating_text_view.text = getString(R.string.maintain_ok)
                    change_goal.visibility = View.GONE
                }
                BodyRating.BULKING_UP_OK -> {
                    body_rating_text_view.text = getString(R.string.bulking_ok)
                    change_goal.visibility = View.GONE
                }
                BodyRating.FAT_LOSE_NEED -> {
                    body_rating_text_view.text = getString(R.string.fat_loss_need)
                    change_goal.visibility = View.VISIBLE
                    changeGoalProposition = Goal.FAT_LOSE
                    changeGoalName = getString(R.string.loss_title)
                }
                BodyRating.FAT_LOSE_PROPOSITION -> {
                    body_rating_text_view.text = getString(R.string.fat_loss_proposition)
                    change_goal.visibility = View.VISIBLE
                    changeGoalProposition = Goal.FAT_LOSE
                    changeGoalName = getString(R.string.loss_title)

                }
                BodyRating.FAT_LOSE_OK -> {
                    body_rating_text_view.text = getString(R.string.fat_lose_ok)
                    change_goal.visibility = View.GONE
                }
                BodyRating.BULKING_UP_PROPOSITION -> {
                    body_rating_text_view.text = getString(R.string.bulking_up_proposition)
                    change_goal.visibility = View.VISIBLE
                    changeGoalProposition = Goal.MUSCLE_GAIN
                    changeGoalName = getString(R.string.bulking_title)

                }
                BodyRating.MAINTAIN_PROPOSITION -> {
                    body_rating_text_view.text = getString(R.string.maintain_proposition)
                    change_goal.visibility = View.VISIBLE
                    changeGoalProposition = Goal.MAINTAIN
                    changeGoalName = getString(R.string.maintain_title)
                }
                BodyRating.LOW_BODY_FAT -> body_rating_text_view.text = getString(R.string.low_body_fat)
                else -> {}
            }
        })

        viewModel.fetchBmiAndBf()


    }

    private fun initBfSeekbar() {

        bf_seekbar.setCustomSectionTextArray { _, array ->
            array.clear()
            array.put(1, getString(R.string.low_body_fat))
            array.put(3, getString(R.string.normal_body_fat))
            array.put(5, getString(R.string.increased_body_fat))
            array.put(7, getString(R.string.high_body_fat))

            array
        }

        bf_seekbar.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
                Log.i(TAG, "Slide : $progressFloat")
                val color: Int = when {
                    progressFloat <= 2f -> {
                        ContextCompat.getColor(context!!, R.color.proteinsColor)
                    }
                    progressFloat <= 4f -> {
                        ContextCompat.getColor(context!!, R.color.positiveGreen)
                    }
                    progressFloat <= 6f -> {
                        ContextCompat.getColor(context!!, R.color.moderateLevel)
                    }
                    progressFloat <= 8f -> {
                        ContextCompat.getColor(context!!, R.color.colorAccent)
                    }
                    else -> ContextCompat.getColor(context!!, R.color.colorAccent)
                }

                bf_seekbar.setSecondTrackColor(color)
                bf_seekbar.setThumbColor(color)
            }

            override fun getProgressOnActionUp(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float) {
            }

            override fun getProgressOnFinally(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
            }
        }
    }
    private fun showGoalChangingDialog() {
        val builder = AlertDialog.Builder(context!!).apply {
            setMessage(StringBuilder(getString(R.string.change_goal_message) + "$changeGoalName ?"))
            setTitle(getString(R.string.change_goal))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                Log.i(TAG, "Changing goal to $changeGoalName")
                viewModel.changeGoal(changeGoalProposition) {
                    if (it) {
                        ViewUtils.createSnackbar(content, getString(R.string.change_goal_succes)).show()
                        viewModel.fetchBmiAndBf()
                        viewModel.fetchPersonalData()
                    } else ViewUtils.createSnackbar(content, getString(R.string.change_goal_error)).show()
                }
            }
            setNegativeButton(getString(R.string.no)) { _, _ ->
                Log.w(TAG, "Change canceled")
            }
        }
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, R.color.textColor))
    }

    private fun updateDemand(demand: Demand) {
        calories_data_text_view.text = "${demand.calories} kcal"
        carbs_data_text_view.text = "${demand.carbohydares} g"
        proteins_data_text_view.text = "${demand.proteins} g"
        fats_date_text_view.text = "${demand.fats} g"
    }

    private fun updatePersonalDataInfo(personalData: PersonalData) {
        somatotype_data_text_view.text = getString(personalData.somatotype!!.nameRef)
        goal_data_text_view.text = getString(personalData.goal!!.nameRef)
    }

    private fun updateWeightProgress(weights: List<Pair<Date, Float>>) {
        val data = ArrayList<BarData>()
        val months = DateFormatSymbols.getInstance().shortMonths
        val max = viewModel.getMaxWeight(weights)
        val min = viewModel.getMinWeight(weights)

        chart_weight_progress.setMaxValue(max - min + 0.2f)
        Log.i(TAG, "MAX : $max | MIN : $min | BAR_MAX : ${(max - min + 0.2f)}")
        weights.forEach {
            val chartValue = it.second - min + 0.1f
            Log.i(TAG, "ACTUAL : ${it.second} | BAR_VALUE : $chartValue")
            data.add(BarData("${it.first.getIntDay()} ${months[it.first.getIntMonth() - 1].toUpperCase()}", chartValue, "${it.second} kg"))
        }

        actual_weight_text_view.text = StringBuilder("${weights[weights.size-1].second}")

        if(weights.size > 2) {
            if ((weights[weights.size - 1].second - weights[weights.size - 2].second) <= 0) ic_arrow_weight_diff.apply {
                setImageResource(R.drawable.ic_arrow_drop_down)
                imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.positiveGreen))
            }
            else ic_arrow_weight_diff.apply {
                setImageResource(R.drawable.ic_arrow_drop_up)
                imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
            }

            val weightDiff = BigDecimal((weights[weights.size - 1].second - weights[weights.size - 2].second).absoluteValue.toDouble()).setScale(1, RoundingMode.HALF_UP)
            weight_diff_text_view.text = StringBuilder("$weightDiff kg")

        } else {
            ic_arrow_weight_diff.apply {
                setImageResource(R.drawable.ic_arrow_drop_down)
                imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.positiveGreen))
            }
            weight_diff_text_view.text = StringBuilder("0 kg")
        }

        chart_weight_progress.setDataList(data)
        chart_weight_progress.build()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            Const.BODY_MEASUREMENTS_REQUEST -> {
                if(resultCode == Activity.RESULT_OK){
                    viewModel.fetchAllWeights()
                    viewModel.fetchPersonalData()
                    viewModel.fetchBmiAndBf()
                    viewModel.fetchLastMeasurements()
                }

            }
        }
    }
}
