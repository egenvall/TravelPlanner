package com.egenvall.travelplanner.common.threading

import io.reactivex.Scheduler


interface BackgroundExecutor {
    val scheduler: Scheduler
}