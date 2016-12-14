package com.egenvall.travelplanner.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.model.StopLocation
import kotlinx.android.synthetic.main.card_stoplocation.view.*


class SearchAdapter(var locationList: List<StopLocation>, val itemClick: (StopLocation) -> Unit):
        RecyclerView.Adapter<SearchAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_stoplocation, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindStopLocation(locationList[position])

    }

    override fun getItemCount() = locationList.size

    class ViewHolder(view: View, val itemClick: (StopLocation) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindStopLocation(item : StopLocation) {
            with(item) {
                Log.d("ADAPTER", "Doing : $item")
                itemView.stoplocation_name.text = item.name
                itemView.setOnClickListener { itemClick(item) }
            }
        }
    }
}