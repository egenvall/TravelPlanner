package com.egenvall.travelplanner.tripdetail

import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.model.RealmStopLocation
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.Trip
import com.egenvall.travelplanner.model.TripResponseModel
import com.egenvall.travelplanner.persistance.IRealmInteractor
import com.egenvall.travelplanner.search.SearchTripByStopsUsecase
import rx.Observer
import javax.inject.Inject


class TripPresenter @Inject constructor(private val searchTripByStopsUsecase: SearchTripByStopsUsecase) : BasePresenter<TripPresenter.View>() {

//===================================================================================
// Trip Search related methods
//===================================================================================
    private fun mapToStopLocation(stop: RealmStopLocation): StopLocation {
        with(stop) {
            return StopLocation(stopid, type, lat, lon, idx, name)
        }
    }

    fun searchForTripByLocations(origin : RealmStopLocation, dest : RealmStopLocation){
        searchForTripByLocations(mapToStopLocation(origin),mapToStopLocation(dest))
    }
    fun searchForTripByLocations(origin: StopLocation, dest: StopLocation) {
        searchTripByStopsUsecase.searchTripsByStops(origin, dest, object : Observer<TripResponseModel> {
            override fun onNext(value: TripResponseModel) = performViewAction {
                setTripResults(value.TripList.Trip)
            }

            override fun onError(e: Throwable?) {
                performViewAction { showMessage(e.toString()) }
            }

            override fun onCompleted(){}
        })
    }


//===================================================================================
//  Lifecycle Methods
//===================================================================================
    override fun onViewAttached() {}
    override fun onViewDetached() {}
    override fun unsubscribe() {
        searchTripByStopsUsecase.unsubscribe()
    }
//===================================================================================
// View Interface
//===================================================================================
    interface View : BaseView {
        fun showMessage(str: String)
        fun setTripResults(list: List<Trip>)
    }
}