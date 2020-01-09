package com.met.diet


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.met.diet.adapter.ProductDetailsAdapter
import com.met.impilo.data.food.FoodProduct
import com.met.impilo.data.food.ServingType
import kotlinx.android.synthetic.main.fragment_product_bottom_sheet.view.*
import kotlinx.android.synthetic.main.product_bottom_sheet_content.view.*


class ProductBottomSheet(val isLightMode: Boolean) : BottomSheetDialogFragment() {

    private val TAG = javaClass.simpleName

    lateinit var onAddProductListener : OnAddProductListener
    private lateinit var behavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetView : View
    private lateinit var detailsAdapter: ProductDetailsAdapter
    var servingType = ServingType.PORTION
    var servingSize = 1f
    var shouldBeExpanded = false

    private var offset: Float = 670f
    private var isAnyChipChecked = false

    lateinit var product: FoodProduct

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet: BottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetView = View.inflate(context, R.layout.fragment_product_bottom_sheet, null)

        bottomSheet.setContentView(bottomSheetView)
        behavior = BottomSheetBehavior.from(bottomSheetView.parent as View)

        behavior.peekHeight = (((Resources.getSystem().displayMetrics.heightPixels) / 2.5).toInt())

        bottomSheetView.minimumHeight = ((Resources.getSystem().displayMetrics.heightPixels) / 3)
        bottomSheetView.layoutParams.height = (Resources.getSystem().displayMetrics.heightPixels)

        bottomSheetView.bottom_sheet_content.translationY = -offset


        bottomSheetView.serving_size_input_edit_text.isEnabled = false
        bottomSheetView.serving_type_spinner.isEnabled = false

        bottomSheetView.product_title.text = product.name

        bottomSheetView.serving_type_spinner.onItemSelectedListener = getSpinnerListener(bottomSheetView)
        bottomSheetView.serving_size_input_edit_text.addTextChangedListener(getServingSizeWatcher(bottomSheetView))

        val servingTypeAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_dropdown_item, listOf(product.servingName, "GRAM"))
        bottomSheetView.serving_type_spinner.adapter = servingTypeAdapter

        behavior.addBottomSheetCallback(getBottomSheetBehavior())

        bottomSheetView.cancel_action.setOnClickListener {
            if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) behavior.state = BottomSheetBehavior.STATE_EXPANDED
            else behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetView.add_product_action.setOnClickListener {
            val calculatedProduct = product
            calculatedProduct.nutrients = product.getCaluclatedNutrients(servingSize, servingType)

            onAddProductListener.onAddProduct(calculatedProduct, servingSize, servingType)
        }

        loadPhoto(bottomSheetView)
        initProductDetailsBottomSheet(bottomSheetView)
        initChipGroup(bottomSheetView)
        updateNutrientsInfo(bottomSheetView)

        return bottomSheet
    }


    private fun initProductDetailsBottomSheet(view: View) {
        detailsAdapter = ProductDetailsAdapter(context!!, product.nutrients!!, product.getCaluclatedNutrients(servingSize, servingType)!!)
        view.product_details_recycler_view.layoutManager = LinearLayoutManager(context!!)
        view.product_details_recycler_view.adapter = detailsAdapter
    }

    @SuppressLint("DefaultLocale")
    private fun initChipGroup(view: View) {
        initChip(view.half_chip, "0.5×" + product.servingName.capitalize())
        initChip(view.one_chip, "1×" + product.servingName.capitalize())
        initChip(view.two_chip, "2×" + product.servingName.capitalize())


        view.chip_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                -1 -> {
                    view.serving_size_input_edit_text.isEnabled = true
                    view.serving_type_spinner.isEnabled = true

                    if (view.serving_size_input_edit_text.text.toString().isNotEmpty()) servingSize = view.serving_size_input_edit_text.text.toString().toFloat()
                    else view.serving_size_input_edit_text.setText("1")

                    servingType = if (view.serving_type_spinner.selectedItemPosition == 0) {
                        ServingType.PORTION
                    } else {
                        ServingType.GRAM
                    }

                    isAnyChipChecked = false
                    updateNutrientsInfo(view)
                }
                R.id.half_chip -> {
                    checkChip(view, 0.5f)
                }
                R.id.one_chip -> {
                    checkChip(view, 1f)
                }
                R.id.two_chip -> {
                    checkChip(view, 2f)
                }
            }
        }
    }

    private fun getBottomSheetBehavior() = object : BottomSheetCallback() {
        override fun onStateChanged(view: View, i: Int) {
            when {
                BottomSheetBehavior.STATE_HIDDEN == i -> dismiss()
                BottomSheetBehavior.STATE_COLLAPSED == i -> {
                    view.serving_size_input_edit_text.isEnabled = false
                    view.serving_type_spinner.isEnabled = false
                }
                else -> {
                    if (!isAnyChipChecked) {
                        view.serving_size_input_edit_text.isEnabled = true
                        view.serving_type_spinner.isEnabled = true
                    }
                }
            }
        }

        override fun onSlide(view: View, v: Float) {
            slide(v)
        }
    }

    private fun slide( v: Float) {
        setAlpha(bottomSheetView.product_photo, v)
        setAlpha(bottomSheetView.app_bar_background, v)
        setAlpha(bottomSheetView.quick_info_layout, 1 - v)
        setAlpha(bottomSheetView.details_header_layout, v)
        setAlpha(bottomSheetView.product_details_recycler_view, v)
        setAlpha(bottomSheetView.divider1, v)
        setAlpha(bottomSheetView.serving_size_text_input_layout, v)
        setAlpha(bottomSheetView.serving_type_spinner, v)
        setTranslationY(bottomSheetView.bottom_sheet_content, v)
        bottomSheetView.cancel_action.rotation = v * 180
        if (isLightMode && v > 0.0f) {
            bottomSheetView.product_title.setTextColor(Color.rgb((255 * v).toInt(), (255 * v).toInt(), (255 * v).toInt()))
            bottomSheetView.cancel_action.imageTintList = ColorStateList.valueOf(Color.rgb((255 * v).toInt(), (255 * v).toInt(), (255 * v).toInt()))
            bottomSheetView.add_product_action.imageTintList = ColorStateList.valueOf(Color.rgb((255 * v).toInt(), (255 * v).toInt(), (255 * v).toInt()))
        }
    }

    private fun getSpinnerListener(view: View) = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
            when (position) {
                0 -> {
                    servingType = ServingType.PORTION
                    updateNutrientsInfo(view)
                }
                1 -> {
                    servingType = ServingType.GRAM
                    updateNutrientsInfo(view)
                }
            }
        }
    }

    private fun getServingSizeWatcher(view: View) = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s.toString().isNotEmpty()) {
                servingSize = s.toString().toFloat()
                updateNutrientsInfo(view)
                Log.e(TAG, "Typed : $s")
            }
            Log.e(TAG, "After changed $s")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private fun checkChip(view: View, value: Float) {
        servingSize = value
        servingType = ServingType.PORTION
        updateNutrientsInfo(view)
        view.serving_size_input_edit_text.isEnabled = false
        view.serving_type_spinner.isEnabled = false
        isAnyChipChecked = true
    }

    @SuppressLint("DefaultLocale")
    private fun updateNutrientsInfo(view: View) {
        val calculatedNutrients = product.getCaluclatedNutrients(servingSize, servingType)

        view.energy_quick_info.text = StringBuilder(calculatedNutrients?.energyKcal.toString() + " kcal")
        view.carbo_quick_info.text = StringBuilder(calculatedNutrients?.carbo.toString() + " g")
        view.proteins_quick_info.text = StringBuilder(calculatedNutrients?.protein.toString() + " g")
        view.fats_quick_info.text = StringBuilder(calculatedNutrients?.fats.toString() + " g")

        if (servingType == ServingType.PORTION) view.portion_header.text = StringBuilder("$servingSize × ${product.servingName.toUpperCase()}")
        else view.portion_header.text = StringBuilder("$servingSize g")

        detailsAdapter.updateData(calculatedNutrients!!)
        detailsAdapter.notifyDataSetChanged()
    }

    private fun loadPhoto(view: View) {
        view.lottie_anim.playAnimation()

        if (product.photoUri != "") {
            Glide.with(this).asBitmap().load(Uri.parse(product.photoUri)).into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                    Glide.with(this@ProductBottomSheet).load(resource).placeholder(ContextCompat.getDrawable(context!!, R.drawable.product_place_holder))
                        .transition(DrawableTransitionOptions.withCrossFade()).into(view.product_photo)
                    view.lottie_anim.cancelAnimation()
                    view.lottie_anim.visibility = View.GONE
                }

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                    view.product_photo.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.product_place_holder))
                }
            })
        } else {
            view.product_photo.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.product_place_holder))
        }
    }

    private fun initChip(chip: Chip, text: String) {
        chip.apply {
            isCheckable = true
            chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, com.met.impilo.R.color.colorAccent))
            setTextColor(Color.WHITE)
            setText(text)
            textSize = 12f
        }
    }

    /**
     * Zostala nadpisana metoda show() aby aktywnsoc nie zapisywala stanu (commitAllowinStateLoss)
     */
    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val transaction = manager.beginTransaction()
            transaction.add(this, tag)
            transaction.commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        product = FoodProduct()
        view?.product_photo?.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.product_place_holder))

    }

    override fun onStart() {
        super.onStart()
        if(shouldBeExpanded) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetView.serving_size_input_edit_text.isEnabled = true
            bottomSheetView.serving_type_spinner.isEnabled = true
            bottomSheetView.serving_size_input_edit_text.setText(servingSize.toInt().toString())
            slide(1f)
        } else
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun setAlpha(view: View, alpha: Float) {
        view.alpha = alpha
    }

    private fun setTranslationY(view: View, position: Float) {
        view.translationY = ((position * offset) - offset)
    }

    interface OnAddProductListener {
        fun onAddProduct(foodProduct: FoodProduct, servingSize : Float, servingType: ServingType)
    }
}
