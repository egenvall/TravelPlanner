package com.egenvall.travelplanner.base.presentation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.common.injection.module.ActivityModule

abstract class BaseFragment<View: BaseView, out Presenter : BasePresenter<View>> : Fragment() {

    protected abstract val passiveView: View
    protected abstract val presenter: Presenter
    protected abstract val layoutResId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initInjection()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): android.view.View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewAttached(passiveView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onViewDetached()
    }

    private val act = activity as AppCompatActivity

    protected fun appComponent() = (act.application as TravelPlanner).appComponent

    protected fun activityModule() = ActivityModule(act)

    protected abstract fun initInjection()
}