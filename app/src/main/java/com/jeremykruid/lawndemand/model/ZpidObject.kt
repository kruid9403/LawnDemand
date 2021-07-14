package com.jeremykruid.lawndemand.model

import java.io.Serializable

class ZpidObject: Serializable {
    var zpid: Int = 0

    constructor()
    constructor(zpid: Int){
        this.zpid = zpid
    }
}