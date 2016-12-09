package com.egenvall.travelplanner.common.injection.component


import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.common.injection.module.MainViewModule
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import com.egenvall.travelplanner.main.MainActivity
import dagger.Component

@PerScreen
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ActivityModule::class, MainViewModule::class))
interface MainViewComponent : ActivityComponent {

    fun inject(mainActivity: MainActivity)
}