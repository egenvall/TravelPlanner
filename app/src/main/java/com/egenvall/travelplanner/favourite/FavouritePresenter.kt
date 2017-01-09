package com.egenvall.travelplanner.favourite

import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import com.egenvall.travelplanner.model.*
import com.egenvall.travelplanner.search.SearchTripByStopsUsecase
import io.realm.Realm
import rx.Observer
import javax.inject.Inject



@PerScreen
class FavouritePresenter @Inject constructor(val searchTripUc : SearchTripByStopsUsecase) : BasePresenter<FavouritePresenter.View>() {
    @Inject lateinit var realm: Realm

    override fun onViewAttached() {
    }

    override fun onViewDetached() {
    }

    /**
     * Called when view is detached
     */
    override fun unsubscribe() {
        searchTripUc.unsubscribe()
    }

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

    fun addFavourite(origin : StopLocation, dest : StopLocation, nick: String, bg : String){
        val favList = realm.where(Favourites::class.java).findFirst()
            if (favList == null) {
                realm.beginTransaction()
                realm.createObject(Favourites::class.java, "Favourites")
                realm.commitTransaction()
                addFavouriteToRealm(origin,dest,nick,bg)
            } else {
                addFavouriteToRealm(origin,dest,nick,bg)
            }
    }

    private fun addFavouriteToRealm(origin : StopLocation, dest : StopLocation, nick: String, bg : String){
        val favourites = realm.where(Favourites::class.java).findFirst()
        val copy = realm.copyFromRealm(favourites.list)

        copy.add(0, Favourite(nick,bg,SearchPair(constructPkSearchPair(origin,dest),
                mapToRealmStopLocation(origin),
                mapToRealmStopLocation(dest)))
        )

        realm.executeTransaction {
            with(favourites.list) {
                deleteAllFromRealm()
                realm.copyToRealmOrUpdate(copy)
                addAll(copy.distinctBy { it.pair.orgPlusDestId })
            }
        }
    }
    private fun mapToRealmStopLocation(stop: StopLocation): RealmStopLocation {
        with(stop) {
            return RealmStopLocation(id, type, lat, lon, idx, name)
        }
    }

        fun constructPkSearchPair(origin: StopLocation, dest: StopLocation): String {
        var originIdentifier: String = ""
        var destIdentifier: String = ""
        when (origin.type) {
            "STOP" -> originIdentifier = origin.id
            "ADR" -> originIdentifier = origin.name
            "POI" -> originIdentifier = origin.name
        }
        when (dest.type) {
            "STOP" -> destIdentifier = dest.id
            "ADR" -> destIdentifier = dest.name
            "POI" -> destIdentifier = dest.name
        }
        return originIdentifier + "/" + destIdentifier
    }


    fun getFavourites() {
        realm.executeTransaction {
            val res = realm.where(Favourites::class.java).findFirst()
            if (res != null) {
                performViewAction { setFavourites(realm.copyFromRealm(res.list.distinct())) }
            } else {
                performViewAction { setFavourites(listOf<Favourite>()) }
            }
        }
    }
    /**
     * Interface for Controllers to implement
     */
    interface View : BaseView {
        fun showMessage(str : String)
        fun setFavourites(list : List<Favourite>)
    }
}