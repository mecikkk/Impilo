package com.met.diet

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.github.pappin.mbs.MaterialBarcodeScannerBuilder
import com.met.diet.adapter.SearchActivityAdapter
import com.met.impilo.data.food.FoodProduct
import com.met.impilo.data.food.ServingType
import com.met.impilo.utils.Constants
import com.met.impilo.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity(), SearchActivityAdapter.OnProductClickListener, ProductBottomSheet.OnAddProductListener {

    lateinit var viewModel: SearchActivityViewModel
    private val TAG = javaClass.simpleName
    private var productSheet : ProductBottomSheet? = null
    private var isLightMode = false
    private var selectedMeal = -1
    private var mealName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        selectedMeal = intent.getIntExtra(Constants.MEAL_ID_REQUEST, -1)
        mealName = intent.getStringExtra(Constants.MEAL_NAME_REQUEST) ?: ""

        viewModel = ViewModelProviders.of(this).get(SearchActivityViewModel::class.java)

        setSupportActionBar(toolbar)
        supportActionBar?.setShowHideAnimationEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        isLightMode = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> true
            Configuration.UI_MODE_NIGHT_YES -> false
            else -> false
        }

        productSheet = ProductBottomSheet(isLightMode)

        val adapter: SearchActivityAdapter? = SearchActivityAdapter(listOf(), this)

        search_list_view.adapter = adapter

        search_input_edit_text.addTextChangedListener(initTextWatcher(adapter))

        adapter?.setOnProductClickListener(this)

        scan_barcode_action.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), Constants.CAMERA_REQUEST)
            } else {
                val materialBarcodeScanner =
                    MaterialBarcodeScannerBuilder().withActivity(this@SearchActivity)
                        .withEnableAutoFocus(true)
                        .withBleepEnabled(true)
                        .withBackfacingCamera()
                        .withCenterTracker(R.drawable.barcode_tracker, R.drawable.barcode_tracker_detected)
                        .withText(getString(R.string.scanning)).withResultListener { barcode ->
                            Log.e(TAG, "Scanned ${barcode.rawValue}")
                            viewModel.getProductByBarcode(barcode.rawValue) {
                                Log.e(TAG, "Response from firebase ")

                                if(it != null) {
                                    productSheet?.product = it
                                    productSheet?.show(supportFragmentManager, productSheet?.tag)
                                    productSheet?.product = it
                                    productSheet?.onAddProductListener = this@SearchActivity
                                } else {
                                    ViewUtils.createSnackbar(search_activity_content, getString(com.met.impilo.R.string.product_not_found)).show()
                                }
                            }
                        }.build()
                materialBarcodeScanner.startScan()
            }
        }
    }



    private fun initTextWatcher(adapter: SearchActivityAdapter?): TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            Log.e("WAtcher", "size : $count")
            progress.visibility = View.VISIBLE

            if (s.isNullOrEmpty()) progress.visibility = View.GONE

            if (s != null && count >= 2) {
                viewModel.searchProduct(s.toString()) {
                    progress.visibility = View.GONE
                    adapter!!.productsNamesFiltered = it
                    adapter.notifyDataSetChanged()
                }
            } else {
                adapter!!.productsNamesFiltered = listOf()
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.CAMERA_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ViewUtils.createSnackbar(search_activity_content, getString(com.met.impilo.R.string.camera_perm_granted)).show()
            } else {
                ViewUtils.createSnackbar(search_activity_content, getString(com.met.impilo.R.string.camera_perm_denied)).show()
            }
        }
    }

    override fun onProductClick(foodProduct: FoodProduct) {
//        productSheet?.dismiss()
//        productSheet = ProductBottomSheet(isLightMode)
        productSheet?.product = foodProduct
        productSheet?.show(supportFragmentManager, productSheet?.tag)
        productSheet?.product = foodProduct
        productSheet?.onAddProductListener = this@SearchActivity
    }

    override fun onAddProduct(foodProduct: FoodProduct, servingSize : Float, servingType: ServingType) {
        productSheet?.dismiss()
        viewModel.addProductToMeal(foodProduct, servingSize, servingType, selectedMeal, mealName){
            if(it) {
                ViewUtils.createSnackbar(search_activity_content, "Product added to meal").show()
                finish()
            } else
                ViewUtils.createSnackbar(search_activity_content, "Adding product error").show()
        }
    }

}
