package com.jeremykruid.lawndemand.model

import java.io.Serializable

class Address: Serializable {
    var city: String = ""
    var state: String = ""
    var streetAddress: String = ""
    var zipcode: String = ""

    constructor()
    constructor(city: String, state: String, streetAddress: String, zipcode: String){
        this.city = city
        this.state = state
        this.streetAddress = streetAddress
        this.zipcode = zipcode
    }
}