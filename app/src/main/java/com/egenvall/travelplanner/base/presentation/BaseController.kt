package com.egenvall.travelplanner.base.presentation

import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bluelinelabs.conductor.rxlifecycle.RxController
import com.egenvall.travelplanner.ExampleApplication
import com.egenvall.travelplanner.common.injection.module.ActivityModule


abstract class BaseController<View: BaseView, out Presenter : BasePresenter<View>> : RxController() {

    protected abstract val passiveView: View
    protected abstract val presenter: Presenter
    protected abstract val layoutResId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): android.view.View {
        val view = inflater.inflate(layoutResId,container,false)
        onViewBound(view)
        return view
    }

    abstract fun onViewBound(view: android.view.View)

    override fun onAttach(view: android.view.View) {
        super.onAttach(view)
        presenter.onViewAttached(passiveView)
    }

    override fun onDetach(view: android.view.View) {
        presenter.onViewDetached()
        super.onDetach(view)
    }

    protected abstract fun initInjection()
}