package com.egenvall.travelplanner.search

import com.egenvall.travelplanner.base.domain.ReactiveUseCase
import com.egenvall.travelplanner.common.threading.AndroidUiExecutor
import com.egenvall.travelplanner.common.threading.RxIoExecutor
import com.egenvall.travelplanner.model.VtResponseModel
import com.egenvall.travelplanner.network.Repository
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import javax.inject.Inject


class SearchUsecase @Inject constructor(val repository: Repository, uiExec : AndroidUiExecutor, ioExec : RxIoExecutor) : ReactiveUseCase<VtResponseModel>(uiExec,ioExec) {

    var searchTerm ="will-be-replaced"
    fun searchForLocation(searchTerm :String, presenterObserver : DisposableObserver<VtResponseModel>){
        this.searchTerm = searchTerm
        super.executeUseCase(presenterObserver)
    }


    override fun useCaseObservable(): Observable<VtResponseModel> {
        return repository.getLocationBySearch(searchTerm)
    }
}