package com.met.auth.registration.configuration


import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.met.auth.R
import com.met.auth.registration.Registration
import com.met.impilo.data.Gender
import com.met.impilo.data.Somatotype
import kotlinx.android.synthetic.main.fragment_somatotype.*
import me.rishabhkhanna.customtogglebutton.CustomToggleButton


class SomatotypeFragment : BaseFragment(), Registration.OnPageChangeListener {

    private lateinit var selectedSomatotype: Somatotype
    private lateinit var gender: Gender

    companion object {
        @JvmStatic
        fun newInstance() = SomatotypeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_somatotype, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        selectedSomatotype = Somatotype.MESOMORPH
        gender = Gender.MALE

        somatotype_image_switcher.inAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        somatotype_image_switcher.outAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)

        somatotype_image_switcher.setFactory {
            val myView = ImageView(context)
            myView.scaleType = ImageView.ScaleType.FIT_CENTER
            myView.layoutParams = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            myView
        }

        toggleClickListeners()
    }

    fun setGender(gender: Gender?) {
        this.gender = gender ?: Gender.MALE
        this.selectedSomatotype = null ?: Somatotype.MESOMORPH

        changeSomatotypeImage(selectedSomatotype)
    }

    private fun toggleClickListeners() {
        ectomorph_toggle.setOnClickListener {
            if (ectomorph_toggle.isChecked) setEnabledToggleButton(endomorph_toggle, false, Color.GRAY)
            else setEnabledToggleButton(endomorph_toggle, true, Color.WHITE)

            selectedSomatotype = if (mesomorph_toggle.isChecked) {
                if (ectomorph_toggle.isChecked) Somatotype.MESO_ECTOMORPH
                else Somatotype.MESOMORPH
            } else Somatotype.ECTOMORPH

            changeSomatotypeImage(selectedSomatotype)
        }

        mesomorph_toggle.setOnClickListener {
            if (ectomorph_toggle.isChecked && !endomorph_toggle.isChecked) {
                selectedSomatotype = if (mesomorph_toggle.isChecked) Somatotype.ECTO_MESOMORPH
                else Somatotype.ECTOMORPH
            } else if (!ectomorph_toggle.isChecked && !endomorph_toggle.isChecked) selectedSomatotype = Somatotype.MESOMORPH

            if (endomorph_toggle.isChecked && !ectomorph_toggle.isChecked) {
                selectedSomatotype = if (mesomorph_toggle.isChecked) Somatotype.ENDO_MESOMORPH
                else Somatotype.ENDOMORPH
            } else if (!endomorph_toggle.isChecked && !ectomorph_toggle.isChecked) selectedSomatotype = Somatotype.MESOMORPH

            changeSomatotypeImage(selectedSomatotype)
        }

        endomorph_toggle.setOnClickListener {
            if (endomorph_toggle.isChecked) setEnabledToggleButton(ectomorph_toggle, false, Color.GRAY)
            else setEnabledToggleButton(ectomorph_toggle, true, Color.WHITE)

            selectedSomatotype = if (mesomorph_toggle.isChecked) {
                if (endomorph_toggle.isChecked) Somatotype.MESO_ENDOMORPH
                else Somatotype.MESOMORPH
            } else Somatotype.ENDOMORPH

            changeSomatotypeImage(selectedSomatotype)
        }
    }

    private fun setEnabledToggleButton(toggle: CustomToggleButton, enabled: Boolean, color: Int) {
        toggle.isEnabled = enabled
        toggle.backgroundTintList = ColorStateList.valueOf(color)
        if (!enabled) {
            val colorStateList = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked)), intArrayOf(Color.GRAY, Color.GRAY))
            toggle.setTextColor(colorStateList)
        } else {
            val colorStateList = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked)),
                intArrayOf(ContextCompat.getColor(context!!, com.met.impilo.R.color.colorAccent), Color.WHITE))
            toggle.setTextColor(colorStateList)
        }
    }

    private fun changeSomatotypeImage(somatotype: Somatotype) {
        var imageResId: Int = R.drawable.man_mesomorph
        when (somatotype) {
            Somatotype.ECTOMORPH -> {
                imageResId = if (gender == Gender.MALE) R.drawable.man_ectomorph
                else R.drawable.woman_ecto

                somatotype_textview.text = resources.getString(com.met.impilo.R.string.ectomorph).toUpperCase()
            }
            Somatotype.ENDOMORPH -> {
                imageResId = if(gender == Gender.MALE) R.drawable.man_endomorph
                else R.drawable.woman_endo

                somatotype_textview.text = resources.getString(com.met.impilo.R.string.endomorph).toUpperCase()
            }
            Somatotype.MESOMORPH -> {
                imageResId = if(gender == Gender.MALE) R.drawable.man_mesomorph
                else R.drawable.woman_meso

                somatotype_textview.text = resources.getString(com.met.impilo.R.string.mesomorph).toUpperCase()
            }
            Somatotype.ECTO_MESOMORPH -> {
                imageResId = if(gender == Gender.MALE) R.drawable.man_ecto_mesomorph
                else R.drawable.woman_ecto_meso

                somatotype_textview.text = resources.getString(com.met.impilo.R.string.ecto_mesomorph).toUpperCase()
            }
            Somatotype.ENDO_MESOMORPH -> {
                imageResId = if(gender == Gender.MALE) R.drawable.man_endo_mesomorph
                else R.drawable.woman_endo_meso

                somatotype_textview.text = resources.getString(com.met.impilo.R.string.endo_mesomorph).toUpperCase()
            }
            Somatotype.MESO_ECTOMORPH -> {
                imageResId = if(gender == Gender.MALE) R.drawable.man_meso_ectomorph
                else R.drawable.woman_meso_ecto

                somatotype_textview.text = resources.getString(com.met.impilo.R.string.meso_ectomorph).toUpperCase()
            }
            Somatotype.MESO_ENDOMORPH -> {
                imageResId = if(gender == Gender.MALE) R.drawable.man_meso_endomorph
                else R.drawable.woman_meso_endo

                somatotype_textview.text = resources.getString(com.met.impilo.R.string.meso_endomorph).toUpperCase()
            }
        }
        somatotype_image_switcher.setImageResource(imageResId)
    }

    override fun onNextPageClick(): Boolean {
        callback.somatotype(selectedSomatotype)
        return true
    }

}
