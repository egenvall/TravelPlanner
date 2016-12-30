package com.egenvall.travelplanner

import com.egenvall.travelplanner.model.CoordLocation
import com.egenvall.travelplanner.model.LocationList
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.VtResponseModel
import com.egenvall.travelplanner.network.Repository
import com.egenvall.travelplanner.search.SearchPresenter
import com.egenvall.travelplanner.search.SearchTripByStopsUsecase
import com.egenvall.travelplanner.search.SearchUsecase
import com.nhaarman.mockito_kotlin.*
import  com.nhaarman.mockito_kotlin.whenever
import org.junit.After
import org.junit.Before
import org.junit.Test
import io.reactivex.Observable
import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers


class SearchPresenterTest {

    val mockService = mock<Repository>()
    val mockUsecase = mock<SearchUsecase>()
    val mockTripUsecase = mock<SearchTripByStopsUsecase>()
    val presenter = SearchPresenter(mockUsecase,mockTripUsecase)
    val mockView = mock<SearchPresenter.View>()


    @Before
    fun setUp(){
        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler(): Scheduler {
                return Schedulers.immediate() // or .test()
            }
        })

        RxJavaHooks.setOnIOScheduler { scheduler1 -> Schedulers.immediate() }

    }

    @After
    fun tearDown(){
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
        AndroidSchedulers.reset();
        Schedulers.reset();
    }
}