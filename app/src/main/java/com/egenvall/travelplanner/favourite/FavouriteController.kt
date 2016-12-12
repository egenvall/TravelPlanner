package com.egenvall.travelplanner.favourite

import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.egenvall.travelplanner.TravelPlanner
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.base.presentation.BaseController
import com.egenvall.travelplanner.common.injection.component.DaggerFavouriteViewComponent
import com.egenvall.travelplanner.common.injection.component.FavouriteViewComponent
import com.egenvall.travelplanner.common.injection.module.ActivityModule
import com.egenvall.travelplanner.extension.showSnackbar
import javax.inject.Inject


class FavouriteController : BaseController<FavouritePresenter.View, FavouritePresenter>(),
        FavouritePresenter.View{

    @LayoutRes override val layoutResId: Int = R.layout.screen_favourites
    private lateinit var favouriteViewComponent : FavouriteViewComponent
    override val passiveView: FavouritePresenter.View = this
    @Inject override lateinit var presenter: FavouritePresenter


    override fun onViewBound(view: View) {
        initInjection()
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
}