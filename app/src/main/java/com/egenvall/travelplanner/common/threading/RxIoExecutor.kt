package com.egenvall.travelplanner.common.threading



import rx.Scheduler
import rx.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class RxIoExecutor @Inject constructor() : BackgroundExecutor {

    override val scheduler: Scheduler
        get() = Schedulers.io()
}