package com.egenvall.travelplanner.tripdetail

import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.adapter.TripOverviewAdapter
import com.egenvall.travelplanner.base.presentation.BaseController
import com.egenvall.travelplanner.common.injection.component.DaggerTripViewComponent
import com.egenvall.travelplanner.common.injection.component.TripViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.extension.showSnackbar
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.Trip
import kotlinx.android.synthetic.main.screen_trip_overview.view.*
import javax.inject.Inject


class TripController(val origin : StopLocation = StopLocation(), val dest : StopLocation = StopLocation()) : BaseController<TripPresenter.View, TripPresenter>(),
        TripPresenter.View{

    val TAG = "TripController"
    private lateinit var tripViewComponent: TripViewComponent
    override val passiveView: TripPresenter.View = this
    @Inject override lateinit var presenter: TripPresenter
    @LayoutRes override val layoutResId: Int = R.layout.screen_trip_overview
    lateinit var tripRecycler : RecyclerView
    lateinit var tripAdapter : TripOverviewAdapter

    override fun onViewBound(view: View) {
        initInjection()
        tripRecycler = view.tripoverview_recycler
        tripRecycler.setHasFixedSize(false)
        tripRecycler.layoutManager = LinearLayoutManager(applicationContext)
        tripAdapter = TripOverviewAdapter(mutableListOf<Trip>()){ clickedTrip(it)}
        tripRecycler.adapter = tripAdapter

        //Set the header text to the name of then destination
        view.trip_back_button.setOnClickListener {
            tripRecycler.adapter = null
            router.popController(this)
        }
        view.tripoverview_text.text = dest.name
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.searchForTripByLocations(origin,dest)
    }


    fun clickedTrip(trip : Trip){

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
        tripAdapter.tripList = list
        tripAdapter.notifyDataSetChanged()
    }
}