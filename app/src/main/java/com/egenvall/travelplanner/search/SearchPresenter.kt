package com.egenvall.travelplanner.search

import android.util.Log
import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.VtResponseModel
import io.reactivex.observers.DisposableObserver
import java.util.*
import javax.inject.Inject


@PerScreen
class SearchPresenter @Inject constructor(private val searchUsecase: SearchUsecase) : BasePresenter<SearchPresenter.View>() {

    /**
     * Called when view is detached
     */
    override fun unsubscribe() {
        searchUsecase.unsubscribe()
    }

    fun searchForLocation(searchTerm : String, wasOrigin : Boolean) {
        searchUsecase.searchForLocation(searchTerm.trim(),object : DisposableObserver<VtResponseModel>(){
            override fun onNext(response : VtResponseModel){
                val locationList = response.LocationList

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
                view.setSearchResults(
                        (coordList+stopList)
                                .filter { it.idx.toInt() <= 3}
                                .sortedBy {it.idx},wasOrigin)


            }
            override fun onError(e: Throwable?)  = view.showMessage(e.toString())
            override fun onComplete() {}
        })
    }

    interface View : BaseView {
        fun showMessage(str : String)
        fun setSearchResults(list : List<StopLocation>, wasOrigin: Boolean)
    }
}