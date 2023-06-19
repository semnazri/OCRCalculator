package com.semnazri.ocrcalculator.base

import android.app.Application
import com.semnazri.ocrcalculator.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApp : Application() {
    private val appModules = listOf(viewModelModule)

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@BaseApp)
            modules(appModules)
        }
    }
}