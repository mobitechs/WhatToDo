package com.wahttodo.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.wahttodo.app.R
import com.wahttodo.app.model.DumpedMoviesList

class DecisionListCardStackAdapter (
    activityContext: Context
) :
    RecyclerView.Adapter<DecisionListCardStackAdapter.MyViewHolder>() {

    private val listItems = ArrayList<DumpedMoviesList>()
    var context: Context = activityContext

    fun updateListItems(list: ArrayList<DumpedMoviesList>) {
        listItems.clear()
        listItems.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_item_card_stack, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: DumpedMoviesList = listItems[position]
        Glide.with(holder.imgMoviePoster)
            .load(item.movieImage)
            .into(holder.imgMoviePoster)
        holder.textMovieName.text = item.movieName
        holder.rating.rating = item.rating.toFloat()
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgMoviePoster: RoundedImageView = view.findViewById(R.id.imgMoviePoster)
        var textMovieName: TextView = view.findViewById(R.id.textMovieName)
        var rating: RatingBar = view.findViewById(R.id.rating)
    }
}