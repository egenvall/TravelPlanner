package com.egenvall.travelplanner.common.injection.component

import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.common.injection.module.TripViewModule
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import com.egenvall.travelplanner.tripdetail.TripController
import dagger.Component


@PerScreen
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ActivityModule::class, TripViewModule::class))
interface TripViewComponent : ActivityComponent {
    fun inject(tripController : TripController)
}
