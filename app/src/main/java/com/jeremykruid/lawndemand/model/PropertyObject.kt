package com.jeremykruid.lawndemand.model

import java.io.Serializable

class PropertyObject: Serializable {
    var zpid: Int = 0
    var imgSrc: String = ""
    var address: Address? = null
    var resoFacts: ResoFacts? = null
    var homeType: String = ""
    var latitude: Double = 0.0
    var longitude:Double = 0.0

    constructor()
    constructor(zpid: Int, imgSrc: String, address: Address, resoFacts: ResoFacts, homeType: String,
                latitude: Double, longitude: Double){
        this.zpid = zpid
        this.imgSrc = imgSrc
        this.address = address
        this.resoFacts = resoFacts
        this.homeType = homeType
        this.latitude = latitude
        this.longitude = longitude
    }
}