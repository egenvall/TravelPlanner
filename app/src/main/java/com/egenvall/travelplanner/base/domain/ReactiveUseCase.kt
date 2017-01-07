package com.egenvall.travelplanner.base.domain

import android.util.Log
import com.egenvall.travelplanner.common.threading.BackgroundExecutor
import com.egenvall.travelplanner.common.threading.UiExecutor
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import rx.schedulers.Schedulers

abstract class ReactiveUseCase<ObservableType> (
        private val uiExecutor: UiExecutor,
        private val backgroundExecutor: BackgroundExecutor) {

    private var disposables = CompositeDisposable()

    protected fun executeUseCase(observer: DisposableObserver<ObservableType>) {
        disposables.add(useCaseObservable()
                .subscribeOn(backgroundExecutor.scheduler)
                .observeOn(uiExecutor.scheduler)
                .subscribeWith(observer))
    }

    abstract fun useCaseObservable(): Observable<ObservableType>

    fun unsubscribe() {
        disposables.clear();
    }
}