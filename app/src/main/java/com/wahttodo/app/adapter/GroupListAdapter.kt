package com.wahttodo.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wahttodo.app.R
import com.wahttodo.app.callbacks.GroupListCallback
import com.wahttodo.app.model.JoinedRoomListItems

class GroupListAdapter(
    activityContext: Context,
    var groupListCallback: GroupListCallback
) :
    RecyclerView.Adapter<GroupListAdapter.MyViewHolder>() {

    private val listItems = ArrayList<JoinedRoomListItems>()
    var context: Context = activityContext

    fun updateListItems(categoryModel: ArrayList<JoinedRoomListItems>) {
        listItems.clear()
        listItems.addAll(categoryModel)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_item_groups, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: JoinedRoomListItems = listItems.get(position)

        holder.txtName.text = "Room: "+item.roomId

        holder.cardView.setOnClickListener {
            groupListCallback.getRoomId(item.roomId)
        }
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var txtName: TextView = view.findViewById(R.id.txtName)
        val cardView: View = itemView

    }

}