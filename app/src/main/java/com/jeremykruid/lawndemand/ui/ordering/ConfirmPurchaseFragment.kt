package com.jeremykruid.lawndemand.ui.ordering

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jeremykruid.lawndemand.R
import com.jeremykruid.lawndemand.functions.DoMath
import com.jeremykruid.lawndemand.model.PropertyObject
import com.jeremykruid.lawndemand.functions.stripe.FirebaseEphemeralKeyProvider
import com.jeremykruid.lawndemand.model.OrderObject
import com.stripe.android.*
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.StripeIntent
import com.stripe.android.view.BillingAddressFields

class ConfirmPurchaseFragment : Fragment(), CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private lateinit var thisView: View
    private lateinit var property: PropertyObject
    private lateinit var streetAddress: TextView
    private lateinit var priceTextView: TextView
    private lateinit var grassCheck: CheckBox
    private lateinit var trimCheck: CheckBox
    private lateinit var edgeCheck: CheckBox
    private lateinit var blowCheck: CheckBox
    private lateinit var petCheck: CheckBox
    private lateinit var lockCheck: CheckBox
    private lateinit var selectPayment: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var documentId: String
    private lateinit var progressBar: ProgressBar

    private var lot: Int = 0
    private var topProvider: Boolean = false

    private var price: Double = 0.0

    private lateinit var paymentSession: PaymentSession
    private lateinit var selectedPaymentMethod: PaymentMethod
    private val stripe: Stripe by lazy { Stripe(requireContext(),
        "pk_test_51J72l1EILgHcZBocdTJYTznoG8moNuURFXyjXrSBDxwD30C9ADyvtBt9XybV4M4oZNTOrw0Jcfpx7wKxsIkH1Neh003mgzkXcM") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PaymentConfiguration.init(requireContext(),
            "pk_test_51J72l1EILgHcZBocdTJYTznoG8moNuURFXyjXrSBDxwD30C9ADyvtBt9XybV4M4oZNTOrw0Jcfpx7wKxsIkH1Neh003mgzkXcM")

        CustomerSession.initCustomerSession(requireContext(), FirebaseEphemeralKeyProvider())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_confirm_purchase, container, false)

        if (arguments != null){
            property = arguments?.get("property") as PropertyObject
            lot = arguments?.get("lot") as Int
            price = arguments?.get("price") as Double
            topProvider = arguments?.get("topProvider") as Boolean
            
            initViews()

            checkAuth()

        }else{
            Toast.makeText(requireContext(), "Try Again", Toast.LENGTH_SHORT).show()
        }

        return thisView
    }

    private fun checkAuth() {
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            uid = auth.uid.toString()
        }
    }

    private fun initViews() {
        streetAddress = thisView.findViewById(R.id.confirm_address)
        streetAddress.text = property.address?.streetAddress

        priceTextView = thisView.findViewById(R.id.confirm_price)
        priceTextView.text = DoMath().convertToDollars(price)

        grassCheck = thisView.findViewById(R.id.confirm_grass)
        grassCheck.setOnCheckedChangeListener(this)

        trimCheck = thisView.findViewById(R.id.confirm_purchase_trimming)
        trimCheck.setOnCheckedChangeListener(this)

        edgeCheck = thisView.findViewById(R.id.confirm_purchase_edging)
        edgeCheck.setOnCheckedChangeListener(this)

        blowCheck = thisView.findViewById(R.id.confirm_purchase_blow)
        blowCheck.setOnCheckedChangeListener(this)

        petCheck = thisView.findViewById(R.id.confirm_purchase_pets)
        petCheck.setOnCheckedChangeListener(this)

        lockCheck = thisView.findViewById(R.id.confirm_purchase_locks)
        lockCheck.setOnCheckedChangeListener(this)

        selectPayment = thisView.findViewById(R.id.confirm_select_payment)

        progressBar = thisView.findViewById(R.id.confirm_progress)
        progressBar.visibility = View.GONE
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView){
            grassCheck -> {
                checksCompleted()
            }
            trimCheck ->{
                checksCompleted()
            }
            edgeCheck ->{
                checksCompleted()
            }
            blowCheck -> {
                checksCompleted()
            }
            petCheck -> {
                checksCompleted()
            }
            lockCheck -> {
                checksCompleted()
            }
        }
    }

    private fun checksCompleted() {
        if (grassCheck.isChecked && trimCheck.isChecked && edgeCheck.isChecked && blowCheck.isChecked &&
                petCheck.isChecked && lockCheck.isChecked){
            selectPayment.visibility = View.VISIBLE
            selectPayment.setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        when(v) {
            selectPayment -> {
                if (selectPayment.text.toString() == getString(R.string.select_payment)) {

                    paymentSession = PaymentSession(this, PaymentSessionConfig.Builder()
                        .setShippingInfoRequired(false)
                        .setShippingMethodsRequired(false)
                        .setBillingAddressFields(BillingAddressFields.None)
                        .setShouldShowGooglePay(false)
                        .build())

                    paymentSession.init(
                        object: PaymentSession.PaymentSessionListener {
                            override fun onPaymentSessionDataChanged(data: PaymentSessionData) {
                                Log.d("PaymentSession", "PaymentSession has changed: $data")
                                Log.d("PaymentSession", "${data.isPaymentReadyToCharge} <> ${data.paymentMethod}")

                                if (data.isPaymentReadyToCharge) {
                                    Log.d("PaymentSession", "Ready to charge")

                                    data.paymentMethod?.let {
                                        Log.d("PaymentSession", "PaymentMethod $it selected")
                                        selectedPaymentMethod = it
                                        Toast.makeText(requireContext(), "Payment Method Selected", Toast.LENGTH_SHORT).show()

                                        selectPayment.text = getString(R.string.order_service)
                                    }
                                }
                            }

                            override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
                                Log.d("PaymentSession",  "isCommunicating $isCommunicating")
                            }

                            override fun onError(errorCode: Int, errorMessage: String) {
                                Log.e("PaymentSession",  "onError: $errorCode, $errorMessage")
                            }
                        })
                    paymentSession.presentPaymentMethodSelection()
                }else{
                    progressBar.visibility = View.VISIBLE
                    uploadOrder()
                }
            }
        }
    }

    private fun uploadOrder(){

        val order = OrderObject(uid, property.imgSrc, System.currentTimeMillis(), lot, property.address?.streetAddress!!,
        property.address?.city!!, property.address?.state!!, property.address?.zipcode!!, topProvider,
        property.latitude, property.longitude, price, null, null, null)

        val orderDatabase = Firebase.firestore
            .collection("orders")

        orderDatabase.add(order).addOnSuccessListener {
            documentId = it.id
            confirmPayment(selectedPaymentMethod.id!!)
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "Please try again", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmPayment(paymentMethodId: String) {

        val paymentCollection = Firebase.firestore
            .collection("payments").document(documentId)

        // Add a new document with a generated ID
        paymentCollection.set(hashMapOf(
            "currency" to "usd",
            "uid" to uid,
            "orderId" to documentId
        ))
            .addOnSuccessListener { documentReference ->
                Log.d("payment", "DocumentSnapshot added with ID: $documentReference")
                paymentCollection.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        progressBar.visibility = View.GONE
                        Log.w("payment", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d("payment", "Current data: ${snapshot.data}")
                        val clientSecret = snapshot.data?.get("client_secret")
                        val paymentId = snapshot.data?.get("id")
                        val amount = snapshot.data?.get("amount_received").toString()
                        val data = hashMapOf(
                            "paymentId" to paymentId,
                            "documentId" to documentId
                        )
                        Firebase.firestore.collection("orderData")
                            .document(documentId).update(data)

                        if (amount == "0") {
                            Log.d("payment", "Create paymentIntent returns $clientSecret")
                            clientSecret.let {
                                stripe.confirmPayment(
                                    this, ConfirmPaymentIntentParams.createWithPaymentMethodId(
                                        paymentMethodId,
                                        (it.toString())
                                    )
                                )
                            }
                        }


                        val confirmation = Firebase.firestore.collection("customerOrders").document(uid)
                        confirmation.addSnapshotListener { value, error ->
                            if (error != null){
                                Toast.makeText(requireContext(),
                                    "Confirmation failed: Check your order status in the settings screen",
                                    Toast.LENGTH_SHORT).show()
                            }else if (value?.data?.get(documentId).toString() == "false"){
                                progressBar.visibility = View.GONE
                                Log.e("Order failed", "order failed")
                            }
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        Log.e("payment", "Current payment intent : null")
                    }
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "failure", Toast.LENGTH_SHORT).show()
                Log.w("payment", "Error adding document", e)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        paymentSession.handlePaymentData(requestCode, resultCode, data ?: Intent())

        stripe.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {

            override fun onSuccess(result: PaymentIntentResult) {
                val paymentIntent = result.intent

                when (paymentIntent.status) {

                    StripeIntent.Status.Succeeded -> {
                        Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                        findNavController().navigate(R.id.action_confirmPurchaseFragment_to_homeFragment)
                    }
                    StripeIntent.Status.RequiresPaymentMethod -> {
                        Toast.makeText(requireContext(), "Payment Failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Unknown Result", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onError(e: Exception) {
                Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })

    }
}