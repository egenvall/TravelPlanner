package com.egenvall.travelplanner.model


data class AccessToken(val scope : String ="", val expires_in : Int = -1, val token_type : String = "", val access_token : String = "")
data class VtResponseModel( val LocationList : LocationList)
data class LocationList( val errorText : String?,  val error : String?,
                          val StopLocation: List<StopLocation>? = listOf<StopLocation>(),
                         val CoordLocation: List<CoordLocation>? = listOf<CoordLocation>()

)


data class StopLocation( val id : String = "id",  val type : String = "STOP",
                          val lat : Double = 0.0,
                         val lon : Double = 0.0,  val idx : String = "idx",
                         val name : String = "Stopname")
data class CoordLocation( val type : String = "ADR",  val lon : Double = 0.0,  val lat : Double = 0.0,  val idx : String ="idx",  val name : String ="Coordname")

data class TripResponseModel(val TripList : TripList)
data class TripList(val error : String?, val errorText: String?, val Trip : List<Trip>)
data class Trip(val Leg : List<Leg>, val type : String)
data class Leg (val Origin : TripEndpoint, val Destination : TripEndpoint, val fgColor : String, val direction : String, val name : String, val sname : String, val type : String)
data class TripEndpoint(val id : String?, val routeIdx : String?, val track : String?,
                        val name : String, val time : String, val type : String,
                        val directtime : String?, val rtTime  : String?)

