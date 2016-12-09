package com.egenvall.travelplanner.common.injection.component

import android.content.Context
import android.content.SharedPreferences
import com.egenvall.travelplanner.common.injection.module.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger component which lasts for the entire app lifecycle
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    fun context(): Context
    fun sharedPreferences(): SharedPreferences
}