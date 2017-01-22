package com.egenvall.travelplanner.tripdetail

import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.base.presentation.BaseController
import com.egenvall.travelplanner.common.injection.component.DaggerTripViewComponent
import com.egenvall.travelplanner.common.injection.component.TripViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.extension.showSnackbar
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.Trip
import javax.inject.Inject


class TripController(val origin : StopLocation = StopLocation(), val dest : StopLocation = StopLocation()) : BaseController<TripPresenter.View, TripPresenter>(),
        TripPresenter.View{

    val TAG = "TripController"
    private lateinit var tripViewComponent: TripViewComponent
    override val passiveView: TripPresenter.View = this
    @Inject override lateinit var presenter: TripPresenter
    @LayoutRes override val layoutResId: Int = R.layout.screen_trip_overview

    override fun onViewBound(view: View) {
        initInjection()
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.searchForTripByLocations(origin,dest)
    }

//===================================================================================
// Dependency injection
//===================================================================================

    override fun initInjection() {
        val act = activity as AppCompatActivity
        tripViewComponent = DaggerTripViewComponent.builder()
                .appComponent((act.application as TravelPlanner).appComponent)
                .activityModule(ActivityModule(act))
                .build()
        tripViewComponent.inject(this)
    }



//===================================================================================
// View methods
//===================================================================================
    override fun showMessage(str: String) {
        view?.showSnackbar(str)
    }

    override fun setTripResults(list: List<Trip>) {
        for (t in list){
            Log.d(TAG,"-------Trip  Byten: ${t.Leg.filter { it.type != "WALK" }.size-1}----------")
            for (l in t.Leg.filter { it.type != "WALK" }){
                Log.d(TAG, ": [${l.Origin.name} - ${l.Destination.name} : ${l.Origin.time} - ${l.Destination.time}")

            }
            Log.d(TAG,"---------B-----------")

        }
    }
}