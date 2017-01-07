package com.egenvall.travelplanner.common.threading

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AndroidUiExecutor @Inject constructor() : UiExecutor {

    override val scheduler: Scheduler
        get() = AndroidSchedulers.mainThread()
}