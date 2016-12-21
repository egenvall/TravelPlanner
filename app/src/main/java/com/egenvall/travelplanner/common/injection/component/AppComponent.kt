package com.egenvall.travelplanner.common.injection.component

import android.content.Context
import android.content.SharedPreferences
import com.egenvall.travelplanner.common.injection.module.AppModule
import com.egenvall.travelplanner.common.injection.module.RealmModule
import com.egenvall.travelplanner.common.injection.module.VtModule
import com.egenvall.travelplanner.network.Repository
import dagger.Component
import io.realm.Realm
import javax.inject.Singleton

/**
 * Dagger component which lasts for the entire app lifecycle
 */
@Singleton
@Component(modules = arrayOf(AppModule::class, VtModule::class,RealmModule::class))
interface AppComponent {

    fun context(): Context
    fun sharedPreferences(): SharedPreferences
    fun repository() : Repository
    fun realm() : Realm
}