package com.example.bettertimberlogsandroid

import ClickableLineNumberDebugTree
import android.app.Application
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(ClickableLineNumberDebugTree("GTAG"))
        Timber.d("Hello Timber")
    }
}