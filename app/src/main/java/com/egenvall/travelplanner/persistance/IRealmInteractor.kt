package com.egenvall.travelplanner.persistance

import com.egenvall.travelplanner.model.Favourite
import com.egenvall.travelplanner.model.SearchPair
import com.egenvall.travelplanner.model.StopLocation


interface IRealmInteractor {

    fun addFavourite(origin : StopLocation, dest : StopLocation, nick: String, bg : String)
    fun getFavourites() : List<Favourite>
    fun addSearchHistory(origin: StopLocation, dest: StopLocation)
    fun getSearchHistory(): List<SearchPair>
    fun removeFromSearchHistory(pair: SearchPair)
}