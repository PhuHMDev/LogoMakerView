package com.mvvm.esportlogo

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        myApplication = this
        System.loadLibrary("opencv_java4") // rất quan trọng
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