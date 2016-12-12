package com.egenvall.travelplanner.main

import android.support.annotation.LayoutRes
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.support.ControllerPagerAdapter
import com.egenvall.travelplanner.ExampleApplication
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.base.presentation.BaseController
import com.egenvall.travelplanner.common.injection.component.DaggerMainViewComponent
import com.egenvall.travelplanner.common.injection.component.MainViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.extension.showSnackbar
import javax.inject.Inject


class ChildController : BaseController<MainPresenter.View, MainPresenter>(),
MainPresenter.View{
    private lateinit var mainViewComponent: MainViewComponent
    override val passiveView: MainPresenter.View = this
    @Inject override lateinit var presenter: MainPresenter
    @LayoutRes override val layoutResId: Int = R.layout.page
    private val TAG = "ChildController"


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