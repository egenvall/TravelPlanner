package com.egenvall.travelplanner.favourite

import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import com.egenvall.travelplanner.model.Favourite
import com.egenvall.travelplanner.model.RealmStopLocation
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.TripResponseModel
import com.egenvall.travelplanner.persistance.IRealmInteractor
import com.egenvall.travelplanner.search.SearchTripByStopsUsecase
import rx.Observer
import javax.inject.Inject


@PerScreen
class FavouritePresenter @Inject constructor(val searchTripUc : SearchTripByStopsUsecase, private val realmInteractor : IRealmInteractor) : BasePresenter<FavouritePresenter.View>() {

//===================================================================================
// Favourites related methods
//===================================================================================
    fun searchFavouriteTrip(fav : Favourite){
        searchTripUc.searchTripsByStops(
                mapToStopLocation(fav.pair.origin),mapToStopLocation(fav.pair.destination),
                object : Observer<TripResponseModel>{
                    override fun onNext(t: TripResponseModel) {

                    }

                    override fun onError(e: Throwable?) {

                    }

                    override fun onCompleted() {

                    }
                })
    }

    private fun mapToStopLocation(stop: RealmStopLocation): StopLocation {
        with(stop) {
            return StopLocation(stopid, type, lat, lon, idx, name)
        }
    }

//===================================================================================
//  Persistance related methods
//===================================================================================
    fun addFavourite(origin : StopLocation, dest : StopLocation, nick: String, bg : String){
        realmInteractor.addFavourite(origin,dest,nick,bg)
    }

    fun getFavourites() = performViewAction {setFavourites(realmInteractor.getFavourites())}

//===================================================================================
//  Lifecycle Methods
//===================================================================================
    override fun onViewAttached() {}

    override fun onViewDetached() {}

    //Called from BasePresenter when view is detached
    override fun unsubscribe() {
        searchTripUc.unsubscribe()
    }

//===================================================================================
// View Interface
//===================================================================================
    interface View : BaseView {
        fun showMessage(str : String)
        fun setFavourites(list : List<Favourite>)
    }
}