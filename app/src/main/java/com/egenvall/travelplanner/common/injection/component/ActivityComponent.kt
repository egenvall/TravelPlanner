package com.egenvall.travelplanner.common.injection.component

import android.app.Activity
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import dagger.Component

@PerScreen
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun activity(): Activity
}
