package com.jeremykruid.lawndemand.ui.ordering

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.jeremykruid.lawndemand.R
import com.jeremykruid.lawndemand.functions.DoMath
import com.jeremykruid.lawndemand.functions.HandleStrings
import com.jeremykruid.lawndemand.model.PropertyObject
import kotlin.math.roundToInt

class QuoteFragment : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private lateinit var thisView: View
    private lateinit var property: PropertyObject

    private lateinit var homeImg: ImageView
    private lateinit var streetAddress: TextView
    private lateinit var city: TextView
    private lateinit var state: TextView
    private lateinit var zip: TextView
    private lateinit var lotSize: TextView
    private lateinit var lawnQuoteBtn: TextView
    private lateinit var topProviderCheckBox: CheckBox

    private var topProvider = false

    private var lot: Int = 0

    private var measurement = ""
    private var price: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_quote, container, false)
        
        if (arguments != null){
            property = arguments?.get("property") as PropertyObject
            initViews()
            measurement = HandleStrings().getLotMeasurementType(property.resoFacts!!.lotSize)
            if (measurement == "sqft") {
                lot = HandleStrings().getLotSfInt(property.resoFacts!!.lotSize)
                price = DoMath().getPrice(lot)
                lawnQuoteBtn.text = getString(R.string.mow_my_lawn, DoMath().convertToDollars(price))

            }else if (measurement == "Acres"){
                lot = HandleStrings().getAcresToSfInt(property.resoFacts!!.lotSize)
                price = DoMath().getPrice(lot)
                lawnQuoteBtn.text = getString(R.string.mow_my_lawn, DoMath().convertToDollars(price))
            }
        }else{
            Toast.makeText(requireContext(), "Try Again", Toast.LENGTH_SHORT).show()
        }
        return thisView
    }

    private fun initViews() {
        homeImg = thisView.findViewById(R.id.quote_image)
        Glide.with(requireActivity()).load(property.imgSrc).into(homeImg)
        streetAddress = thisView.findViewById(R.id.quote_street)
        streetAddress.text = property.address?.streetAddress
        city = thisView.findViewById(R.id.quote_city)
        city.text = property.address?.city
        state = thisView.findViewById(R.id.quote_state)
        state.text = property.address?.state
        zip = thisView.findViewById(R.id.quote_zip)
        zip.text = property.address?.zipcode
        lotSize = thisView.findViewById(R.id.quote_lot_size)
        lotSize.text = property.resoFacts?.lotSize
        lawnQuoteBtn = thisView.findViewById(R.id.quote_price_btn)
        lawnQuoteBtn.setOnClickListener(this)

        topProviderCheckBox = thisView.findViewById(R.id.quote_top_provider_check_box)
        topProviderCheckBox.setOnCheckedChangeListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            lawnQuoteBtn ->{
                val bundle = bundleOf(
                    Pair("property", property),
                    Pair("price", price),
                    Pair("lot", lot),
                    Pair("topProvider", topProvider)
                )
                findNavController().navigate(R.id.action_quoteFragment_to_confirmPurchaseFragment, bundle)
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView){
            topProviderCheckBox -> {
                if (topProviderCheckBox.isChecked){
                    topProvider = true
                    price = (price * 1.25 * 100).roundToInt()/ 100.toDouble()
                    lawnQuoteBtn.text = getString(R.string.mow_my_lawn, DoMath().convertToDollars(price))
                }else {
                    topProvider = false
                    if (measurement == "sqft") {
                        lot = HandleStrings().getLotSfInt(property.resoFacts!!.lotSize)
                        price = DoMath().getPrice(lot)
                        lawnQuoteBtn.text = getString(R.string.mow_my_lawn, DoMath().convertToDollars(price))

                    }else if (measurement == "Acres"){
                        lot = HandleStrings().getAcresToSfInt(property.resoFacts!!.lotSize)
                        price = DoMath().getPrice(lot)
                        lawnQuoteBtn.text = getString(R.string.mow_my_lawn, DoMath().convertToDollars(price))
                    }
                }
            }
        }
    }
}