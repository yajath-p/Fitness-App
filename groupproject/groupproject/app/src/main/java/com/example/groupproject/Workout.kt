package com.example.groupproject

class Workout {
    var type : String = ""
    var duration : Int = 0
    var day : String = ""

    constructor(newType : String, newDuration : Int, newDay : String) {
        type = newType
        duration = newDuration
        day = newDay
    }
}