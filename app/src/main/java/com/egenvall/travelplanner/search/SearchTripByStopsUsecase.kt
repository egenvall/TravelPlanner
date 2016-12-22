package com.egenvall.travelplanner.search

import com.egenvall.travelplanner.base.domain.ReactiveUseCase
import com.egenvall.travelplanner.common.threading.AndroidUiExecutor
import com.egenvall.travelplanner.common.threading.RxIoExecutor
import com.egenvall.travelplanner.model.TripResponseModel
import com.egenvall.travelplanner.model.VtResponseModel
import com.egenvall.travelplanner.network.Repository
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import javax.inject.Inject



class SearchTripByStopsUsecase @Inject constructor(val repository: Repository, uiExec : AndroidUiExecutor, ioExec : RxIoExecutor) : ReactiveUseCase<TripResponseModel>(uiExec,ioExec) {

    var originId = ""
    var destId = ""
    fun searchTripsByStops(originId : String, destId : String, presenterObserver : DisposableObserver<TripResponseModel>){
        this.originId = originId
        this.destId = destId
        super.executeUseCase(presenterObserver)
    }


    override fun useCaseObservable(): Observable<TripResponseModel> {
        return repository.getTripByStops(originId,destId)
    }
}