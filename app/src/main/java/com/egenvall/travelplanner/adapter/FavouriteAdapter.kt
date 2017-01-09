package com.egenvall.travelplanner.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.egenvall.travelplanner.R
import com.egenvall.travelplanner.model.Favourite
import com.egenvall.travelplanner.model.SearchPair
import kotlinx.android.synthetic.main.favourite_item.view.*

class FavouriteAdapter(var favList: List<Favourite>, val itemClick: (Favourite) -> Unit):
        RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favourite_item, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindStopLocation(favList[position])

    }

    override fun getItemCount() = favList.size

    class ViewHolder(view: View, val itemClick: (Favourite) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindStopLocation(item : Favourite) {
            with(item) {
                itemView.fav_bg.setBackgroundColor(Color.parseColor(hexColor))
                itemView.fav_nick.text = nickName
                itemView.setOnClickListener { itemClick(item) }
            }
        }
    }
}