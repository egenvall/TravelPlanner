package com.egenvall.travelplanner.main

import android.support.annotation.LayoutRes
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.egenvall.travelplanner.ExampleApplication
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.base.presentation.BaseController
import com.egenvall.travelplanner.common.injection.component.DaggerMainViewComponent
import com.egenvall.travelplanner.common.injection.component.MainViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.extension.showSnackbar
import javax.inject.Inject


class MainController : BaseController<MainPresenter.View, MainPresenter>(),
        MainPresenter.View {

    private lateinit var mainViewComponent: MainViewComponent
    override val passiveView: MainPresenter.View = this
    @Inject override lateinit var presenter: MainPresenter
    @LayoutRes override val layoutResId: Int = R.layout.screen_main
    private val TAG = "MainController"

    //===================================================================================
    // Lifecycle methods and initialization
    //===================================================================================
    override fun onViewBound(view: View) {
        initInjection()
        val fab = view.findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { presenter.onButtonClicked() }
    }

    //===================================================================================
    // Dependency injection
    //===================================================================================

    override fun initInjection() {
        val act = activity as AppCompatActivity
        mainViewComponent = DaggerMainViewComponent.builder()
                .appComponent((act.application as ExampleApplication).appComponent)
                .activityModule(ActivityModule(act))
                .build()
        mainViewComponent.inject(this)
    }

    //===================================================================================
    // View methods
    //===================================================================================

    override fun showMessage(str : String) {
        view?.showSnackbar(str)
    }
}