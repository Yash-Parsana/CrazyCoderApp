package com.parsanatech.crazycoder.models

class contestModel {

    var name:String
    var startTime:String
    var endTime:String
    var timeStamp:Long=0




    constructor(name: String, startTime: String, endTime: String,Ts:Long) {
        this.name = name
        this.startTime = startTime
        this.endTime = endTime
        timeStamp=Ts
    }

}