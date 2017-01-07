package com.egenvall.travelplanner.search

import android.util.Log
import com.egenvall.travelplanner.base.domain.DefaultSubscriber
import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import com.egenvall.travelplanner.model.*
import io.reactivex.observers.DisposableObserver
import io.realm.Realm
import rx.Observer
import javax.inject.Inject


@PerScreen
open class SearchPresenter @Inject constructor(private val searchUsecase: SearchUsecase, private val searchTripByStopsUsecase: SearchTripByStopsUsecase) : BasePresenter<SearchPresenter.View>() {

   @Inject lateinit var realm : Realm
    val TAG  = "SearchPresenter"
    /**
     * Called when view is detached
     */
    override fun unsubscribe() {
        searchUsecase.unsubscribe()
        searchTripByStopsUsecase.unsubscribe()
    }


//===================================================================================
// Usecase Methods
//===================================================================================

    /**
     * Introduce RxBindings when it's available for RxJava2
     */
    open fun searchForLocation(searchTerm : String, wasOrigin : Boolean) {
        searchUsecase.searchForLocation(searchTerm.trim(), object : Observer<VtResponseModel>{
            override fun onNext(response : VtResponseModel){
                val locationList = response.LocationList
                if (locationList.error != null) onError(Throwable(locationList.errorText))

                /**
                 * If the response contains no CoordLocation or StopLocation (Nullable in model)
                 * Set the variable to an empty list to avoid handling nulls.
                 * If the returned list is empty it should be handled by the view
                 */
                val coordList = locationList.CoordLocation?.map {
                    StopLocation(type = it.type,lon = it.lon,lat = it.lat,idx = it.idx,name = it.name)
                }?: listOf<StopLocation>()
                val stopList = locationList.StopLocation?: listOf<StopLocation>()

                //Set the view to show 3 most relevant items sorted by relevance
                performViewAction { setSearchResults(
                        (coordList+stopList)
                                .filter { it.idx.toInt() <= 3}
                                .sortedBy {it.idx},wasOrigin) }


            }
            override fun onError(e: Throwable)  = view.showMessage(e.toString())
            override fun onCompleted() {view.showMessage("")}
        })
    }


    override fun onViewAttached() {
    }

    override fun onViewDetached() {
    }

    fun searchForTripByLocations(origin : RealmStopLocation, dest: RealmStopLocation){
        searchForTripByLocations(mapToStopLocation(origin),mapToStopLocation(dest))
    }

    fun searchForTripByLocations(origin: StopLocation, dest: StopLocation){
        addToSearchHistory(origin,dest)
        searchTripByStopsUsecase.searchTripsByStops(origin,dest, object : Observer<TripResponseModel> {

            override fun onNext(value: TripResponseModel) {
                view.setTripResults(value.TripList.Trip)



            }

            override fun onError(e: Throwable?) {
                view.showMessage(e.toString())
                Log.e(TAG,e.toString())
            }

            override fun onCompleted() {
                getSearchHistory()
            }


        })
    }


//===================================================================================
// Realm Methods
//===================================================================================

    fun addToSearchHistory(origin : StopLocation, dest : StopLocation){
        val history = realm.where(SearchHistory::class.java).findFirst()
        if (history == null){
            realm.beginTransaction()
                val history = realm.createObject(SearchHistory::class.java,"History")
            realm.commitTransaction()
            addPairToRealm(origin,dest)
        }
        else{
            addPairToRealm(origin,dest)
        }
    }

    fun constructPkSearchPair(origin: StopLocation, dest: StopLocation):String{
        var originIdentifier : String = ""
        var destIdentifier : String = ""
        when(origin.type){
            "STOP" -> originIdentifier = origin.id
            "ADR" -> originIdentifier = origin.name
            "POI" -> originIdentifier = origin.name
        }
        when (dest.type){
            "STOP" -> destIdentifier = dest.id
            "ADR" -> destIdentifier = dest.name
            "POI" -> destIdentifier = dest.name
        }
        return originIdentifier+"/"+destIdentifier
    }
    fun addPairToRealm(origin: StopLocation, dest: StopLocation){
        val history = realm.where(SearchHistory::class.java).findFirst()
        val copy = realm.copyFromRealm(history.list)

        copy.add(0, SearchPair(constructPkSearchPair(origin,dest),
                mapToRealmStopLocation(origin),
                mapToRealmStopLocation(dest))
                )

        realm.executeTransaction {
            with(history.list) {
                deleteAllFromRealm()
                realm.copyToRealmOrUpdate(copy.take(6))
                addAll(copy.distinctBy { it.orgPlusDestId })
            }
        }
    }

    private fun mapToStopLocation(stop : RealmStopLocation) : StopLocation{
        with(stop){
            return StopLocation(stopid,type,lat,lon,idx,name)
        }
    }
    private fun mapToRealmStopLocation(stop : StopLocation) : RealmStopLocation{
        with(stop){
            return RealmStopLocation(id,type,lat,lon,idx,name)
        }
    }

    fun removeFromSearchHistory(pair : SearchPair){
        realm.executeTransaction {
            val result = realm.where(SearchPair::class.java)
                    .equalTo("orgPlusDestId", pair.orgPlusDestId).findFirst()
            result.deleteFromRealm()
        }
    }


    fun getSearchHistory(){
        realm.executeTransaction {
            //val res = realm.where(SearchHistory::class.java).findFirst().list.deleteAllFromRealm()
            val result = realm.where(SearchHistory::class.java).findFirst().list.distinct()
            view.setSearchHistory(realm.copyFromRealm(result).take(6))
        }
    }

//===================================================================================
// View Interface
//===================================================================================

    interface View : BaseView {
        fun showMessage(str : String)
        fun setSearchResults(list : List<StopLocation>, wasOrigin: Boolean)
        fun setTripResults(list : List<Trip>)
        fun setSearchHistory(list : List<SearchPair>)
    }
}