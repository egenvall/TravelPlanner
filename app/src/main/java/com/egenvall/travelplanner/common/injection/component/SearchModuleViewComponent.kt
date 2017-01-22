package com.egenvall.travelplanner.common.injection.component

import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.common.injection.module.SearchViewModule
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import com.egenvall.travelplanner.search.SearchModuleController
import dagger.Component


@PerScreen
@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(ActivityModule::class, SearchViewModule::class))
interface SearchModuleViewComponent : ActivityComponent {
    fun inject(searchController : SearchModuleController)
}