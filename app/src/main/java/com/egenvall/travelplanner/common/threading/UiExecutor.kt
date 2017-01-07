package com.egenvall.travelplanner.common.threading

import rx.Scheduler

interface UiExecutor {
    val scheduler: Scheduler
}