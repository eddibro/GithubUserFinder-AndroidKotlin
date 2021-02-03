package com.edbro.githubuserfinder

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        myApplicationContext = this
    }

    companion object {
        lateinit var myApplicationContext: MyApplication
    }
}