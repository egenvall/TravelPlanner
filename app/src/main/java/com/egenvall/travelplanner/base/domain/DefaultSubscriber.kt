package com.egenvall.travelplanner.base.domain

import io.reactivex.observers.DisposableObserver
import io.reactivex.subscribers.ResourceSubscriber


open class DefaultSubscriber<T> : DisposableObserver<T>() {
    override fun onComplete() {
    }

    override fun onError(e: Throwable) {
    }

    override fun onNext(t: T) {
    }
}