package com.egenvall.travelplanner.adapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.model.Trip
import kotlinx.android.synthetic.main.card_trip_overview.view.*


class TripOverviewAdapter (var tripList: List<Trip>, val itemClick: (Trip) -> Unit):
        RecyclerView.Adapter<TripOverviewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_trip_overview, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindStopLocation(tripList[position])

    }

    override fun getItemCount() = tripList.size

    class ViewHolder(view: View, val itemClick: (Trip) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindStopLocation(item: Trip) {
            val noWalk = item.Leg.filter { it.type != "WALK" }
            with(item) {
                itemView.trip_stopname.text = noWalk.first().Origin.name.substringBefore(',')//Starthållplats
                itemView.trip_platform.text = noWalk.first().Origin.track
                itemView.trip_departure.text = noWalk.first().Origin.time
                itemView.trip_arrival.text = noWalk.last().Destination.time
                itemView.setOnClickListener { itemClick(item) }
            }

            /**
             * for (t in list){
            Log.d(TAG,"-------Trip  Byten: ${t.Leg.filter { it.type != "WALK" }.size-1}----------")
            for (l in t.Leg.filter { it.type != "WALK" }){
            Log.d(TAG, ": [${l.Origin.name} - ${l.Destination.name} : ${l.Origin.time} - ${l.Destination.time}")

            }
            Log.d(TAG,"---------B-----------")

            }
             */

        }
    }
}