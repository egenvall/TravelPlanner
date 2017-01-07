package com.egenvall.travelplanner.favourite

import com.egenvall.travelplanner.base.presentation.BasePresenter
import com.egenvall.travelplanner.base.presentation.BaseView
import com.egenvall.travelplanner.common.injection.scope.PerScreen
import io.reactivex.observers.DisposableObserver
import javax.inject.Inject



@PerScreen
class FavouritePresenter @Inject constructor(private val getUsecase: GetFavouritesUsecase) : BasePresenter<FavouritePresenter.View>() {
    override fun onViewAttached() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onViewDetached() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Called when view is detached
     */
    override fun unsubscribe() {
        getUsecase.unsubscribe()
    }

    fun getFavourites() {
        getUsecase.getFavourites(object : DisposableObserver<String>(){
            override fun onNext(value : String) = view.showMessage(value)
            override fun onError(e: Throwable?)  = view.showMessage(e.toString())
            override fun onComplete() {}
        })
    }


    /**
     * Interface for Controllers to implement
     */
    interface View : BaseView {
        fun showMessage(str : String)
    }
}