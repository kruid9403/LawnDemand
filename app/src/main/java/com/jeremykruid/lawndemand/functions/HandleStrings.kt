package com.jeremykruid.lawndemand.functions

import kotlin.math.roundToInt

class HandleStrings {
    fun getLotSfInt(string: String): Int {
        val split = string.split(" ")
        val coma = split[0].split(",")
        var newString = ""
        coma.forEach {
            newString += it
        }

        return newString.toInt()
    }

    fun getAcresToSfInt(string: String):Int {
        val split = string.split(" ")
        val double = split[0].toDouble()
        val i = (double * 43560).roundToInt()

        return i
    }

    fun getLotMeasurementType(string: String):String{
        val split = string.split(" ")

        return split[split.size-1]
    }
}