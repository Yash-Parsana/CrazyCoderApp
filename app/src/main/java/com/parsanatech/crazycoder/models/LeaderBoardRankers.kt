package com.parsanatech.crazycoder.models

class LeaderBoardRankers {

    var handle:String
    var rank:Int
    var itsMe:Boolean

    constructor(handle: String, rank: Int,itsMe:Boolean) {
        this.handle = handle
        this.rank = rank
        this.itsMe=itsMe
    }
}