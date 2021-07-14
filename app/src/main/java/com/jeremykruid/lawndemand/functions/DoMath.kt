package com.jeremykruid.lawndemand.functions

class DoMath {
    fun getPrice(lotSize: Int): Double{
        var price = 0.0
        when(lotSize){
             in 1..2000 ->{
                 price = 25.0
            }
            in 2001..3000 -> {
                price = 30.0
            }
            in 3001..4000 -> {
                price = 32.0
            }
            in 4001..5000 -> {
                price = 35.0
            }
            in 5001..6000 -> {
                price = 37.0
            }
            in 6001..7000 -> {
                price = 39.0
            }
            in 7001..8000 -> {
                price = 41.0
            }
            in 8001..9000 -> {
                price = 43.0
            }
            in 9001..10000 ->{
                price = 45.0
            }
            in 10001..11000 -> {
                price = 47.0
            }
            in 11001..12000 -> {
                price = 48.0
            }
            in 12001..13000 -> {
                price = 49.0
            }
            in 13001..14000 -> {
                price = 50.0
            }
            in 14001..15000 ->{
                price = 51.0
            }
            in 15001..16000 -> {
                price = 52.0
            }
            in 16001..17000 -> {
                price = 53.0
            }
            in 17001..18000 ->{
                price = 54.0
            }
            in 19000..20000 ->{
                price = 55.0
            }
            else -> {
                price = lotSize * .002895
            }

        }
        return price
    }

    fun convertToDollars(double: Double):String{
        var thisDollar = ""
        val arr = double.toString().split(".")
        if (arr[1].length == 1){
            thisDollar = "${arr[0]}.${arr[1]}0"
        }else if (arr[1].length >= 2){
            thisDollar = "${arr[0]}.${arr[1][0]}${arr[1][1]}"
        }
        return thisDollar
    }

    fun convertStripeToDollars(string: String): String{
        val int = string.toDouble()
        val double:Double = int / 100
        val arr = double.toString().split(".")
        var dollarString = ""
        if (arr[1].length == 1){
            dollarString = "${arr[0]}.${arr[1]}0"
        }else if (arr[1].length >= 2){
            dollarString = "${arr[0]}.${arr[1][0]}${arr[1][1]}"
        }

        return dollarString
    }

    fun convertToStripeDollars(double: Double): String {
        val arr = double.toString().split(".")
        var dollarString = ""
        if (arr[1].length == 1){
            dollarString = "${arr[0]}${arr[1]}0"
        }else if (arr[1].length >= 2){
            dollarString = "${arr[0]}${arr[1][0]}${arr[1][1]}"
        }

        return dollarString
    }
}