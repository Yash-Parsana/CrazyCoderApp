package com.parsanatech.crazycoder.models

class UseratFirebase {

    var usename:String?=null
    var email:String?=null
    var pass:String?=null

    constructor(usename: String?, email: String?, pass: String?) {
        this.usename = usename
        this.email = email
        this.pass = pass
    }
    constructor(usename: String?, email: String?) {
        this.usename = usename
        this.email = email
    }
}