package com.jeremykruid.lawndemand.ui.properties

import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.jeremykruid.lawndemand.R
import com.jeremykruid.lawndemand.model.PropertyObject
import com.jeremykruid.lawndemand.model.ZpidObject
import okhttp3.*
import java.io.IOException
import java.util.*

class NewPropertyFragment : Fragment(), View.OnClickListener {

    private lateinit var thisView: View
    private lateinit var gpsBtn: ImageView
    private lateinit var streetEdit: EditText
    private lateinit var cityEdit: EditText
    private lateinit var stateEdit: EditText
    private lateinit var zipEdit: EditText
    private lateinit var detailsBtn: Button
    private lateinit var pb: ProgressBar
    private lateinit var handler: Handler
    private lateinit var propertyImg: ImageView
    private lateinit var streetVerify: TextView
    private lateinit var cityVerify: TextView
    private lateinit var stateVerify: TextView
    private lateinit var zipVerify: TextView
    private lateinit var lotVerify: TextView
    private lateinit var setPropertyBtn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var finalProperty: PropertyObject? = null

    private val locationPermission: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if (isGranted){
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

                fusedLocationClient.lastLocation.addOnCompleteListener {
                    if (it.isSuccessful){
                        lat = it.result.latitude
                        lon = it.result.longitude

                        updateUI()
                    }
                }

            }else{
                Toast.makeText(requireContext(), "Location Permissions Required", Toast.LENGTH_SHORT).show()
            }
        }

    private fun updateUI() {
        val address = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(lat, lon, 5)
        val split = address[0].getAddressLine(0).split(",")
        streetEdit.setText(split[0])
        cityEdit.setText(split[1])
        val stateZip = split[2].split(" ")
        stateEdit.setText(stateZip[1])
        zipEdit.setText(stateZip[2])
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_new_property, container, false)

        initViews()

        handler = Handler(Looper.myLooper()!!)

        return thisView
    }

    private fun getAddress() {
        locationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun initViews() {
        gpsBtn = thisView.findViewById(R.id.new_property_gps_btn)
        gpsBtn.setOnClickListener(this)

        streetEdit = thisView.findViewById(R.id.new_property_street)
        cityEdit = thisView.findViewById(R.id.new_property_city)
        stateEdit = thisView.findViewById(R.id.new_property_state)
        zipEdit = thisView.findViewById(R.id.new_property_zip)
        detailsBtn = thisView.findViewById(R.id.new_property_details_btn)
        detailsBtn.setOnClickListener(this)

        pb = thisView.findViewById(R.id.new_property_pb)

        streetVerify = thisView.findViewById(R.id.new_property_street_verify)
        cityVerify = thisView.findViewById(R.id.new_property_city_verify)
        stateVerify = thisView.findViewById(R.id.new_property_state_verify)
        zipVerify = thisView.findViewById(R.id.new_property_zip_verify)
        lotVerify = thisView.findViewById(R.id.new_property_lot_size_verify)
        propertyImg = thisView.findViewById(R.id.new_property_image)

        setPropertyBtn = thisView.findViewById(R.id.new_property_set_property_btn)
    }

    override fun onClick(v: View?) {
        when(v){
            gpsBtn -> {
                getAddress()
            }
            detailsBtn ->{
                pb.visibility = View.VISIBLE
                getZid()
            }
            setPropertyBtn -> {
                if (finalProperty != null){
                    uploadProperty()
                }
            }
        }
    }

    private fun uploadProperty() {
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null && finalProperty?.resoFacts?.lotSize != "null" && finalProperty?.resoFacts?.lotSize != null){
            finalProperty?.let {
                Firebase.firestore.collection("customers")
                    .document(auth.uid.toString()).collection("properties").add(finalProperty!!)
            }

            thisView.findNavController().navigate(R.id.action_newPropertyFragment_to_homeFragment)
        }else if(finalProperty?.resoFacts?.lotSize == null || finalProperty?.resoFacts?.lotSize == "null") {
            Toast.makeText(requireContext(), "Data Not Available", Toast.LENGTH_SHORT).show()
        }else{
            findNavController().navigate(R.id.action_newPropertyFragment_to_logInFragment)
        }

    }

    private fun getZid() {
        
        if (streetEdit.text.toString() != "" && cityEdit.text.toString() != "" && 
            stateEdit.text.toString() != "" && zipEdit.text.toString() != "") {

            val client = OkHttpClient()

            val url = "https://zillow-com1.p.rapidapi.com/propertyExtendedSearch?location=${streetEdit.text.toString().trim()} " +
                    "${cityEdit.text.toString().trim()} ${stateEdit.text.toString().trim()} ${zipEdit.text.toString().trim()}"

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-key", "dd6cc4764fmsh2439a087ed3c1c8p1f4cabjsnc321a4f933d4")
                .addHeader("x-rapidapi-host", "zillow-com1.p.rapidapi.com")
                .build()

            client.newCall(request).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("error", e.localizedMessage!!)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = Gson().fromJson(response.body?.string(), ZpidObject::class.java)
                    val id = result.zpid
                    getPropertyData(id)
                }

            })


        }else{
            Toast.makeText(requireContext(), "All Fields Are Required", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getPropertyData(id: Int) {
        val client = OkHttpClient()
        val url = "https://zillow-com1.p.rapidapi.com/property?zpid=$id"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("x-rapidapi-key", "0537271c3emsh151294e8d1ef973p132cbfjsn648271bdb627")
            .addHeader("x-rapidapi-host", "zillow-com1.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", e.localizedMessage!!)

            }

            override fun onResponse(call: Call, response: Response) {
                val result = Gson().fromJson(response.body?.string(), PropertyObject::class.java)
                if (result.resoFacts?.lotSize != null) {
                    result.resoFacts?.let { Log.e("result", it.lotSize) }
                    handler.post {
                        pb.visibility = View.GONE
                        setResults(result)
                    }
                }else{
                    handler.post{
                        pb.visibility = View.GONE
                        Toast.makeText(requireContext(), "Missing Data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun setResults(property: PropertyObject?) {
        Glide.with(requireActivity()).load(property?.imgSrc).into(propertyImg)
        streetVerify.text = property?.address?.streetAddress ?: "No Data"
        cityVerify.text = property?.address?.city ?: "No Data"
        stateVerify.text = property?.address?.state ?: "No Data"
        zipVerify.text = property?.address?.zipcode ?: "No Data"
        lotVerify.text = property?.resoFacts?.lotSize ?: "No Data"

        setPropertyBtn.setOnClickListener(this)

        finalProperty = property
    }
}