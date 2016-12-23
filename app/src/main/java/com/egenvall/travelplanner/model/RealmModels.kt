package com.egenvall.travelplanner.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SearchHistory(@PrimaryKey open var key: String = "key", open var list : RealmList<SearchPair> = RealmList()) : RealmObject()
open class RealmStopLocation(@PrimaryKey open var stopid : String = "stopidY", open var type : String = "type",
                             open var lat : Double = 0.0,
                             open var lon : Double = 0.0, open var idx : String = "idx",
                             open var name : String = "Stopname") : RealmObject()
open class SearchPair(@PrimaryKey open var orgPlusDestId : String = "orgid/destid", open var origin : RealmStopLocation = RealmStopLocation(), open var destination : RealmStopLocation = RealmStopLocation()) : RealmObject()