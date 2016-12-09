package com.egenvall.travelplanner.base.domain

import io.reactivex.subscribers.ResourceSubscriber


class DefaultSubscriber<T> : ResourceSubscriber<T>() {
    override fun onComplete() {
    }

    override fun onError(e: Throwable) {
    }

    override fun onNext(t: T) {
    }
}