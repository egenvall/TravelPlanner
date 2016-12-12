package com.egenvall.travelplanner.search

import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.egenvall.travelplanner.ExampleApplication
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.base.presentation.BaseController
import com.egenvall.travelplanner.common.injection.component.DaggerSearchViewComponent
import com.egenvall.travelplanner.common.injection.component.SearchViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.extension.showSnackbar
import javax.inject.Inject


class SearchController : BaseController<SearchPresenter.View, SearchPresenter>(),
        SearchPresenter.View {
    private lateinit var searchViewComponent: SearchViewComponent
    override val passiveView: SearchPresenter.View = this
    @Inject override lateinit var presenter: SearchPresenter
    @LayoutRes override val layoutResId: Int = R.layout.screen_search
    private val TAG = "SearchController"


    //===================================================================================
    // Lifecycle methods and initialization
    //===================================================================================
    override fun onViewBound(view: View) {
        initInjection()
    }

    //===================================================================================
    // Dependency injection
    //===================================================================================

    override fun initInjection() {
        val act = activity as AppCompatActivity
        searchViewComponent = DaggerSearchViewComponent.builder()
                .appComponent((act.application as ExampleApplication).appComponent)
                .activityModule(ActivityModule(act))
                .build()
        searchViewComponent.inject(this)
    }

    //===================================================================================
    // View methods
    //===================================================================================

    override fun showMessage(str : String) {
        view?.showSnackbar(str)
    }
}