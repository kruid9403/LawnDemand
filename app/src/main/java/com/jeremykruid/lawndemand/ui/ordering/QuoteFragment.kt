package com.jeremykruid.lawndemand.ui.ordering

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.jeremykruid.lawndemand.R
import com.jeremykruid.lawndemand.databinding.FragmentQuoteBinding
import com.jeremykruid.lawndemand.functions.DoMath
import com.jeremykruid.lawndemand.functions.HandleStrings
import com.jeremykruid.lawndemand.model.PropertyObject
import kotlin.math.roundToInt

class QuoteFragment : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private lateinit var binding: FragmentQuoteBinding
    private lateinit var viewModel: QuoteViewModel
    private lateinit var property: PropertyObject

    private var topProvider = false

    private var lot: Int = 0

    private var measurement = ""
    private var price: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentQuoteBinding.inflate(layoutInflater)
        binding.quotePb.visibility = View.VISIBLE
        viewModel = ViewModelProvider(this).get(QuoteViewModel::class.java)

        if (arguments != null){
            property = arguments?.get("property") as PropertyObject
            measurement = HandleStrings().getLotMeasurementType(property.resoFacts!!.lotSize)
            if (measurement == "sqft") {
                lot = HandleStrings().getLotSfInt(property.resoFacts!!.lotSize)

            }else if (measurement == "Acres"){
                lot = HandleStrings().getAcresToSfInt(property.resoFacts!!.lotSize)
            }

            initViews()

        }else{
            Toast.makeText(requireContext(), "Try Again", Toast.LENGTH_SHORT).show()
        }

        viewModel.getPrices(lot, false)
        viewModel.rates.observe(viewLifecycleOwner){
            if (it != null) {
                binding.quotePb.visibility = View.GONE
                price = it
                setPrices()
                Log.e("QuoteFrag", price.toString())
            }else{
                Log.e("QuoteFrag", "Null rate")
            }
        }

        return binding.root
    }

    private fun setPrices(){

        binding.quoteOneTimeServiceBtn.text = getString(R.string.mow_my_lawn, DoMath().convertToDollars(price))
    }

    private fun initViews() {
        Glide.with(requireActivity()).load(property.imgSrc).into(binding.quoteImage)
        binding.quoteOneTimeServiceBtn.setOnClickListener(this)

        binding.quoteTopProviderCheckBox.setOnCheckedChangeListener(this)
        binding.quoteStreet.text = property.address?.streetAddress
        binding.quoteCity.text = property.address?.city
        binding.quoteState.text = property.address?.state
        binding.quoteZip.text = property.address?.zipcode
        binding.quoteLotSize.text = property.resoFacts?.lotSize
        binding.quoteOneTimeServiceBtn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v){
            binding.quoteOneTimeServiceBtn ->{
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
            binding.quoteTopProviderCheckBox -> {
                if (binding.quoteTopProviderCheckBox.isChecked){
                    topProvider = true
                    lot = HandleStrings().getLotSfInt(property.resoFacts!!.lotSize)
                    viewModel.getPrices(lot, topProvider)
                    binding.quotePb.visibility = View.VISIBLE
//                    price = (price * 1.25 * 100).roundToInt()/ 100.toDouble()
//                    binding.quoteOneTimeServiceBtn.text = getString(R.string.mow_my_lawn, DoMath().convertToDollars(price))
                }else {
                    topProvider = false
                    if (measurement == "sqft") {
                        lot = HandleStrings().getLotSfInt(property.resoFacts!!.lotSize)
                        viewModel.getPrices(lot, topProvider)
                        binding.quotePb.visibility = View.VISIBLE
//                        binding.quoteOneTimeServiceBtn.text = getString(R.string.mow_my_lawn, DoMath().convertToDollars(price))

                    }else if (measurement == "Acres"){
                        lot = HandleStrings().getAcresToSfInt(property.resoFacts!!.lotSize)
                        viewModel.getPrices(lot, topProvider)
                        binding.quotePb.visibility = View.VISIBLE
//                        binding.quoteOneTimeServiceBtn.text = getString(R.string.mow_my_lawn, DoMath().convertToDollars(price))
                    }
                }
            }
        }
    }
}