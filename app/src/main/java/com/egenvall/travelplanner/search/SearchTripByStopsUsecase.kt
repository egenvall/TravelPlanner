package com.egenvall.travelplanner.search

import com.egenvall.travelplanner.base.domain.ReactiveUseCase
import com.egenvall.travelplanner.common.threading.AndroidUiExecutor
import com.egenvall.travelplanner.common.threading.RxIoExecutor
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.TripResponseModel
import com.egenvall.travelplanner.network.Repository
import io.reactivex.Observable
import io.reactivex.observers.DisposableObserver
import javax.inject.Inject



open class SearchTripByStopsUsecase @Inject constructor(val repository: Repository, uiExec : AndroidUiExecutor, ioExec : RxIoExecutor) : ReactiveUseCase<TripResponseModel>(uiExec,ioExec) {

    var origin = StopLocation()
    var dest = StopLocation()
    fun searchTripsByStops(origin : StopLocation, dest : StopLocation, presenterObserver : DisposableObserver<TripResponseModel>){
        this.origin = origin
        this.dest = dest
        super.executeUseCase(presenterObserver)
    }

    private fun fromStopId() : Observable<TripResponseModel> {
        with(dest.type){
            if (this == "STOP") return repository.getTripByStops(origin,dest) //Dest is Stop
            else return repository.getTripsIdAndCoord(origin,dest) //Dest is Address or POI
        }

    }
    private fun fromAddress() : Observable<TripResponseModel>{
        with(dest.type){
            if (this == "STOP") return repository.getTripsCoordAndId(origin,dest)
            else return repository.getTripsCoordAndCoord(origin,dest)
        }
    }
    override fun useCaseObservable(): Observable<TripResponseModel> {
        with(origin.type){
            if (this == "STOP") return  fromStopId()
            else return fromAddress()
        }
    }
}