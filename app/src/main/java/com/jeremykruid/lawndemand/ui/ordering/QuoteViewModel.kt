package com.jeremykruid.lawndemand.ui.ordering

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.jeremykruid.lawndemand.ui.settings.BaseViewModel

class QuoteViewModel: BaseViewModel() {

    val rates = MutableLiveData<Double>()

    fun getPrices(lot: Int, topProvider: Boolean) {
        val data = hashMapOf(
            "lotSize" to  lot,
            "topProvider" to topProvider
        )
        functions.getHttpsCallable("getPrices").call(data).addOnSuccessListener {
            val tempData = it.data.toString().toDouble()
            rates.postValue(tempData)

        }.addOnFailureListener {
            Log.e("QuoteVM", "failure" + it.toString())
        }
    }
}