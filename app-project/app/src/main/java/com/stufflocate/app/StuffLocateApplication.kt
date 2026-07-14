package com.stufflocate.app

import android.app.Application
import com.stufflocate.app.di.ServiceLocator

class StuffLocateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}

