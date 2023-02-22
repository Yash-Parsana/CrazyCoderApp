package com.parsanatech.crazycoder.models

class chatUser{

    var username:String
    var picture:String
    var id:String
    var status:Boolean  // false means ofline and true means online

    constructor(username: String, picture: String, status: Boolean,id:String) {
        this.username = username
        this.picture = picture
        this.status = status
        this.id=id
    }
}