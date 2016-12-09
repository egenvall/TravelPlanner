package com.egenvall.travelplanner.base.domain

import rx.Subscriber

class DefaultSubscriber<T> : Subscriber<T>() {
    override fun onCompleted() {
    }

    override fun onError(e: Throwable) {
    }

    override fun onNext(t: T) {
    }
}