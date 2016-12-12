package com.egenvall.travelplanner.common.injection.component

import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.common.injection.module.FavouriteViewModule
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import com.egenvall.travelplanner.favourite.FavouriteController
import dagger.Component


@PerScreen
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ActivityModule::class, FavouriteViewModule::class))
interface FavouriteViewComponent : ActivityComponent {

    fun inject(favouriteController : FavouriteController)
}