package com.egenvall.travelplanner.search

import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import com.egenvall.travelplanner.model.*
import com.egenvall.travelplanner.persistance.IRealmInteractor
import io.realm.Realm
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

    fun searchForLocation(searchTerm: String, wasOrigin: Boolean) {
        searchUsecase.searchForLocation(searchTerm.trim(), object : Observer<VtResponseModel> {
            override fun onNext(response: VtResponseModel) {
                val locationList = response.LocationList
                if (locationList.error != null) onError(Throwable(locationList.errorText))

                /**
                 * If the response contains no CoordLocation or StopLocation (Nullable in model)
                 * Set the variable to an empty list to avoid handling nulls.
                 * If the returned list is empty it should be handled by the view
                 */
                val coordList = locationList.CoordLocation?.map {
                    StopLocation(type = it.type, lon = it.lon, lat = it.lat, idx = it.idx, name = it.name)
                } ?: listOf<StopLocation>()
                val stopList = locationList.StopLocation ?: listOf<StopLocation>()

                //Set the view to show 3 most relevant items sorted by relevance
                performViewAction {
                    setSearchResults((coordList + stopList)
                            .filter { it.idx.toInt() <= 3 }
                            .sortedBy { it.idx }, wasOrigin)
                }


            }

            override fun onError(e: Throwable) = performViewAction { showMessage(e.toString()) }
            override fun onCompleted() {}
        })
    }

    fun searchForTripByLocations(origin : RealmStopLocation, dest : RealmStopLocation){
        searchForTripByLocations(mapToStopLocation(origin),mapToStopLocation(dest))
    }
    fun searchForTripByLocations(origin: StopLocation, dest: StopLocation) {
        addToSearchHistory(origin, dest)
        searchTripByStopsUsecase.searchTripsByStops(origin, dest, object : Observer<TripResponseModel> {
            override fun onNext(value: TripResponseModel) = performViewAction {
                setTripResults(value.TripList.Trip)
            }

            override fun onError(e: Throwable?) {
                performViewAction { showMessage(e.toString()) }
            }

            override fun onCompleted() = getSearchHistory()
        })
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
        fun setSearchResults(list: List<StopLocation>, wasOrigin: Boolean)
        fun setTripResults(list: List<Trip>)
        fun setSearchHistory(list: List<SearchPair>)
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