package com.egenvall.travelplanner.network

import com.egenvall.travelplanner.model.AccessToken
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.TripResponseModel
import com.egenvall.travelplanner.model.VtResponseModel
import io.reactivex.Observable


interface Repository {
    fun generateAccessToken() : Observable<AccessToken>
    fun getLocationBySearch(searchTerm :String) : Observable<VtResponseModel>
    fun getNearbyAddress(lat : String, lon : String, destId: String)
    fun getTripByStops(origin : StopLocation, destination : StopLocation) : Observable<TripResponseModel>
    fun getTripsIdAndCoord(origin : StopLocation, destination : StopLocation) : Observable<TripResponseModel>
    fun getTripsCoordAndId(origin: StopLocation, destination: StopLocation) : Observable<TripResponseModel>
    fun getTripsCoordAndCoord(origin: StopLocation, destination: StopLocation) : Observable<TripResponseModel>
}