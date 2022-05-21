package com.wahttodo.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wahttodo.app.R
import com.wahttodo.app.model.CategoryListItems
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.openActivity
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.view.activity.DecisionActivity
import com.wahttodo.app.view.activity.WaitingRoomActivity

class DecisionCategoryListAdapter(activityContext: Context) :
    RecyclerView.Adapter<DecisionCategoryListAdapter.MyViewHolder>() {

    private val listItems = ArrayList<CategoryListItems>()
    var context: Context = activityContext

    fun updateListItems(categoryModel: ArrayList<CategoryListItems>) {
        listItems.clear()
        listItems.addAll(categoryModel)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_item_category, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: CategoryListItems = listItems.get(position)

        holder.txtName.text = item.name
        holder.img.setImageResource(item.img)

        holder.itemView.setOnClickListener {

            if(item.id=="1"){
                var userId = SharePreferenceManager.getInstance(context).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
                context.openActivity(WaitingRoomActivity::class.java){
                    putParcelable("DecisionFor", item)
                    putString("imFrom", "DecisionCategory")
                    putString("hostUserId", userId)
                }
            }else{
                context.showToastMsg("Work in progress")
            }

        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txtName: TextView = view.findViewById(R.id.txtName)
        val cardView: View = itemView
        val img: ImageView = view.findViewById(R.id.img)

    }

}