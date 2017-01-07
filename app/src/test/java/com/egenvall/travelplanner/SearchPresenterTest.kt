package com.egenvall.travelplanner

import com.egenvall.travelplanner.common.threading.AndroidUiExecutor
import com.egenvall.travelplanner.common.threading.RxIoExecutor
import com.egenvall.travelplanner.model.CoordLocation
import com.egenvall.travelplanner.model.LocationList
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.VtResponseModel
import com.egenvall.travelplanner.network.Repository
import com.egenvall.travelplanner.search.SearchPresenter
import com.egenvall.travelplanner.search.SearchTripByStopsUsecase
import com.egenvall.travelplanner.search.SearchUsecase
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test


class SearchPresenterTest {


    val mockTrip = mock<SearchTripByStopsUsecase>()
    val mockRepo = mock<Repository>()
    lateinit var mockSearchUc : SearchUsecase

    lateinit var presenter : SearchPresenter
    val mockView = mock<SearchPresenter.View>()
    @Before
    fun setUp(){
    }

    internal class TestUI : AndroidUiExecutor(){
        override val scheduler : Scheduler
            get() = TestScheduler()
    }
    internal class TestIO : RxIoExecutor(){
        override val scheduler: Scheduler
            get() = TestScheduler()
    }

    @Test
    fun test(){
        val ui = TestUI()
        val io = TestIO()
        mockSearchUc = SearchUsecase(mockRepo, ui, io)
        presenter = SearchPresenter(mockSearchUc,mockTrip)
        presenter.viewAttached(mockView)


        val resp = VtResponseModel(LocationList(null,null, listOf(StopLocation()), listOf(CoordLocation())))
        whenever(mockRepo.getLocationBySearch(any())).thenReturn(Observable.just(resp))
        presenter.searchForLocation("Lantmilsgatan",true)
        verify(mockRepo,times(1)).getLocationBySearch("Lantmilsgatan")
        verify(mockView,times(1)).setSearchResults(any(), any()) //Wanted but not invoked
    }

}