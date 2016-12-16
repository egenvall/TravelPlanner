package com.egenvall.travelplanner.model


data class AccessToken(val scope : String ="", val expires_in : Int = -1, val token_type : String = "", val access_token : String = "")
data class VtResponseModel( val LocationList : LocationList)
data class LocationList( val errorText : String?,  val error : String?,
                          val StopLocation: List<StopLocation>? = listOf<StopLocation>(),
                         val CoordLocation: List<CoordLocation>? = listOf<CoordLocation>()

)


data class StopLocation( val id : String = "id",  val type : String = "type",
                          val lon : Double = 0.0,
                         val lat : Double = 0.0,  val idx : String = "idx",
                         val name : String = "Stopname")
data class CoordLocation( val type : String = "type",  val lon : Double = 0.0,  val lat : Double = 0.0,  val idx : String ="idx",  val name : String ="Coordname")



