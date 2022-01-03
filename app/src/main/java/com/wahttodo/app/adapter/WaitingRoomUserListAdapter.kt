package com.wahttodo.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wahttodo.app.R
import com.wahttodo.app.model.JoinedUserList

import com.wahttodo.app.utils.openActivity
import com.wahttodo.app.view.activity.WaitingRoomActivity

class WaitingRoomUserListAdapter (
    activityContext: Context
) :
    RecyclerView.Adapter<WaitingRoomUserListAdapter.MyViewHolder>() {

    private val listItems = ArrayList<JoinedUserList>()
    var context: Context = activityContext

    fun updateListItems(categoryModel: ArrayList<JoinedUserList>) {
        listItems.clear()
        listItems.addAll(categoryModel)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_item_joined_user_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: JoinedUserList = listItems.get(position)

        holder.txtName.text = item.userName

    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txtName: TextView = view.findViewById(R.id.txtName)
        val cardView: View = itemView

    }

}