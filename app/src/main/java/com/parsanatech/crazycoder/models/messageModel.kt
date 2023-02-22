package com.parsanatech.crazycoder.models

class messageModel {

    var id:String?=null
    var senderId:String?=null
    var message:String?=null
    var time:String?=null
    var messageId:String?=null

    constructor()
    {

    }

    constructor(id:String,senderId: String, message: String, time: String,messageId:String) {
        this.id=id
        this.senderId = senderId
        this.message = message
        this.time = time
        this.messageId=messageId
    }
}