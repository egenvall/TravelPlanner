package com.egenvall.travelplanner.search

import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.base.presentation.BaseController
import com.egenvall.travelplanner.common.injection.component.DaggerSearchViewComponent
import com.egenvall.travelplanner.common.injection.component.SearchViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.extension.getDrawable
import com.egenvall.travelplanner.extension.setDrawable
import com.egenvall.travelplanner.extension.showSnackbar
import kotlinx.android.synthetic.main.screen_search.*
import kotlinx.android.synthetic.main.screen_search.view.*
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
        view.search_expand_btn.setOnClickListener {toggleExpandableView(view)}
        presenter.searchForLocation("Lantmilsgatan")
    }

    fun toggleExpandableView(view:View){
        if (view.expandable_layout.isExpanded){
            view.expandable_layout.collapse(true)
            view.search_expand_btn.setDrawable(view.getDrawable(R.drawable.ic_expand_more_white_36dp))
        }
        else {
            view.expandable_layout.expand(true)
            view.search_expand_btn.setDrawable(view.getDrawable(R.drawable.ic_expand_less_white_36dp))
        }

    }

    //===================================================================================
    // Dependency injection
    //===================================================================================

    override fun initInjection() {
        val act = activity as AppCompatActivity
        searchViewComponent = DaggerSearchViewComponent.builder()
                .appComponent((act.application as TravelPlanner).appComponent)
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