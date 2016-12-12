package com.egenvall.travelplanner.main

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.egenvall.travelplanner.ExampleApplication
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.common.injection.component.DaggerMainViewComponent
import com.egenvall.travelplanner.common.injection.component.MainViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import kotlinx.android.synthetic.main.activity_main.*
import devlight.io.library.ntb.NavigationTabBar
import android.graphics.Color.parseColor
import java.util.*


class MainActivity : AppCompatActivity(){
    private lateinit var mainViewComponent: MainViewComponent
    protected fun appComponent() = (application as ExampleApplication).appComponent
    protected fun activityModule() = ActivityModule(this)
    lateinit var mRouter : Router
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initInjection()
        mRouter = Conductor.attachRouter(this, controller_container, savedInstanceState)
        if (!mRouter.hasRootController()) {
            mRouter.setRoot(RouterTransaction.with(MainController()));
        }
    }

    fun initInjection() {
        mainViewComponent = DaggerMainViewComponent.builder()
                .appComponent(appComponent())
                .activityModule(activityModule())
                .build()
        mainViewComponent.inject(this)
    }


}

