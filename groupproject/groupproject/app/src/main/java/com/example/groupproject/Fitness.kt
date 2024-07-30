package com.example.groupproject

import android.content.Context
import android.content.SharedPreferences

class Fitness {
    private lateinit var pref : SharedPreferences
    private var restDay : String = ""
    private var theme : Int = 0

    constructor(context : Context) {
        pref = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
        restDay = pref.getString("rest day", "").toString()
        theme = pref.getInt("theme", 0)
    }

    fun getRestDay() : String {
        return restDay
    }

    fun getTheme() : Int {
        return theme
    }

    fun setRestDay(day : String) {
        restDay = day
    }

    fun setTheme(color : Int) {
        theme = color
    }

    fun setPreferences(context : Context) {
        pref = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = pref.edit()

        editor.putString("rest day", restDay)
        editor.putInt("theme", theme)
        editor.commit()
    }
}