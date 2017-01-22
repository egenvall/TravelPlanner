package com.egenvall.travelplanner.persistance

import com.egenvall.travelplanner.model.*
import io.realm.Realm
import javax.inject.Inject


class RealmInteractorImpl @Inject constructor(val realm : Realm) : IRealmInteractor {
//===================================================================================
// Favourites
//===================================================================================
    override fun addFavourite(origin: StopLocation, dest: StopLocation, nick: String, bg: String) {
    val favList = realm.where(Favourites::class.java).findFirst()
            if (favList == null) {
                realm.beginTransaction()
                realm.createObject(Favourites::class.java, "Favourites")
                realm.commitTransaction()
                addFavouriteToRealm(origin,dest,nick,bg)
            } else {
                addFavouriteToRealm(origin,dest,nick,bg)
            }
    }

    private fun addFavouriteToRealm(origin : StopLocation, dest : StopLocation, nick: String, bg : String){
        val favourites = realm.where(Favourites::class.java).findFirst()
        val copy = realm.copyFromRealm(favourites.list)

        copy.add(0, Favourite(nick,bg,SearchPair(constructPkSearchPair(origin,dest),
                mapToRealmStopLocation(origin),
                mapToRealmStopLocation(dest)))
        )

        realm.executeTransaction {
            with(favourites.list) {
                deleteAllFromRealm()
                realm.copyToRealmOrUpdate(copy)
                addAll(copy.distinctBy { it.pair.orgPlusDestId })
            }
        }
    }

    override fun getFavourites(): List<Favourite> {
        var returnList = listOf<Favourite>()
        realm.executeTransaction {
            val res = realm.where(Favourites::class.java).findFirst()
            if (res != null) {
                returnList = realm.copyFromRealm(res.list.distinct())
            }
        }
        return returnList
    }



//===================================================================================
// Search History
//===================================================================================
    override fun addSearchHistory(origin: StopLocation, dest: StopLocation) {
        val history = realm.where(SearchHistory::class.java).findFirst()
            if (history == null) {
                realm.beginTransaction()
                realm.createObject(SearchHistory::class.java, "History")
                realm.commitTransaction()
                addPairToRealm(origin, dest)
            } else {
                addPairToRealm(origin, dest)
            }
    }

    private fun addPairToRealm(origin: StopLocation, dest: StopLocation) {
        val history = realm.where(SearchHistory::class.java).findFirst()
        val copy = realm.copyFromRealm(history.list)

        copy.add(0, SearchPair(constructPkSearchPair(origin, dest),
                mapToRealmStopLocation(origin),
                mapToRealmStopLocation(dest))
        )

        realm.executeTransaction {
            with(history.list) {
                deleteAllFromRealm()
                realm.copyToRealmOrUpdate(copy.take(6))
                addAll(copy.distinctBy { it.orgPlusDestId })
            }
        }
    }

    override fun getSearchHistory(): List<SearchPair> {
        //val res = realm.where(SearchHistory::class.java).findFirst().list.deleteAllFromRealm()
        val res = realm.where(SearchHistory::class.java).findFirst()
            if (res == null){
                realm.beginTransaction()
                realm.createObject(SearchHistory::class.java, "History")
                realm.commitTransaction()
            }
        val result = realm.where(SearchHistory::class.java).findFirst().list.distinct()
        return result
    }

    override fun removeFromSearchHistory(pair: SearchPair) {
        realm.executeTransaction {
            val result = realm.where(SearchPair::class.java)
                    .equalTo("orgPlusDestId", pair.orgPlusDestId).findFirst()
            result.deleteFromRealm()
        }
    }
//===================================================================================
// Helper Methods
//===================================================================================
    private fun constructPkSearchPair(origin: StopLocation, dest: StopLocation): String {
        var originIdentifier: String = ""
        var destIdentifier: String = ""
        when (origin.type) {
            "STOP" -> originIdentifier = origin.id
            "ADR" -> originIdentifier = origin.name
            "POI" -> originIdentifier = origin.name
        }
        when (dest.type) {
            "STOP" -> destIdentifier = dest.id
            "ADR" -> destIdentifier = dest.name
            "POI" -> destIdentifier = dest.name
        }
        return originIdentifier + "/" + destIdentifier
    }

    private fun mapToRealmStopLocation(stop: StopLocation): RealmStopLocation {
        with(stop) {
            return RealmStopLocation(id, type, lat, lon, idx, name)
        }
    }
}