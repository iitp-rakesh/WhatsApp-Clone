package com.android.application

class Message {
    var message:String?=null
    var senderId:String?=null
    var timestamp:String?=null
    constructor(){}

    constructor(message:String,senderId:String,timestamp:String){
        this.message=message
       this.senderId=senderId
        this.timestamp=timestamp
    }
}