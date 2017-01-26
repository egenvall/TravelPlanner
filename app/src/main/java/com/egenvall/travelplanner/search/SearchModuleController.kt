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
import com.bluelinelabs.conductor.Controller
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.adapter.SearchAdapter
import com.egenvall.travelplanner.base.presentation.BaseController
import com.egenvall.travelplanner.common.injection.component.DaggerSearchModuleViewComponent
import com.egenvall.travelplanner.common.injection.component.SearchModuleViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.extension.showSnackbar
import com.egenvall.travelplanner.model.StopLocation
import com.jakewharton.rxbinding.widget.RxTextView
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import jp.wasabeef.recyclerview.animators.LandingAnimator
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.screen_searchmodule.view.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.Subscriptions
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.ViewTreeObserver



/**
 * Modular Search Functionality performing the actual search for an address or stop based on
 * user input
 */
class SearchModuleController (val target : Controller = SearchRouterController(), val origin : Boolean = false ) : BaseController<SearchModulePresenter.View, SearchModulePresenter>(),
        SearchModulePresenter.View {

    //TODO T should <T  : Controller & TargetTitleListeener>
    private lateinit var searchViewComponent: SearchModuleViewComponent
    override val passiveView: SearchModulePresenter.View = this
    @LayoutRes override val layoutResId: Int = R.layout.screen_searchmodule
    @Inject override lateinit var presenter: SearchModulePresenter
    private var editTextSub = Subscriptions.unsubscribed()
    lateinit private var resultRecycler : RecyclerView
    lateinit private var resultAdapter : SearchAdapter
    val TAG = "SMODULE"


    interface TargetTitleListener{
        fun onSelectedStop(stop: StopLocation,origin: Boolean)
    }

    override fun onViewBound(view: View) {
        initInjection()
        resultRecycler = view.searchresult_recycler
        resultAdapter = SearchAdapter(listOf<StopLocation>()){ clickedStopItem(it) }
        resultRecycler.setHasFixedSize(false)
        resultRecycler.layoutManager = LinearLayoutManager(applicationContext)
        resultRecycler.adapter = resultAdapter
        resultRecycler.itemAnimator = FadeInAnimator()
        resultRecycler.itemAnimator.addDuration = 150
        resultRecycler.viewTreeObserver.addOnGlobalLayoutListener({
            Log.d(TAG,"Finished layout")
            //At this point the layout is complete and the
            //dimensions of recyclerView and any child views are known.
        })
        setTargetController(target)
        editTextSub = getEditTextSub(view.searchmodule_edt)
        view.back_button.setOnClickListener { clickedBack() }
        view.searchmodule_edt.requestFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    fun getEditTextSub(edit : EditText) : Subscription {
        edit.setSelectAllOnFocus(true)
        return RxTextView.textChanges(edit)
                //.skip(1)
                .map { s -> s.toString()}
                .throttleLast(200, TimeUnit.MILLISECONDS) //Emit only the last item in 200ms interval
                .debounce (1000, TimeUnit.MILLISECONDS)   //Emit the last item if 1000ms has passed with no more emits
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ finalText ->
                    if (finalText.length >=3){
                        searchForLocation(finalText)
                    }
                })
    }

    fun clickedBack(){
        editTextSub.unsubscribe()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
        resultRecycler.adapter = null
        router.popController(this)
    }

    override fun handleBack(): Boolean {
        clickedBack()
        return true
    }

    override fun showMessage(str: String) {
        view?.showSnackbar(str)
    }

    fun searchForLocation(searchTerm : String) {
        Log.d(TAG,"Searching again")
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)
        presenter.searchForLocation(searchTerm)
    }

    fun clickedStopItem(item : StopLocation){
        editTextSub.unsubscribe()
        val targetController = targetController
        if (targetController != null){
            (targetController as TargetTitleListener).onSelectedStop(item,origin)
            router.popController(this)
        }
    }

//===================================================================================
// Dependency injection
//===================================================================================

    override fun initInjection() {
        val act = activity as AppCompatActivity
        searchViewComponent = DaggerSearchModuleViewComponent.builder()
                .appComponent((act.application as TravelPlanner).appComponent)
                .activityModule(ActivityModule(act))
                .build()
        searchViewComponent.inject(this)
    }

//===================================================================================
// View methods
//===================================================================================

    override fun setSearchResult(list : List<StopLocation>){
        if (list.isEmpty()) {
            view?.showSnackbar("No results found")
        }
        else {
                resultAdapter.setList(list)
        }
    }

}