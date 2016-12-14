package com.egenvall.travelplanner.search

import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.TravelPlanner
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
import android.widget.ArrayAdapter




class SearchController : BaseController<SearchPresenter.View, SearchPresenter>(),
        SearchPresenter.View {
    private lateinit var searchViewComponent: SearchViewComponent
    override val passiveView: SearchPresenter.View = this
    @Inject override lateinit var presenter: SearchPresenter
    @LayoutRes override val layoutResId: Int = R.layout.screen_search
    private val TAG = "SearchController"


    //===================================================================================
    // Lifecycle methods and initialization
    //===================================================================================
    override fun onViewBound(view: View) {
        initInjection()
        view.search_expand_btn.setOnClickListener {toggleExpandableView(view)}
        setTextFieldObservers(view)
    }

    fun setTextFieldObservers(view : View){
        with(view.origin_edt){
            Observable.create(ObservableOnSubscribe<String> { sub ->
                addTextChangedListener(object : TextWatcher{
                    override fun afterTextChanged(s: Editable?) = Unit
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                            = sub.onNext(s.toString())
                })
            }).debounce(1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidUiExecutor().scheduler)
                    .subscribe({
                        writtenText ->
                        searchForLocation(writtenText)
                    })}

    }
    fun searchForLocation(searchTerm : String){
        presenter.searchForLocation(searchTerm)
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

    override fun setSearchResults(list: MutableList<StopLocation>) {
        Log.d(TAG,"Result  $list")
    }
}