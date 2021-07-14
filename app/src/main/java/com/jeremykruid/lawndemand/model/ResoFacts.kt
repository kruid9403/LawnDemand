package com.jeremykruid.lawndemand.model

import java.io.Serializable

class ResoFacts: Serializable {
    var lotSize: String = ""

    constructor()
    constructor(lotSize: String){
        this.lotSize = lotSize
    }

}
