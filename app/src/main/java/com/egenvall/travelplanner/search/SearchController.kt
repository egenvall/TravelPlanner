package com.egenvall.travelplanner.search

import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.adapter.SearchHistoryAdapter
import com.egenvall.travelplanner.base.presentation.BaseController
import com.egenvall.travelplanner.common.injection.component.DaggerSearchViewComponent
import com.egenvall.travelplanner.common.injection.component.SearchViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.extension.*
import com.egenvall.travelplanner.model.SearchPair
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.Trip
import com.egenvall.travelplanner.tripdetail.TripController
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import kotlinx.android.synthetic.main.screen_search.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Base UI representation of the search functionality.
 * Shows search history and TextViews with the currently specified origin and destination for a
 * trip to be searched
 */
class SearchController : BaseController<SearchPresenter.View, SearchPresenter>(),
        SearchPresenter.View, SearchModuleController.TargetTitleListener {
    override fun onSelectedStop(stop: StopLocation, origin: Boolean) {
        if (origin) {
            Log.d(TAG, "Origin received: $stop")
            clickedOrigin(stop)
        }
        else {
            Log.d(TAG, "Destination received: $stop")
            clickedDest(stop)
        }
    }
    private lateinit var searchViewComponent: SearchViewComponent
    override val passiveView: SearchPresenter.View = this
    @Inject override lateinit var presenter: SearchPresenter
    @LayoutRes override val layoutResId: Int = R.layout.screen_search
    lateinit private var mRecyclerHistory : RecyclerView
    lateinit private var mHistoryAdapter : SearchHistoryAdapter
    private var mOrigin : StopLocation? = null
    private var mDestination : StopLocation? = null
    lateinit var originText : TextView
    lateinit var destText : TextView
    val TAG = "Chilrtoot"

    //===================================================================================
//     Lifecycle methods and initialization
//===================================================================================
    override fun onViewBound(view: View) {
        retainViewMode = RetainViewMode.RETAIN_DETACH
        initInjection()
        view.search_expand_btn.setOnClickListener {toggleExpandableView(view)}
        view.search_swap_btn.setOnClickListener { swapOriginDestination() }
        originText = view.origin_edt
        destText = view.desination_edt

        /**
         * Search History
         */

        mRecyclerHistory = view.history_recycler
        mRecyclerHistory.setHasFixedSize(true)
        mRecyclerHistory.layoutManager = LinearLayoutManager(applicationContext)
        mHistoryAdapter = SearchHistoryAdapter(mutableListOf<SearchPair>()){clickedHistoryPair(it)}
        mRecyclerHistory.adapter = mHistoryAdapter

        view.search_button.setOnClickListener {presenter.searchForTripByLocations(mOrigin!!,mDestination!!)
        }

        val format = SimpleDateFormat("EEE ddMMM HH:mm", Locale.getDefault())

        view.datetime_button.text = format.format(Date())

        view.datetime_button.setOnClickListener {
            SingleDateAndTimePickerDialog.Builder(activity)
                    .bottomSheet()
                    .title("Simple")
                    .listener(object : SingleDateAndTimePickerDialog.Listener {
                        override fun onDateSelected(date: Date) {
                            Log.d(TAG,"Chose Date: ${date.toString()}")
                            view.datetime_button.text = format.format(date)
                        }
                    }).display()
        }


        originText.setOnClickListener { openSearchScreen(true)}
        destText.setOnClickListener { openSearchScreen(false) }
    }

    fun openSearchScreen(origin: Boolean){
        //Childrouter?
        router.pushController(RouterTransaction.with(SearchModuleController(this,origin))
                .pushChangeHandler(HorizontalChangeHandler())
                .popChangeHandler(HorizontalChangeHandler()))
    }


    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.getSearchHistory()
        Log.d(TAG,"Router backstack ${router.backstack}")
    }

//===================================================================================
// UI Elements and Interaction
//===================================================================================

    override fun searchForTrips(origin: StopLocation, dest: StopLocation){
        router.pushController(RouterTransaction.with(TripController(origin,dest))
                .pushChangeHandler(HorizontalChangeHandler())
                .popChangeHandler(HorizontalChangeHandler()))
    }
    fun clickedHistoryPair(pair : SearchPair){
        presenter.searchForTripByLocations(pair.origin,pair.destination)
    }
    fun hideSearchButton(){
        with(view?.search_button) {
            this?.animate()?.alpha(0.0f)?.setDuration(300)
                    ?.withEndAction { view?.search_button?.hide() }
        }

    }
    fun showSearchButton(){
        with(view?.search_button){
            this?.show()
            this?.alpha = 0.0f
            this?.animate()?.alpha(1.0f)?.setDuration(300)
        }
    }

    fun clickedDest(item: StopLocation){
        destText.setText(item.name)
        mDestination = item
        checkOriginAndDest()
    }


    fun clickedOrigin(item : StopLocation){
        originText.text = item.name
        mOrigin = item
        checkOriginAndDest()
    }

    fun toggleExpandableView(view: View){
        with(view.expandable_layout){
            if (isExpanded){
                collapse(true)
                view.search_expand_btn.setDrawable(view.getDrawable(R.drawable.ic_expand_more_white_36dp))
            }
            else {
                expand(true)
                view.search_expand_btn.setDrawable(view.getDrawable(R.drawable.ic_expand_less_white_36dp))
            }
        }
    }

//===================================================================================
// Helper Methods
//===================================================================================
    private fun checkOriginAndDest(){
        if ((mOrigin != null) and (mDestination!=null)) showSearchButton()
        else hideSearchButton()

    }

    fun swapOriginDestination(){
        val tmpSwap = mOrigin
        mOrigin = mDestination
        mDestination = tmpSwap
        originText.setText(mOrigin?.name)
        destText.setText(mDestination?.name)
    }
//===================================================================================
// Dependency injection
//===================================================================================

    override fun initInjection() {
        val act = activity as AppCompatActivity
        searchViewComponent = DaggerSearchViewComponent.builder()
                .appComponent((act.application as TravelPlanner).appComponent)
                .activityModule(ActivityModule(act))
                .build()
        searchViewComponent.inject(this)
    }

//===================================================================================
// View methods
//===================================================================================

    override fun showMessage(str : String) {
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

    override fun setSearchHistory(list: List<SearchPair>) {
        mHistoryAdapter.historyList = list
        mHistoryAdapter.notifyDataSetChanged()
    }
}