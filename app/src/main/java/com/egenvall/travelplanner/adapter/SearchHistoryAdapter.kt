package com.egenvall.travelplanner.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.model.SearchPair
import kotlinx.android.synthetic.main.card_search_history.view.*



class SearchHistoryAdapter(var historyList: List<SearchPair>, val itemClick: (SearchPair) -> Unit):
        RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_search_history, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindStopLocation(historyList[position])

    }

    override fun getItemCount() = historyList.size

    class ViewHolder(view: View, val itemClick: (SearchPair) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindStopLocation(item : SearchPair) {
            with(item) {
                Log.d("ADAPTER", "Doing : $item")
                itemView.history_origin.text = shortenDisplayName(item.origin.name)
                itemView.history_dest.text = shortenDisplayName(item.destination.name)
                itemView.setOnClickListener { itemClick(item) }
            }
        }

        fun shortenDisplayName(name : String): String{
            return name.substringBefore(",")
        }
    }
}