package com.egenvall.travelplanner.common.injection.component

import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.common.injection.module.VtModule
import com.egenvall.travelplanner.network.Repository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(VtModule::class))
interface VtComponent {
    fun inject(application : TravelPlanner)
    fun repository(): Repository
}