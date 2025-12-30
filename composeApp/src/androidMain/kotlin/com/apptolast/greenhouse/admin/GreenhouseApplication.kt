package com.apptolast.greenhouse.admin

import android.app.Application
import com.apptolast.greenhouse.admin.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class GreenhouseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@GreenhouseApplication)
        }
    }
}
