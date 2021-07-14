package com.jeremykruid.lawndemand.functions.stripe

import android.util.Log
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.stripe.android.EphemeralKeyProvider
import com.stripe.android.EphemeralKeyUpdateListener

class FirebaseEphemeralKeyProvider: EphemeralKeyProvider {
    override fun createEphemeralKey(
        apiVersion: String,
        keyUpdateListener: EphemeralKeyUpdateListener
    ) {
        val data = hashMapOf(
            "api_version" to apiVersion
        )
        Firebase.functions
            .getHttpsCallable("createEphemeralKey")
            .call(data)
            .continueWith {
                if (!it.isSuccessful){
                    val e = it.exception
                    if (e is FirebaseFunctionsException){
                        val code = e.code
                        val message = e.message
                        Log.e("EphemeralKey", "Ephemeral key provider returns error: $e $code $message")
                    }
                }
                val key = it.result?.data.toString()
                Log.d("EphemeralKey", "Ephemeral key provider returns $key")
                keyUpdateListener.onKeyUpdate(key)
            }
    }
}