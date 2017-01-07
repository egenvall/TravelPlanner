package com.egenvall.travelplanner

import com.egenvall.travelplanner.common.threading.AndroidUiExecutor
import com.egenvall.travelplanner.common.threading.RxIoExecutor
import com.egenvall.travelplanner.model.*
import com.egenvall.travelplanner.network.Repository
import com.egenvall.travelplanner.search.SearchPresenter
import com.egenvall.travelplanner.search.SearchTripByStopsUsecase
import com.egenvall.travelplanner.search.SearchUsecase
import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import rx.Observable
import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers


class SearchPresenterTest {


    val mockTrip = mock<SearchTripByStopsUsecase>()
    val mockRepo = mock<Repository>()
    lateinit var searchUsecase : SearchUsecase
    val mockC = mock<SearchUsecase>()

    lateinit var presenter : SearchPresenter
    val mockView = mock<SearchPresenter.View>()

 @Before
    fun setUp(){
        RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
            override fun getMainThreadScheduler(): Scheduler {
                return Schedulers.immediate() // or .test()
            }
        })

        RxJavaHooks.setOnIOScheduler { scheduler1 -> Schedulers.immediate() }

        searchUsecase = SearchUsecase(mockRepo, AndroidUiExecutor(), RxIoExecutor())
        presenter = SearchPresenter(searchUsecase,mockTrip)
        presenter.viewAttached(mockView)
    }

    /**
     * The custom JSON parser handles ill formatted responses from the server,
     * i.e when the List<StopLocation> is actually an object in the JSON.
     */
    @Test
    fun successfulLocationResponse(){
        val resp = VtResponseModel(LocationList(null,null, listOf(StopLocation(idx = "1")), listOf(CoordLocation(idx = "2"))))
        whenever(mockRepo.getLocationBySearch(any())).thenReturn(Observable.just(resp))
        presenter.searchForLocation("Lantmilsgatan",true)
        verify(mockRepo,times(1)).getLocationBySearch("Lantmilsgatan")
        verify(mockView,times(1)).setSearchResults(any(), any()) //Wanted but not invoked
    }

    /**
     * Error from the server. Could be 401, 400 or whatever the API desires.
     */
    @Test
    fun errorLocationResponse(){
        val resp = VtResponseModel(LocationList("ErrorType","ErrorMessage", null,null))
        whenever(mockRepo.getLocationBySearch(any())).thenReturn(Observable.just(resp))
        presenter.searchForLocation("Lantmilsgatan",true)
        verify(mockRepo,times(1)).getLocationBySearch("Lantmilsgatan")
        verify(mockView,times(1)).showMessage(any())
    }
    @Test
    fun successfulTripResponse(){
        val tripResponse = TripResponseModel(
                TripList(null,null, listOf(Trip(
                        listOf(Leg(
                                TripEndpoint(null,null,null,"First","1337","HP",null,null),
                                TripEndpoint(null,null,null,"Second","1338","HP",null,null),"","","","",""
                                )),""
                )))
        )
       /* whenever(mockRepo.getTripByStops(any(),any())).thenReturn(Observable.just(tripResponse))
        whenever(mockRepo.getTripsCoordAndCoord(any(), any())).thenReturn(Observable.just(tripResponse))
        whenever(mockRepo.getTripsCoordAndId(any(),any())).thenReturn(Observable.just(tripResponse))
        whenever(mockRepo.getTripsIdAndCoord(any(),any())).thenReturn(Observable.just(tripResponse))
        presenter.searchForTripByLocations(StopLocation("1",type = "STOP"), StopLocation("2",type = "STOP"))
        verify(mockRepo,times(1)).getTripByStops(any(), any())*/


    }

    @After
    fun tearDown(){
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
        AndroidSchedulers.reset();
        Schedulers.reset();
    }

}