package com.egenvall.travelplanner.util

import java.lang.reflect.Type
import com.squareup.moshi.*
import java.io.IOException
import com.squareup.moshi.JsonAdapter

import java.util.*

/**
 * Custom JsonAdapter as suggested by JakeWharton to my question in the Moshi Slack Channel.
 * Special thanks to him.
 *
 * The adapter Wraps an encountered Objects in a List whenever a List<T> was expected but an
 * Object was encountered in the JSON
 */

class ObjectAsListJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<Any>? {
        if (!List::class.java.isAssignableFrom(Types.getRawType(type))) {
            return null
        }
        val listDelegate = moshi.nextAdapter<List<Any>>(this, type, annotations)
        val innerType = Types.collectionElementType(type, List::class.java)
        val objectDelegate = moshi.adapter<Any>(innerType, annotations)
        return ListJsonAdapter(listDelegate, objectDelegate) as JsonAdapter<Any>
        }

     class ListJsonAdapter<T> (val listDelegate: JsonAdapter<List<T>>, val objectDelegate: JsonAdapter<T>) : JsonAdapter<List<T>>() {
        @Throws(IOException::class)
        override fun fromJson(jsonReader: JsonReader): List<T> {
            if (jsonReader.peek() === JsonReader.Token.BEGIN_OBJECT) {
                return Collections.singletonList(objectDelegate.fromJson(jsonReader))
            } else {
                return listDelegate.fromJson(jsonReader)
            }
        }

        @Throws(IOException::class)
        override fun toJson(jsonWriter: JsonWriter, list: List<T>) {
            listDelegate.toJson(jsonWriter, list)
        }
    }
}