package com.mvvm.esportlogo

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        myApplication = this
    }


    companion object {
        var myApplication: MyApplication? = null

        fun getInstance(): MyApplication {
            if (myApplication == null) {
                myApplication = MyApplication()
            }
            return myApplication!!
        }
    }
}