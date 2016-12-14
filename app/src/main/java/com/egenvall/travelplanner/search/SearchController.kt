package com.egenvall.travelplanner.search

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
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
import com.egenvall.travelplanner.common.threading.AndroidUiExecutor
import com.egenvall.travelplanner.extension.getDrawable
import com.egenvall.travelplanner.extension.setDrawable
import com.egenvall.travelplanner.extension.showSnackbar
import com.egenvall.travelplanner.model.StopLocation
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import kotlinx.android.synthetic.main.screen_search.view.*
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
    val TAG = "SearchController"


    //===================================================================================
    // Lifecycle methods and initialization
    //===================================================================================
    override fun onViewBound(view: View) {
        initInjection()
        view.search_expand_btn.setOnClickListener {toggleExpandableView(view)}
        view.search_swap_btn.setOnClickListener { swapOriginDestination() }
        mRecyclerOrigin = view.origin_recycler
        mOriginAdapter = SearchAdapter(mutableListOf<StopLocation>()){ clickedOrigin(it) }

        mRecyclerOrigin.setHasFixedSize(false)
        mRecyclerOrigin.layoutManager = LinearLayoutManager(applicationContext)
        mRecyclerOrigin.adapter = mOriginAdapter


        mRecyclerDestination = view.destination_recycler
        mRecyclerDestination.setHasFixedSize(false)
        mRecyclerDestination.layoutManager = LinearLayoutManager(applicationContext)
        mDestAdapter = SearchAdapter(mutableListOf<StopLocation>()){ clickedDest(it) }
        mRecyclerDestination.adapter = mDestAdapter
        setTextFieldObservers(view)
    }

    fun swapOriginDestination(){
        Log.d(TAG,"Before : $mOrigin     ->      $mDestination")
        Log.d(TAG,"After : $mDestination     ->      $mOrigin")
        view?.origin_edt?.setText(mDestination.name)
        view?.desination_edt?.setText(mOrigin.name)

    }

    fun setEditTextName(name : String){
        val focusedEdt = view?.findFocus() as EditText
        focusedEdt.setText(name)
        view?.expandable_layout_origin_search?.collapse(true)
        view?.expandable_layout_destination_search?.collapse(true)
    }
    fun clickedOrigin(item : StopLocation){
        setEditTextName(item.name)
        mOrigin = item
        Log.d(TAG,"Set origin : $mOrigin")

    }

    fun clickedDest(item: StopLocation){
        setEditTextName(item.name)
        mDestination = item
        Log.d(TAG,"Set dest : $mDestination")
    }

    fun setTextFieldObservers(view : View){
        with(view.origin_edt) {
            setOnFocusChangeListener { view, b ->
                if (b) {
                    Log.d(TAG, "Got Focus")
                    setSelectAllOnFocus(true)
                    Observable.create(ObservableOnSubscribe<String> { sub ->
                        addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) = Unit
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                                if (s.length > 3) {
                                    removeTextChangedListener(this)
                                    sub.onNext(s.toString())
                                }

                            }
                        })
                    }).debounce(1000, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidUiExecutor().scheduler)
                            .subscribe({
                                writtenText ->
                                searchForLocation(writtenText,true)
                            })
                } else {
                    Log.d(TAG, "Lost Focus")
                }
            }
        }
            with(view.desination_edt){
                setOnFocusChangeListener { view, b ->
                    if (b){
                        Log.d(TAG,"Got Focus")
                        setSelectAllOnFocus(true)
                        Observable.create(ObservableOnSubscribe<String> { sub ->
                            addTextChangedListener(object : TextWatcher{
                                override fun afterTextChanged(s: Editable?) = Unit
                                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                                {
                                    if (s.length > 3){
                                        removeTextChangedListener(this)
                                        sub.onNext(s.toString())
                                    }

                                }
                            })
                        }).debounce(1000, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidUiExecutor().scheduler)
                                .subscribe({
                                    writtenText ->
                                    searchForLocation(writtenText,false)
                                })
                    }
                    else{
                        Log.d(TAG,"Lost Focus")
                    }
                }
        }
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
        //TODO HERE HOW

    }
}