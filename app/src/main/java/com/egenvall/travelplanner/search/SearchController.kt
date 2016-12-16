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
import com.egenvall.travelplanner.extension.getDrawable
import com.egenvall.travelplanner.extension.setDrawable
import com.egenvall.travelplanner.extension.showSnackbar
import com.egenvall.travelplanner.model.StopLocation
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.screen_search.view.*
import rx.Subscription
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

    lateinit private var mOrigin : StopLocation
    lateinit private var mDestination : StopLocation
    private var editOrgSubscription = Subscriptions.unsubscribed()
    private var editDestSubscription = Subscriptions.unsubscribed()


    lateinit var originText : EditText
    lateinit var destText : EditText
    val TAG = "SearchController"


    //===================================================================================
    // Lifecycle methods and initialization
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

        /**
         * Start subscription for edittexts
         */
        startOriginSubscription()
        startDestinationSubscription()
    }

    fun swapOriginDestination(){
        editOrgSubscription.unsubscribe()
        editDestSubscription.unsubscribe()
        val tmpSwap = mOrigin
        mOrigin = mDestination
        mDestination = tmpSwap
        originText.setText(mOrigin.name)
        destText.setText(mDestination.name)
        startOriginSubscription()
        startDestinationSubscription()
    }


    fun getEditTextSub(view : EditText, origin : Boolean) : Subscription{
        view.setSelectAllOnFocus(true)
        return RxTextView.textChanges(view)
                .skip(1)
                .map { s -> s.toString()}
                .throttleLast(200,TimeUnit.MILLISECONDS) //Emit only the last item in 200ms interval
                .debounce (750, TimeUnit.MILLISECONDS)   //Emit the last item if 750ms has passed with no more emits
                .subscribe({ finalText ->
                    if (finalText.isNotBlank() and (finalText.length >=3)){
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


    fun clickedDest(item: StopLocation){
        editDestSubscription.unsubscribe()
        destText.setText(item.name)
        view?.expandable_layout_destination_search?.collapse(true)
        mDestination = item
        startDestinationSubscription()
    }

    fun clickedOrigin(item : StopLocation){
        editOrgSubscription.unsubscribe()
        originText.setText(item.name)
        view?.expandable_layout_origin_search?.collapse(true)
        mOrigin = item
        startOriginSubscription()
    }


    fun searchForLocation(searchTerm : String, originRequested : Boolean){
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
        presenter.searchForLocation(searchTerm,originRequested)
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
        if (wasOrigin){
            view?.expandable_layout_origin_search?.expand()
            with(mOriginAdapter){
                locationList = list
                notifyDataSetChanged()
            }
        }
        else{
            view?.expandable_layout_destination_search?.expand()
            with(mDestAdapter){
                locationList = list
                notifyDataSetChanged()
            }
        }
    }
}