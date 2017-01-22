package com.egenvall.travelplanner.favourite

import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.adapter.FavouriteAdapter
import com.egenvall.travelplanner.base.presentation.BaseController
import com.egenvall.travelplanner.common.injection.component.DaggerFavouriteViewComponent
import com.egenvall.travelplanner.common.injection.component.FavouriteViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.extension.hide
import com.egenvall.travelplanner.extension.show
import com.egenvall.travelplanner.extension.showSnackbar
import com.egenvall.travelplanner.model.Favourite
import kotlinx.android.synthetic.main.screen_favourites.view.*
import java.math.BigInteger
import javax.inject.Inject


class FavouriteController : BaseController<FavouritePresenter.View, FavouritePresenter>(),
        FavouritePresenter.View{

    @LayoutRes override val layoutResId: Int = R.layout.screen_favourites
    private lateinit var favouriteViewComponent : FavouriteViewComponent
    private lateinit var favouriteRecycler : RecyclerView
    private lateinit var favouriteAdapter : FavouriteAdapter
    override val passiveView: FavouritePresenter.View = this
    @Inject override lateinit var presenter: FavouritePresenter
    val TAG = "FavouriteController"



    override fun onViewBound(view: View) {
        initInjection()
        favouriteRecycler = view.fav_recycler
        favouriteAdapter = FavouriteAdapter(mutableListOf<Favourite>()){favClicked(it)}
        favouriteRecycler.setHasFixedSize(false)
        favouriteRecycler.layoutManager = StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL)
        favouriteRecycler.adapter = favouriteAdapter

    }
    

    override fun onAttach(view: View) {
        super.onAttach(view)
        presenter.getFavourites()
    }



    fun favClicked(favourite : Favourite){
        Log.d(TAG,"Clicked ${favourite.nickName}")
    }
    override fun initInjection() {
        val act = activity as AppCompatActivity
        favouriteViewComponent = DaggerFavouriteViewComponent.builder()
                .appComponent((act.application as TravelPlanner).appComponent)
                .activityModule(ActivityModule(act))
                .build()
        favouriteViewComponent.inject(this)
    }

    //===================================================================================
    // View methods
    //===================================================================================

    override fun showMessage(str : String) {
        view?.showSnackbar(str)
    }
    override fun setFavourites(list: List<Favourite>) {
        favouriteAdapter.favList = list
        favouriteAdapter.notifyDataSetChanged()
        if (list.isEmpty()) {
            view?.no_favs_text?.show()
            view?.no_fav_icon?.show()
        }
        else {
            view?.no_favs_text?.hide()
            view?.no_fav_icon?.hide()
        }
    }
}