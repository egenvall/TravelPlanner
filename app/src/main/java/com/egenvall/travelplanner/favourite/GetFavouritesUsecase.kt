package com.egenvall.travelplanner.favourite

import com.egenvall.travelplanner.base.domain.ReactiveUseCase
import com.egenvall.travelplanner.common.threading.AndroidUiExecutor
import com.egenvall.travelplanner.common.threading.RxIoExecutor
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import javax.inject.Inject


class GetFavouritesUsecase  @Inject constructor(uiExec : AndroidUiExecutor, ioExec : RxIoExecutor) : ReactiveUseCase<String>(uiExec,ioExec) {

    fun getFavourites(presenterObserver : DisposableObserver<String>){
        super.executeUseCase(presenterObserver)
    }
    override fun useCaseObservable(): Observable<String> {
        return Observable.just("SomeFav")
    }
}