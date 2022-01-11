package com.wahttodo.app.view.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.wahttodo.app.R
import com.wahttodo.app.adapter.GroupListAdapter
import com.wahttodo.app.callbacks.AlertDialogBtnClickedCallBack
import com.wahttodo.app.callbacks.ApiResponse
import com.wahttodo.app.callbacks.GroupListCallback
import com.wahttodo.app.model.JoinedRoomListItems
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.*
import com.wahttodo.app.viewModel.UserListViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.loader.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.progressbar.progressBar
import kotlinx.android.synthetic.main.recyclerview.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat

class HomeActivity : AppCompatActivity(),GroupListCallback, ApiResponse,
    AlertDialogBtnClickedCallBack {
    lateinit var listAdapter: GroupListAdapter
    var listItems = ArrayList<JoinedRoomListItems>()
    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var viewModelUser: UserListViewModel
    var userId = ""
    var position = 0
    var method = ""
    var roomId = ""
    private lateinit var rootView: View
    companion object {
        lateinit var db: FirebaseFirestore
    }
    private var doubleBackToExitPressedOnce = false

    private lateinit var waitingRoomFirebaseListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = FirebaseFirestore.getInstance()

        initView()
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

    private fun initView() {
        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()

        btnCreateGroupNShare.setOnClickListener{
            this.openActivity(DecisionActivity::class.java)
        }
    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)

        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = GroupListAdapter(this, this)
        recyclerView.adapter = listAdapter
        listAdapter.updateListItems(listItems)

        viewModelUser.showProgressBar.observe(this, Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        viewModelUser.getMyGroupList(userId)

        viewModelUser.joinedRoomListItems.observe(this, Observer {
            listItems.clear()
            listItems.addAll(it)
            listAdapter.updateListItems(listItems)
        })

    }

    override fun getRoomId(roomId: String) {
        SharePreferenceManager.getInstance(this).save(Constants.ROOM_ID, roomId)
        db.collection("whatToDoCollection")
            .document(roomId)
            .get()
            .addOnSuccessListener {
                var currentDate = Timestamp.now().toDate().time
                if(it.data?.getValue("timeStamp")!=null){
                    val serverTimestamp = it.data?.getValue("timeStamp") as Timestamp
                    val sfd = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                    sfd.format(serverTimestamp.toDate())
                    var serverDate = serverTimestamp.toDate().time
                    if (currentDate - serverDate >= 86400000) {
                        this.showToastMsg("Room is not active")
//                        (context as HomeActivity).displayDecisionShortListed()
                        openActivity(ShortListedLIstActivity::class.java)
                    }
                    else {
//                        (context as HomeActivity).displayDecisionCardListing()
                        openActivity(ListingCardActivity::class.java)
                    }
                }


            }
            .addOnFailureListener {
                this.showToastMsg("Record failed to fetch")
            }
    }

    override fun deleteRoom(item: JoinedRoomListItems, pos: Int) {
        position = pos
        method = "deleteRoom"
        roomId = item.roomId
        showAlertDialog("Confirmation","Do you really want to delete this room?","YES","NO",this)

    }

    override fun deleteRoomByHost(item: JoinedRoomListItems, pos: Int) {
        method = "deleteRoomByHost"
        position = pos
        roomId = item.roomId
        showAlertDialog("Confirmation","Do you really want to delete this room, if you delete this room no one can able see this group.","YES","NO",this)
    }



    private fun deleteRoomAPI() {
        val jsonObject = JSONObject()
        if(method == "deleteRoom"){
            try {
                jsonObject.put("method", method)
                jsonObject.put("joinedUserId", userId)
                jsonObject.put("roomId", roomId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }else{
            try {
                jsonObject.put("method", method)
                jsonObject.put("hostUserId", userId)
                jsonObject.put("roomId", roomId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }


        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    private fun deleteRoomFirebase() {
        db.collection("whatToDoCollection")
            .document(roomId)
            .delete()
            .addOnSuccessListener {
                db.collection("dumpMoviesCollection").document(roomId)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (it.result.exists()) {
                                db.collection("dumpMoviesCollection")
                                    .document(roomId)
                                    .delete()
                                    .addOnSuccessListener {
                                        showToastMsg("Room successfully deleted")
                                    }
                                    .addOnFailureListener {
                                        showToastMsg("Room failed to delete")
                                    }
                            }
                            else {
                                showToastMsg("Room successfully deleted")
                            }
                        }
                    }
            }
            .addOnFailureListener {
                showToastMsg("Room failed to delete")
            }
    }

    override fun onSuccess(data: Any, tag: String) {
        listAdapter.deleteRoomItems(position)
    }

    override fun onFailure(message: String) {

    }

    override fun positiveBtnClicked() {
        //delete from firebase
        deleteRoomAPI()
        deleteRoomFirebase()
    }

    override fun negativeBtnClicked() {

    }

}