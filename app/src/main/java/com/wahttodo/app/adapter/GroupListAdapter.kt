package com.wahttodo.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.wahttodo.app.R
import com.wahttodo.app.callbacks.GroupListCallback
import com.wahttodo.app.model.JoinedRoomListItems
import com.wahttodo.app.utils.parseDateToddMMyyyy
import java.text.SimpleDateFormat

class GroupListAdapter(
    activityContext: Context,
    var groupListCallback: GroupListCallback
) :
    RecyclerView.Adapter<GroupListAdapter.MyViewHolder>() {

    private var currentDate: Long = 0
    private val listItems = ArrayList<JoinedRoomListItems>()
    var context: Context = activityContext



    fun deleteRoomItems(position: Int) {
        listItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listItems!!.size)
        notifyDataSetChanged()
    }
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
        holder.txtCreatedAt.text = "Created At: "+room
        holder.txtName.text = item.roomName


        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.addedDate)
        var serverDate = date.time
        if (currentDate - serverDate >= 86400000) {
//            requireActivity().showToastMsg("Room is not active")
//            (context as HomeActivity).displayDecisionShortListed()
            holder.layoutRoomStatus.background.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_ATOP)
        }
        else {
//            (context as HomeActivity).displayDecisionCardListing()
            holder.layoutRoomStatus.background.setColorFilter(Color.parseColor("#4CAF50"), PorterDuff.Mode.SRC_ATOP)

            if(item.joinedUserId == item.hostUserId){
                holder.imgDelete.visibility = View.VISIBLE
            }else{
                holder.imgDelete.visibility = View.GONE
            }
        }

//        holder.cardView.setOnClickListener {
//            groupListCallback.getRoomId(item.roomId, "KeepSwipw")
//        }
        holder.cardView.setOnClickListener {
            groupListCallback.getRoomId(item.roomId,"KeepSwipe")
        }
        holder.btnMatchedShortList.setOnClickListener {
            groupListCallback.getRoomId(item.roomId,"ShorListing")
        }

        holder.imgDelete.setOnClickListener {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.addedDate)
            var serverDate = date.time
            if (currentDate - serverDate >= 86400000) {
                //not active
                groupListCallback.deleteRoom(item,position)
            }
            else {
                //active
                if(item.joinedUserId == item.hostUserId){
                    groupListCallback.deleteRoomByHost(item,position)
                }
            }

        }
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var btnMatchedShortList: LinearLayout = view.findViewById(R.id.btnMatchedShortList)
        var btnKeepSwipe: AppCompatImageView = view.findViewById(R.id.btnKeepSwipe)
        var imgDelete: LinearLayout = view.findViewById(R.id.imgDelete)
        var txtName: TextView = view.findViewById(R.id.txtName)
        var txtCreatedAt: TextView = view.findViewById(R.id.txtCreatedAt)

        var layoutRoomStatus: RelativeLayout = view.findViewById(R.id.layoutRoomStatus)
        val cardView: View = itemView

    }

}