package com.egenvall.travelplanner.main

import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import com.egenvall.travelplanner.common.threading.AndroidUiExecutor
import com.egenvall.travelplanner.common.threading.RxIoExecutor
import io.reactivex.observers.DisposableObserver
import javax.inject.Inject


@PerScreen
class MainPresenter @Inject constructor() : BasePresenter<MainPresenter.View>() {

    val someUseCase = SomeUseCase(AndroidUiExecutor(), RxIoExecutor()) //Inject

    /**
     * Called when view is detached
     */
    override fun unsubscribe() {
        someUseCase.unsubscribe()
    }

    fun onButtonClicked() {
        someUseCase.doSomething(object : DisposableObserver<String>(){
            override fun onNext(value : String) = view.showMessage(value)
            override fun onError(e: Throwable?)  = view.showMessage(e.toString())
            override fun onComplete() {}
        })
    }

    interface View : BaseView {
        fun showMessage(str : String)
    }
}