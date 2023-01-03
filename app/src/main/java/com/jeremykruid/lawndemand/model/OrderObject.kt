package com.jeremykruid.lawndemand.model

class OrderObject {
    var uid: String = ""
    var orderId: String = ""
    var imgUrl: String= ""
    var orderDate: Long = 0
    var lotSize: Int = 0
    var streetAddress: String = ""
    var city: String = ""
    var state: String = ""
    var zip: String = ""
    var topProvider: Boolean = false
    var lat: Double = 0.0
    var lon: Double = 0.0
    var price: Double = 0.0
    var status: String = ""
    var completed: String = ""
    var provider: String = ""

    constructor()
    constructor(uid: String, orderId: String, imgUrl: String, orderDate: Long, lotSize: Int, streetAddress: String,
                city: String, state: String, zip: String, topProvider: Boolean, lat: Double, lon: Double,
                price: Double, status: String, completed: String, provider: String){
        this.uid = uid
        this.orderId = orderId
        this.imgUrl = imgUrl
        this.orderDate = orderDate
        this.lotSize = lotSize
        this.streetAddress = streetAddress
        this.city = city
        this.state = state
        this.zip = zip
        this.topProvider = topProvider
        this.lat = lat
        this.lon = lon
        this.price = price
        this.status = status
        this.completed = completed
        this.provider = provider
    }
}