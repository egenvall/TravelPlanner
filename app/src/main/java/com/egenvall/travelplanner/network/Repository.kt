package com.egenvall.travelplanner.network

import com.egenvall.travelplanner.model.AccessToken
import com.egenvall.travelplanner.model.TripResponseModel
import com.egenvall.travelplanner.model.VtResponseModel
import io.reactivex.Observable


interface Repository {
    fun generateAccessToken() : Observable<AccessToken>
    fun getLocationBySearch(searchTerm :String) : Observable<VtResponseModel>
    fun getSingleLocationByInput(input: String): Observable<VtResponseModel>
    fun getNearbyAddress(lat : String, lon : String, destId: String)
    fun getTripByStops(originId :String, destId: String) : Observable<TripResponseModel>
}