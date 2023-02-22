package com.parsanatech.crazycoder.models

class sdeModel {

    var sheetname=""
    var topic=""
    var madeby=""
    var workedat=""
    var image=""
    var rank=0
    var link=""




    constructor(
        sheetname: String,
        topic: String,
        madeby: String,
        workedat: String,
        image: String,
        rank: Int,
        link: String,
    ) {
        this.sheetname = sheetname
        this.topic = topic
        this.madeby = madeby
        this.workedat = workedat
        this.image = image
        this.rank = rank
        this.link = link
    }


}