package com.jeremykruid.lawndemand.ui.settings

import androidx.lifecycle.ViewModel
import com.google.firebase.functions.FirebaseFunctions

open class BaseViewModel: ViewModel() {

    val functions = FirebaseFunctions.getInstance()
}