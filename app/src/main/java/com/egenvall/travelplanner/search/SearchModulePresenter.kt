package com.egenvall.travelplanner.search

import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.VtResponseModel
import rx.Observer
import javax.inject.Inject


class SearchModulePresenter @Inject constructor(private val searchUsecase: SearchUsecase): BasePresenter<SearchModulePresenter.View>(){
    fun searchForLocation(searchTerm : String){
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
                    setSearchResult((coordList + stopList)
                            .filter { it.idx.toInt() <= 3 }
                            .sortedBy { it.idx })
                }


            }

            override fun onError(e: Throwable) = performViewAction { showMessage(e.toString()) }
            override fun onCompleted() {}
        })

    }
//===================================================================================
// View Interface
//===================================================================================

    interface View : BaseView {
        fun showMessage(str: String)
        fun setSearchResult(stopLocation: List<StopLocation>)
    }


    //===================================================================================
//  Lifecycle Methods
//===================================================================================
    override fun onViewAttached() {}
    override fun onViewDetached() {}
    override fun unsubscribe() {
        searchUsecase.unsubscribe()
    }
}