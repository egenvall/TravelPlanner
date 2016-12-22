package com.egenvall.travelplanner.network

import com.egenvall.travelplanner.model.AccessToken
import com.egenvall.travelplanner.model.StopLocation
import com.egenvall.travelplanner.model.TripResponseModel
import com.egenvall.travelplanner.model.VtResponseModel
import io.reactivex.Observable
import javax.inject.Inject


class RestDataSource @Inject constructor(private val service: VtService) : Repository{
    private var accessToken = AccessToken()
    private var expiry :Long = -1
    private val tokenUrl = "https://api.vasttrafik.se/token"

    fun isTokenValid(token: AccessToken) : Boolean{
        if (expiry > System.currentTimeMillis()) {
            return true
        }

    return false
    }
    override fun generateAccessToken(): Observable<AccessToken> {
        return service.getAccessToken(tokenUrl,"client_credentials","2").doOnNext {
            token ->
            accessToken = token
            expiry = token.expires_in*1000+System.currentTimeMillis()
        }
    }

    private fun checkValidToken() : Observable<AccessToken> {
        return Observable.just(isTokenValid(accessToken)).flatMap { valid ->
            if (valid )Observable.just(accessToken)
            else generateAccessToken()
        }}
    private fun formatTokenString(token: AccessToken) : String{
        return token.token_type+" "+token.access_token
    }

    override fun getLocationBySearch(searchTerm : String): Observable<VtResponseModel> {
        return checkValidToken().flatMap { token -> service.getLocationByInput(formatTokenString(token),searchTerm) }
    }

    override fun getTripByStops(origin: StopLocation, destination: StopLocation): Observable<TripResponseModel> {
        return checkValidToken().flatMap { token -> service.getTripIdToId(formatTokenString(token),origin.id,destination.id) }
    }

    override fun getTripsIdAndCoord(origin: StopLocation, destination: StopLocation): Observable<TripResponseModel> {
        return checkValidToken().flatMap { token -> service.getTripIdAndCoord(formatTokenString(token)
                ,origin.id,destination.lat.toString(),destination.lon.toString(),destination.name) }
    }

    override fun getTripsCoordAndId(origin : StopLocation, destination : StopLocation): Observable<TripResponseModel> {
        return checkValidToken().flatMap { token -> service.getTripCoordAndId(formatTokenString(token)
                ,origin.lat.toString(),origin.lon.toString(),origin.name,destination.id)}
    }

    override fun getTripsCoordAndCoord(origin: StopLocation, destination: StopLocation): Observable<TripResponseModel> {
        return checkValidToken().flatMap { token -> service.getTripCoordAndCoord(formatTokenString(token)
                ,origin.lat.toString(),origin.lon.toString(),origin.name,destination.lat.toString(),
                destination.lon.toString(),destination.name)}
    }


    override fun getNearbyAddress(lat: String, lon: String, destId: String) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

