package com.egenvall.travelplanner.main

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.egenvall.travelplanner.ExampleApplication
import com.egenvall.travelplanner.R

import com.egenvall.travelplanner.common.injection.module.ActivityModule
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){
    protected fun appComponent() = (application as ExampleApplication).appComponent
    protected fun activityModule() = ActivityModule(this)
    lateinit var mRouter : Router
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRouter = Conductor.attachRouter(this, controller_container, savedInstanceState)
        if (!mRouter.hasRootController()) {
            mRouter.setRoot(RouterTransaction.with(MainController()));
        }
    }
}

