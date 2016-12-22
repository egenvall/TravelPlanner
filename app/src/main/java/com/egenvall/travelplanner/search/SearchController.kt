package com.egenvall.travelplanner.search

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.adapter.SearchAdapter
import com.egenvall.travelplanner.base.presentation.BaseController
import com.egenvall.travelplanner.common.injection.component.DaggerSearchViewComponent
import com.egenvall.travelplanner.common.injection.component.SearchViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.extension.*
import com.egenvall.travelplanner.model.SearchPair
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.Trip
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.screen_search.view.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.Subscriptions
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class SearchController : BaseController<SearchPresenter.View, SearchPresenter>(),
        SearchPresenter.View {
    private lateinit var searchViewComponent: SearchViewComponent
    override val passiveView: SearchPresenter.View = this
    @Inject override lateinit var presenter: SearchPresenter
    @LayoutRes override val layoutResId: Int = R.layout.screen_search
    lateinit private var mRecyclerOrigin : RecyclerView
    lateinit private var mRecyclerDestination : RecyclerView
    lateinit private var mOriginAdapter : SearchAdapter
    lateinit private var mDestAdapter : SearchAdapter
    private var mOrigin : StopLocation? = null
    private var mDestination : StopLocation? = null
    private var editOrgSubscription = Subscriptions.unsubscribed()
    private var editDestSubscription = Subscriptions.unsubscribed()
    private var animCount = 0


    lateinit var originText : EditText
    lateinit var destText : EditText
    val TAG = "SearchController"

//===================================================================================
//     Lifecycle methods and initialization
//===================================================================================
    override fun onViewBound(view: View) {
        initInjection()
        view.search_expand_btn.setOnClickListener {toggleExpandableView(view)}
        view.search_swap_btn.setOnClickListener { swapOriginDestination() }
        originText = view.origin_edt
        destText = view.desination_edt

        /**
         * Origin Recycler
         */
        mRecyclerOrigin = view.origin_recycler
        mOriginAdapter = SearchAdapter(mutableListOf<StopLocation>()){ clickedOrigin(it) }
        mRecyclerOrigin.setHasFixedSize(false)
        mRecyclerOrigin.layoutManager = LinearLayoutManager(applicationContext)
        mRecyclerOrigin.adapter = mOriginAdapter

        /**
         * Destination recycler
         */
        mRecyclerDestination = view.destination_recycler
        mRecyclerDestination.setHasFixedSize(false)
        mRecyclerDestination.layoutManager = LinearLayoutManager(applicationContext)
        mDestAdapter = SearchAdapter(mutableListOf<StopLocation>()){ clickedDest(it) }
        mRecyclerDestination.adapter = mDestAdapter
        view.search_button.setOnClickListener {
            presenter.searchForTripByLocations(mOrigin!!,mDestination!!)
        }

        /**
         * Start subscription for edittexts
         */
        startOriginSubscription()
        startDestinationSubscription()
    }

    fun getEditTextSub(edit : EditText, origin : Boolean) : Subscription{
        edit.setSelectAllOnFocus(true)
        return RxTextView.textChanges(edit)
                .skip(1)
                .map { s -> s.toString()}
                .throttleLast(200,TimeUnit.MILLISECONDS) //Emit only the last item in 200ms interval
                .debounce (750, TimeUnit.MILLISECONDS)   //Emit the last item if 750ms has passed with no more emits
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ finalText ->
                    hideSearchButton()
                    if (finalText.length >=3){
                        searchForLocation(finalText,origin)
                    }
                })
    }

    fun startOriginSubscription(){
        editOrgSubscription = getEditTextSub(originText,true)
    }
    fun startDestinationSubscription() {
        editDestSubscription = getEditTextSub(destText,false)
    }


//===================================================================================
// Presenter Methods
//===================================================================================
    fun searchForLocation(searchTerm : String, originRequested : Boolean){
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
        presenter.searchForLocation(searchTerm,originRequested)
    }

//===================================================================================
// UI Elements and Interaction
//===================================================================================

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
        editDestSubscription.unsubscribe()
        destText.setText(item.name)
        view?.expandable_layout_destination_search?.collapse(true)
        mDestination = item
        checkOriginAndDest()
        startDestinationSubscription()
    }


    fun clickedOrigin(item : StopLocation){
        editOrgSubscription.unsubscribe()
        originText.setText(item.name)
        view?.expandable_layout_origin_search?.collapse(true)
        mOrigin = item
        checkOriginAndDest()
        startOriginSubscription()
    }

    fun toggleExpandableView(view:View){
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
        editOrgSubscription.unsubscribe()
        editDestSubscription.unsubscribe()
        val tmpSwap = mOrigin
        mOrigin = mDestination
        mDestination = tmpSwap
        originText.setText(mOrigin?.name)
        destText.setText(mDestination?.name)
        startOriginSubscription()
        startDestinationSubscription()
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

    override fun setSearchResults(list: List<StopLocation>, wasOrigin : Boolean) {
        if (list.isEmpty()) {
            view?.showSnackbar("No results found")
            return
        }
        if (wasOrigin) {
            view?.expandable_layout_origin_search?.expand()
            with(mOriginAdapter){
                locationList = list
                notifyDataSetChanged()
            }
        }
        else {
            view?.expandable_layout_destination_search?.expand()
            with(mDestAdapter){
                locationList = list
                notifyDataSetChanged()
            }
        }

    }

    override fun setTripResults(list: List<Trip>) {
        for (t in list){
            Log.d(TAG,"-------Trip  Byten: ${t.Leg.filter { it.type != "WALK" }.size-1}----------")
            for (l in t.Leg.filter { it.type != "WALK" }){
                Log.d(TAG, ": [${l.Origin.name} - ${l.Destination.name} : ${l.Origin.time} - ${l.Destination.time}")

            }
            Log.d(TAG,"--------------------")

        }
    }

    override fun setSearchHistory(list: List<SearchPair>) {
        for(p in list){
            Log.d(TAG,"${p.orgPlusDestId}")
        }
        Log.d(TAG,"-----------")
    }
}