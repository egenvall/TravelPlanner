package com.egenvall.travelplanner.search

import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import com.egenvall.travelplanner.model.*
import com.egenvall.travelplanner.persistance.IRealmInteractor
import rx.Observer
import javax.inject.Inject


@PerScreen
class SearchPresenter @Inject constructor(private val searchUsecase: SearchUsecase, private val searchTripByStopsUsecase: SearchTripByStopsUsecase, private val realmInteractor : IRealmInteractor) : BasePresenter<SearchPresenter.View>() {

//===================================================================================
// Search related methods
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
        // Only Stops have id, and you can't travel to the same stop
        if ((origin.id == dest.id) and (origin.type == "STOP") and (dest.type == "STOP")){
            performViewAction { showMessage("Origin and Destination can not be the same. Try again") }
        }
        else{
            addToSearchHistory(origin, dest)
            performViewAction {searchForTrips(origin,dest)}
        }
    }

    private fun addToSearchHistory(origin: StopLocation, dest: StopLocation)
            = realmInteractor.addSearchHistory(origin,dest)

    fun removeFromSearchHistory(pair: SearchPair) = realmInteractor.removeFromSearchHistory(pair)

    fun getSearchHistory() = performViewAction { setSearchHistory(realmInteractor.getSearchHistory()) }


//===================================================================================
// View Interface
//===================================================================================

    interface View : BaseView {
        fun showMessage(str: String)
        fun setTripResults(list: List<Trip>)
        fun setSearchHistory(list: List<SearchPair>)
        fun searchForTrips(origin: StopLocation, dest: StopLocation)
    }


//===================================================================================
//  Lifecycle Methods
//===================================================================================
    override fun onViewAttached() {}
    override fun onViewDetached() {}
    override fun unsubscribe() {
        searchUsecase.unsubscribe()
        searchTripByStopsUsecase.unsubscribe()
    }
}