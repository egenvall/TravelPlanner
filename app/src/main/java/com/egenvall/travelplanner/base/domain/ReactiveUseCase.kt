package com.egenvall.travelplanner.base.domain

import com.egenvall.travelplanner.common.threading.UiExecutor
import com.egenvall.travelplanner.common.threading.BackgroundExecutor

import rx.Observable
import rx.Observer
import rx.functions.Action1
import rx.subscriptions.Subscriptions

abstract class ReactiveUseCase<ObservableType> (
        private val uiExecutor: UiExecutor,
        private val backgroundExecutor: BackgroundExecutor) {

    private var subscription = Subscriptions.empty()

    protected fun executeUseCase(observer: Observer<ObservableType>) {
        subscription = useCaseObservable()
                .subscribeOn(backgroundExecutor.scheduler)
                .observeOn(uiExecutor.scheduler)
                .subscribe(observer)
    }

    protected fun executeUseCase(resultAction: Action1<ObservableType>, errorAction: Action1<Throwable>) {
        this.subscription = useCaseObservable()
                .subscribeOn(backgroundExecutor.scheduler)
                .observeOn(uiExecutor.scheduler)
                .subscribe(resultAction, errorAction)
    }

    protected abstract fun useCaseObservable(): Observable<ObservableType>

    fun unsubscribe() {
        if (!subscription.isUnsubscribed) {
            subscription.unsubscribe()
        }
    }
}