package com.egenvall.travelplanner.main

import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import javax.inject.Inject


@PerScreen
class MainPresenter @Inject constructor() : BasePresenter<MainPresenter.View>() {

    override fun unsubscribe() {

    }

    fun onButtonClicked() {
        view.showMessage()
    }

    interface View : BaseView {
        fun showMessage()
    }
}