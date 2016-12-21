package com.egenvall.travelplanner.common.injection.component

import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.common.injection.module.RealmModule
import dagger.Component
import io.realm.Realm
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(RealmModule::class))
interface RealmComponent {
    fun inject (application : TravelPlanner)
    fun realm (): Realm
}