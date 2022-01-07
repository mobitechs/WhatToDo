package com.wahttodo.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.wahttodo.app.R
import com.wahttodo.app.callbacks.GroupListCallback
import com.wahttodo.app.model.JoinedRoomListItems
import com.wahttodo.app.utils.parseDateToddMMyyyy
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.view.activity.HomeActivity
import java.text.SimpleDateFormat

class GroupListAdapter(
    activityContext: Context,
    var groupListCallback: GroupListCallback
) :
    RecyclerView.Adapter<GroupListAdapter.MyViewHolder>() {

    private var currentDate: Long = 0
    private val listItems = ArrayList<JoinedRoomListItems>()
    var context: Context = activityContext

    fun updateListItems(categoryModel: ArrayList<JoinedRoomListItems>) {
        currentDate = Timestamp.now().toDate().time
        listItems.clear()
        listItems.addAll(categoryModel)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.adapter_item_groups, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: JoinedRoomListItems = listItems.get(position)

        var roomId = item.roomId.split("a")[1]
        var room = parseDateToddMMyyyy(roomId)
        holder.txtName.text = "Room: "+room

        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.addedDate)
        var serverDate = date.time
        if (currentDate - serverDate >= 86400000) {
//            requireActivity().showToastMsg("Room is not active")
//            (context as HomeActivity).displayDecisionShortListed()
            holder.layoutRoomStatus.background.setTint(context.resources.getColor(R.color.red))
        }
        else {
//            (context as HomeActivity).displayDecisionCardListing()
            holder.layoutRoomStatus.background.setTint(context.resources.getColor(R.color.green))
        }

        holder.cardView.setOnClickListener {
            groupListCallback.getRoomId(item.roomId)
        }
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var txtName: TextView = view.findViewById(R.id.txtName)
        var layoutRoomStatus: RelativeLayout = view.findViewById(R.id.layoutRoomStatus)
        val cardView: View = itemView

    }

}