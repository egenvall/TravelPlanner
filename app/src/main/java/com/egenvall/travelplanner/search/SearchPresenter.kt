package com.egenvall.travelplanner.search

import android.util.Log
import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import com.egenvall.travelplanner.common.threading.AndroidUiExecutor
import com.egenvall.travelplanner.common.threading.RxIoExecutor
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.VtResponseModel
import io.reactivex.SingleObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.ResourceObserver
import io.reactivex.subscribers.ResourceSubscriber
import javax.inject.Inject


@PerScreen
class SearchPresenter @Inject constructor(private val searchUsecase: SearchUsecase) : BasePresenter<SearchPresenter.View>() {

    /**
     * Called when view is detached
     */
    override fun unsubscribe() {
        searchUsecase.unsubscribe()
    }

    fun onButtonClicked() {
        searchUsecase.searchForLocation("Lantmilsgatan",object : DisposableObserver<VtResponseModel>(){
            override fun onNext(response : VtResponseModel){
                val locationList = response.LocationList
                val topTen = (locationList.CoordLocation.map {
                    StopLocation(type = it.type,lon = it.lon,lat = it.lat,idx = it.idx,name = it.name)
                }
                        +locationList.StopLocation
                        )
                        .filter { it.idx.toInt() <= 5 }
                        .sortedBy { it.idx }
                Log.d("SearchPresenter","$topTen")


                /**
                 * CoordLocation( val type : String = "type",  val lon : Double = 0.0,  val lat : Double = 0.0,  val idx : String ="idx",  val name : String ="Coordname")
                 */
            }
            override fun onError(e: Throwable?)  = view.showMessage(e.toString())
            override fun onComplete() {}
        })
    }

    interface View : BaseView {
        fun showMessage(str : String)
    }
}