package com.wahttodo.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.wahttodo.app.R
import com.wahttodo.app.model.CategoryListItems
import com.wahttodo.app.model.ShortListedItems
import com.wahttodo.app.view.activity.DecisionActivity

class DecisionShortListedAdapter (
    activityContext: Context
) :
    RecyclerView.Adapter<DecisionShortListedAdapter.MyViewHolder>() {

    private val listItems = ArrayList<ShortListedItems>()
    var context: Context = activityContext

    fun updateListItems(categoryModel: ArrayList<ShortListedItems>) {
        listItems.clear()
        listItems.addAll(categoryModel)
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

        var item: ShortListedItems = listItems.get(position)

        holder.txtName.text = item.name

        holder.itemView.setOnClickListener {
//            var bundle = Bundle()
//            bundle.putParcelable("OrderDetails", item)
//            (context as DecisionActivity?)!!.openMovieSection(bundle)
        }

    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txtName: TextView = view.findViewById(R.id.txtName)
        val cardView: View = itemView


    }

}