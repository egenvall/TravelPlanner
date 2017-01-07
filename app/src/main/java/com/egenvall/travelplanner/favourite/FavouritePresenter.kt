package com.egenvall.travelplanner.favourite

import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import rx.Observer
import javax.inject.Inject



@PerScreen
class FavouritePresenter @Inject constructor(private val getUsecase: GetFavouritesUsecase) : BasePresenter<FavouritePresenter.View>() {
    override fun onViewAttached() {
    }

    override fun onViewDetached() {
    }

    /**
     * Called when view is detached
     */
    override fun unsubscribe() {
        getUsecase.unsubscribe()
    }

    fun getFavourites() {
        getUsecase.getFavourites(object : Observer<String>{
            override fun onNext(value : String) = view.showMessage(value)
            override fun onError(e: Throwable?)  = view.showMessage(e.toString())
            override fun onCompleted() {}
        })
    }


    /**
     * Interface for Controllers to implement
     */
    interface View : BaseView {
        fun showMessage(str : String)
    }
}