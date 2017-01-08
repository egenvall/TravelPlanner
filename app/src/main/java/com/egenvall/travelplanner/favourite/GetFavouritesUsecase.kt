package com.egenvall.travelplanner.favourite

import com.egenvall.travelplanner.base.domain.ReactiveUseCase
import com.egenvall.travelplanner.common.threading.AndroidUiExecutor
import com.egenvall.travelplanner.common.threading.RxIoExecutor
import rx.Observable
import rx.Observer
import javax.inject.Inject


class GetFavouritesUsecase  @Inject constructor(uiExec : AndroidUiExecutor, ioExec : RxIoExecutor) : ReactiveUseCase<String>(uiExec,ioExec) {

    fun getFavourites(presenterObserver : Observer<String>){
        super.executeUseCase({it},{it},{})
    }
    override fun useCaseObservable(): Observable<String> {
        return Observable.just("SomeFav")
    }
}