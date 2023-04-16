package com.example.bettertimberlogsandroid

import BetterTimberDebugTree
import android.app.Application
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(BetterTimberDebugTree("GTAG"))
        Timber.d("Hello Timber")
    }
}