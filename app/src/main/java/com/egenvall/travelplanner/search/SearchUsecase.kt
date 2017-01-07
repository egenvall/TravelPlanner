package com.egenvall.travelplanner.search

import com.egenvall.travelplanner.base.domain.ReactiveUseCase
import com.egenvall.travelplanner.common.threading.AndroidUiExecutor
import com.egenvall.travelplanner.common.threading.BackgroundExecutor
import com.egenvall.travelplanner.common.threading.RxIoExecutor
import com.egenvall.travelplanner.common.threading.UiExecutor
import com.egenvall.travelplanner.model.VtResponseModel
import com.egenvall.travelplanner.network.Repository
import rx.Observable
import rx.Observer
import javax.inject.Inject


open class SearchUsecase @Inject constructor(val repository: Repository, uiExec : AndroidUiExecutor, ioExec : RxIoExecutor) : ReactiveUseCase<VtResponseModel>(uiExec,ioExec) {

    var searchTerm ="will-be-replaced"
    fun searchForLocation(searchTerm :String, presenterObserver : Observer<VtResponseModel>){
        this.searchTerm = searchTerm
        executeUsecase(presenterObserver)
    }

    fun executeUsecase(observer : Observer<VtResponseModel>){
        super.executeUseCase({observer.onNext(it)},{observer.onError(it)},{})
    }

    override fun useCaseObservable(): Observable<VtResponseModel> {
        return repository.getLocationBySearch(searchTerm)
    }
}