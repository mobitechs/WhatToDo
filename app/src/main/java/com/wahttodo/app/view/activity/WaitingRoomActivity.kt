package com.wahttodo.app.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.wahttodo.app.R
import com.wahttodo.app.adapter.WaitingRoomUserListAdapter
import com.wahttodo.app.callbacks.ApiResponse
import com.wahttodo.app.model.*
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.*
import com.wahttodo.app.viewModel.UserListViewModel
import kotlinx.android.synthetic.main.activity_waiting_room.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recyclerview.*
import kotlinx.android.synthetic.main.toolbar.*

import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WaitingRoomActivity : AppCompatActivity(), ApiResponse {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var db: FirebaseFirestore
    }

    lateinit var categoryListItems: CategoryListItems
    var roomId = "1234" //userId+currentTimestamp
    var userName = ""
    var userId = ""
    var hostuser = ""
    var imFrom = ""
    var roomName = ""
    var hostUserId = ""
    var dumpMoviesList = ArrayList<DumpedMoviesList>()
    var matchedMoviesList = ArrayList<MatchedMoviesList>()
    var joinedUserList = ArrayList<JoinedUserList>()


    private lateinit var waitingRoomFirebaseListener: ListenerRegistration
    lateinit var rootView: View
    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: WaitingRoomUserListAdapter
    var listItems = ArrayList<JoinedUserList>()
    lateinit var mLayoutManager: LinearLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)
        //categoryListItems = intent.getParcelableExtra("DecisionFor")!!
        hostuser = intent.getStringExtra("hostUserId").toString()
        imFrom = intent.getStringExtra("imFrom").toString()

        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
            ?.get(0)?.userId.toString()
        userName = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
            ?.get(0)?.name.toString()


        db = FirebaseFirestore.getInstance()

        if (hostuser == userId && imFrom == "DecisionCategory") {
            val format = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss")
            val date = format.format(Date())
            roomId = userId + "a" + date
            createRoomAndAddUser()
            ShareRoomLink(this, roomId, userId)
        } else {
            roomId = intent.getStringExtra("roomId").toString()
            checkIfRoomIsActive()
        }

        SharePreferenceManager.getInstance(this).save(Constants.ROOM_ID, roomId)
        initView()
    }

    private fun initView() {

        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
            ?.get(0)?.userId.toString()
        roomId =
            SharePreferenceManager.getInstance(this).getValueString(Constants.ROOM_ID).toString()

        setupRecyclerView()

        btnStart.setOnClickListener {
//            (context as WaitingRoomActivity).displayDecisionSubCategory()
            openActivity(SubCategoryActivity::class.java)
            waitingRoomFirebaseListener.remove()
        }

    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
//        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)!!
        progressBar.visibility = View.VISIBLE

        setupCommonRecyclerViewsProperty(recyclerView, Constants.VERTICAL)
        listAdapter = WaitingRoomUserListAdapter(this)
        recyclerView.adapter = listAdapter

        getListOfUsers()
    }

    private fun getListOfUsers() {

        waitingRoomFirebaseListener = db.collection("whatToDoCollection")
            .document(roomId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    showToastMsg("Listen failed. $error")
                    progressBar.visibility = View.GONE
                }

                if (snapshot != null && snapshot.exists()) {
                    progressBar.visibility = View.GONE
                    val data = snapshot.data
                    val joinedUserList = data?.getValue("joinedUserList") as ArrayList<*>

                    listItems.clear()
                    for (u in joinedUserList) {
                        val user = u as HashMap<*, *>
                        val memberUserId = user["userId"].toString()
                        val memberUserName = user["userName"].toString()
                        listItems.add(JoinedUserList(memberUserId, memberUserName))
                        listAdapter.updateListItems(listItems)
                    }
                } else {
                    showToastMsg("not exist failed.")
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::waitingRoomFirebaseListener.isInitialized) {
            waitingRoomFirebaseListener.remove()
        }
    }

    private fun checkIfRoomIsActive() {
        db.collection("whatToDoCollection")
            .document(roomId)
            .get()
            .addOnSuccessListener {
                var currentDate = Timestamp.now().toDate().time


                if (it.data?.getValue("timeStamp") != null) {
                    val serverTimestamp = it.data?.getValue("timeStamp") as Timestamp
                    val sfd = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                    sfd.format(serverTimestamp.toDate())
                    var serverDate = serverTimestamp.toDate().time
                    if (currentDate - serverDate >= 86400000) {
                        showToastMsg("Room is not active")
                    }
                    else {
                        addUser()
                    }
                }


            }
            .addOnFailureListener {
                showToastMsg("Record failed to fetch")
            }
    }

    private fun addUser() {
        val joinedUserList = JoinedUserList(userId, userName)
        db.collection("whatToDoCollection")
            .document(roomId)
            .update("joinedUserList", FieldValue.arrayUnion(joinedUserList))
            .addOnSuccessListener {
                Log.d("TAG", "User added")
//                displayWaitingRoomJoinedUserList()
                saveJoinedRoom()
            }
            .addOnFailureListener {
                showToastMsg("User failed to add")
            }
    }

    private fun createRoomAndAddUser() {
        joinedUserList.add(JoinedUserList(userId, userName))
        val waitingRoomDetails = WaitingRoomDetails(Timestamp.now(), matchedMoviesList, joinedUserList)

        db.collection("whatToDoCollection")
            .document(roomId)
            .set(waitingRoomDetails)
            .addOnSuccessListener {
                Log.d("TAG","Record added successfully.")
//                displayWaitingRoomJoinedUserList()
                saveJoinedRoom()
            }
            .addOnFailureListener {
                showToastMsg("Record failed to add.")
            }
    }

    private fun saveJoinedRoom() {
        hostUserId = hostuser
        val method = "joinedRoom"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("joinedUserId", userId)
            jsonObject.put("hostUserId", hostUserId)
            jsonObject.put("roomId", roomId)
            jsonObject.put("roomName", roomName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    override fun onSuccess(data: Any, tag: String) {

    }

    override fun onFailure(message: String) {

    }


}
