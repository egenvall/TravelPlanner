package com.egenvall.travelplanner.network

import com.egenvall.travelplanner.model.AccessToken
import com.egenvall.travelplanner.model.TripResponseModel
import com.egenvall.travelplanner.model.VtResponseModel
import io.reactivex.Observable
import retrofit2.http.*


interface VtService {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded", "Authorization:" + " Basic YU04YW9uNkJJYkViMTNVdm9rVmQ3TlJjc2pzYTpWMWRzd3Vsb3l6cXUyWFRqQmlmWXNmMGdVOXdh")
    @POST
     fun getAccessToken(@Url url : String,
            @Field("grant_type") client_credentials: String,
            @Field("scope") scopeId: String

    ): Observable<AccessToken>

    @GET("location.name")
     fun getLocationByInput(
            @Header("Authorization") tokenTypeAccessToken: String,
            @Query("input") input: String,
            @Query("format") json: String = "json"

    ): Observable<VtResponseModel>


    @GET("location.nearbyaddress")
     fun getNearbyAddress(
            @Header("Authorization") tokenTypeAccessToken: String,
            @Query("originCoordLat") lat: String,
            @Query("originCoordLong") lon: String,
            @Query("format") json: String

    ): Observable<VtResponseModel>

    @GET("trip")
    fun getTripIdToId(@Header("Authorization") tokenTypeAccessToken: String,
                      @Query("originId") originId : String,
                      @Query("destId") destId : String,
                      @Query("numTrips") numberOfTrips : Int = 8,
                      @Query("format") json : String = "json"
                      ) : Observable<TripResponseModel>
/*
    @GET("trip")
     fun getTripCoordAndId(
            @Header("Authorization") tokenTypeAccessToken: String,
            @Query("originCoordLat") originLat: String,
            @Query("originCoordLong") originLong: String,
            @Query("originCoordName") originAddressName: String,
            @Query("maxWalkDist") distance: Int,
            @Query("destId") destinationId: String,
            @Query("numTrips") numberOfTrips: Int,
            @Query("format") json: String

    ): Observable<VtTripResponseModel>

    @GET("trip")
     fun getTripCoordAndCoord(
            @Header("Authorization") tokenTypeAccessToken: String,
            @Query("originCoordLat") originLat: String,
            @Query("originCoordLong") originLong: String,
            @Query("originCoordName") originAddressName: String,
            @Query("maxWalkDist") distance: Int,
            @Query("destCoordLat") destinationLat: String,
            @Query("destCoordLong") destinationLong: String,
            @Query("destCoordName") destinationName: String,
            @Query("numTrips") numberOfTrips: Int,
            @Query("format") json: String

    ): Observable<VtTripResponseModel>*/
}