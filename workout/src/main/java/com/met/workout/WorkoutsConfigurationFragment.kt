package com.met.workout


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.met.impilo.adapter.WorkoutBaseFragment
import com.met.impilo.data.BodyRating
import com.met.impilo.data.Goal
import com.met.impilo.data.workouts.TrainingPlanInfo
import com.met.impilo.utils.Const
import com.met.impilo.utils.ViewUtils
import com.met.workout.own_plan.MyOwnPlanActivity
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.fragment_workouts_configuration.*
import java.lang.Exception


class WorkoutsConfigurationFragment : WorkoutBaseFragment() {

    private var TAG = javaClass.simpleName

    private lateinit var viewModel : WorkoutsConfigurationFragmentViewModel

    private var changeGoalProposition : Goal = Goal.FAT_LOSE
    private var changeGoalName = ""

    companion object {
        @JvmStatic
        fun newInstance() = WorkoutsConfigurationFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workouts_configuration, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(WorkoutsConfigurationFragmentViewModel::class.java)

        initBfSeekbar()

        have_plan_card.setOnClickListener {
            startActivityForResult(Intent(context, MyOwnPlanActivity::class.java), Const.MY_OWN_PLAN_CREATOR_REQUEST)
        }

        change_goal.setOnClickListener {
            val builder = AlertDialog.Builder(context!!).apply {
                setMessage(StringBuilder(getString(com.met.impilo.R.string.change_goal_message) + "$changeGoalName ?"))
                setTitle(getString(com.met.impilo.R.string.change_goal))
                setPositiveButton(getString(com.met.impilo.R.string.yes)) { _, _ ->
                    Log.i(TAG, "Changing goal to $changeGoalName")
                    viewModel.changeGoal(changeGoalProposition){
                        if(it){
                            ViewUtils.createSnackbar(content_layout, getString(com.met.impilo.R.string.change_goal_succes)).show()
                            viewModel.fetchBF()
                        }
                        else ViewUtils.createSnackbar(content_layout, getString(com.met.impilo.R.string.change_goal_error)).show()
                    }
                }
                setNegativeButton(getString(com.met.impilo.R.string.no)) { _, _ ->
                    Log.w(TAG, "Change canceled")
                }
            }
            val dialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context!!, com.met.impilo.R.color.colorAccent))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context!!, com.met.impilo.R.color.textColor))

        }

        viewModel.getBF().observe(this, Observer { bf ->
            bf_value_text_view.text = StringBuilder(getString(com.met.impilo.R.string.body_fat_level) + " : $bf%")
            viewModel.fetchThumbPosition(bf)
            viewModel.fetchBodyRating(bf)
        })

        viewModel.getThumbPosition().observe(this, Observer {
            bf_seekbar.setProgress(it)
        })

        viewModel.getBodyRating().observe(this, Observer {
            when(it){
                BodyRating.MAINTAIN_OK -> {
                    body_rating_text_view.text = getString(com.met.impilo.R.string.maintain_ok)
                    change_goal.visibility = View.GONE
                }
                BodyRating.BULKING_UP_OK -> {
                    body_rating_text_view.text = getString(com.met.impilo.R.string.bulking_ok)
                    change_goal.visibility = View.GONE
                }
                BodyRating.FAT_LOSE_NEED -> {
                    body_rating_text_view.text = getString(com.met.impilo.R.string.fat_loss_need)
                    change_goal.visibility = View.VISIBLE
                    changeGoalProposition = Goal.FAT_LOSE
                    changeGoalName = getString(com.met.impilo.R.string.loss_title)
                }
                BodyRating.FAT_LOSE_PROPOSITION -> {
                    body_rating_text_view.text = getString(com.met.impilo.R.string.fat_loss_proposition)
                    change_goal.visibility = View.VISIBLE
                    changeGoalProposition = Goal.FAT_LOSE
                    changeGoalName = getString(com.met.impilo.R.string.loss_title)

                }
                BodyRating.FAT_LOSE_OK -> {
                    body_rating_text_view.text = getString(com.met.impilo.R.string.fat_lose_ok)
                    change_goal.visibility = View.GONE
                }
                BodyRating.BULKING_UP_PROPOSITION -> {
                    body_rating_text_view.text = getString(com.met.impilo.R.string.bulking_up_proposition)
                    change_goal.visibility = View.VISIBLE
                    changeGoalProposition = Goal.MUSCLE_GAIN
                    changeGoalName = getString(com.met.impilo.R.string.bulking_title)

                }
                BodyRating.MAINTAIN_PROPOSITION -> {
                    body_rating_text_view.text = getString(com.met.impilo.R.string.maintain_proposition)
                    change_goal.visibility = View.VISIBLE
                    changeGoalProposition = Goal.MAINTAIN
                    changeGoalName = getString(com.met.impilo.R.string.maintain_title)
                }
                BodyRating.LOW_BODY_FAT -> body_rating_text_view.text = getString(com.met.impilo.R.string.low_body_fat)
            }
        })

        viewModel.fetchBF()


    }

    private fun initBfSeekbar() {

        bf_seekbar.setCustomSectionTextArray { _, array ->
            array.clear()
            array.put(1, getString(com.met.impilo.R.string.low_body_fat))
            array.put(3, getString(com.met.impilo.R.string.normal_body_fat))
            array.put(5, getString(com.met.impilo.R.string.increased_body_fat))
            array.put(7, getString(com.met.impilo.R.string.high_body_fat))

            array
        }

        bf_seekbar.onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
            override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar?, progress: Int, progressFloat: Float, fromUser: Boolean) {
                Log.i(TAG, "Slide : $progressFloat")
                val color: Int = when {
                    progressFloat <= 2f -> {
                        ContextCompat.getColor(context!!, com.met.impilo.R.color.proteinsColor)
                    }
                    progressFloat <= 4f -> {
                        ContextCompat.getColor(context!!, com.met.impilo.R.color.positiveGreen)
                    }
                    progressFloat <= 6f -> {
                        ContextCompat.getColor(context!!, com.met.impilo.R.color.moderateLevel)
                    }
                    progressFloat <= 8f -> {
                        ContextCompat.getColor(context!!, com.met.impilo.R.color.colorAccent)
                    }
                    else -> ContextCompat.getColor(context!!, com.met.impilo.R.color.colorAccent)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            Const.MY_OWN_PLAN_CREATOR_REQUEST -> {
                when(resultCode){
                    Activity.RESULT_OK -> {
                        val trainingPlanInfo = data?.getSerializableExtra("trainingPlanInfo") as TrainingPlanInfo

                        Log.d(TAG, "Received planInfo : $trainingPlanInfo")

                        viewModel.addAllTrainingDays(trainingPlanInfo){
                            if(it == 6)
                                viewModel.addTrainingPlanInfo(trainingPlanInfo){
                                    try {
                                        onEndOfConfigurationListener.onEndOfConfiguration()
                                        Log.e(TAG, "Ending config - start listener")

                                    } catch (e : Exception){
                                        Log.e(TAG, "Interface OnEndOfConfigurationListener must be initialized !")
                                    }
                                }
                        }
                    }
                }
            }
        }
    }


}
