package com.wahttodo.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.wahttodo.app.R
import com.wahttodo.app.model.CategoryListItems
import com.wahttodo.app.model.MatchedMoviesList
import com.wahttodo.app.model.ShortListedItems
import com.wahttodo.app.view.activity.DecisionActivity

class DecisionShortListedAdapter (
    activityContext: Context
) :
    RecyclerView.Adapter<DecisionShortListedAdapter.MyViewHolder>() {

    private val listItems = ArrayList<MatchedMoviesList>()
    var context: Context = activityContext

    fun updateListItems(list: ArrayList<MatchedMoviesList>) {
        listItems.clear()
        listItems.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_item_short_listed, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: MatchedMoviesList = listItems[position]

        holder.txtName.text = item.movieName
        holder.textDescription.text = item.description
        holder.rating.rating = item.rating.toFloat()/2

        holder.itemView.setOnClickListener {
//            var bundle = Bundle()
//            bundle.putParcelable("OrderDetails", item)
//            (context as DecisionActivity?)!!.openMovieSection(bundle)
        }

    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txtName: TextView = view.findViewById(R.id.textMovieName)
        var rating: RatingBar = view.findViewById(R.id.rating)
        var textDescription: TextView = view.findViewById(R.id.textDescription)
        val cardView: View = itemView
    }

}