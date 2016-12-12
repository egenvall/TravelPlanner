package com.egenvall.travelplanner.base.presentation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.common.injection.module.ActivityModule

abstract class BaseActivity<View: BaseView, out Presenter : BasePresenter<View>> : AppCompatActivity() {

    protected abstract val passiveView: View
    protected abstract val presenter: Presenter
    protected abstract val layoutResId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        // The activity is being created.
        super.onCreate(savedInstanceState)

        setContentView(layoutResId)

        initInjection()

        presenter.onViewAttached(passiveView)
    }

    override fun onStart() {
        // The activity is about to become visible.
        super.onStart()
    }

    override fun onResume() {
        // The activity has become visible (it is now "resumed").
        super.onResume()
    }

    override fun onPause() {
        // Another activity is taking focus (this activity is about to be "paused").
        super.onPause()
    }

    override fun onStop() {
        // The activity is no longer visible (it is now "stopped")
        super.onStop()
    }

    override fun onDestroy() {
        // The activity is about to be destroyed.
        super.onDestroy()
        presenter.onViewDetached()
    }

    protected fun appComponent() = (application as TravelPlanner).appComponent

    protected fun activityModule() = ActivityModule(this)

    protected abstract fun initInjection()
}