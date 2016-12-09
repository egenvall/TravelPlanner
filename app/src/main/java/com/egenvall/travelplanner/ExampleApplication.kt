package com.egenvall.travelplanner

import android.app.Application
import com.egenvall.travelplanner.common.injection.component.AppComponent
import com.egenvall.travelplanner.common.injection.component.DaggerAppComponent
import com.egenvall.travelplanner.common.injection.module.AppModule


class ExampleApplication : Application() {

    internal lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initInjection()
    }

    private fun initInjection() {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }
}