package com.egenvall.travelplanner.common.threading

import rx.Scheduler


interface BackgroundExecutor {
    val scheduler: Scheduler
}