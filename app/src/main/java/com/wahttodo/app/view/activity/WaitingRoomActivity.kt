package com.wahttodo.app.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.wahttodo.app.R
import com.wahttodo.app.callbacks.ApiResponse
import com.wahttodo.app.model.*
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.*
import com.wahttodo.app.utils.replaceFragment
import com.wahttodo.app.view.fragment.DecisionListingFragment
import com.wahttodo.app.view.fragment.DecisionShortListedFragment
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

import com.wahttodo.app.view.fragment.DecisionSubCategoryFragment
import com.wahttodo.app.view.fragment.WaitingRoomUserListFragment
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.*

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
    var dumpMoviesList = ArrayList<DumpedMoviesList>()
    var matchedMoviesList = ArrayList<MatchedMoviesList>()
    var joinedUserList = ArrayList<JoinedUserList>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)
        //categoryListItems = intent.getParcelableExtra("DecisionFor")!!
        hostuser = intent.getStringExtra("hostUserId").toString()
        imFrom = intent.getStringExtra("imFrom").toString()

        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        userName = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)?.get(0)?.name.toString()

        setToolBarTitle("Waiting Room")
        db = FirebaseFirestore.getInstance()

        if (hostuser == userId && imFrom == "DecisionCategory") {
            val format = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss")
            val date = format.format(Date())
            roomId = userId + "a" + date
            createRoomAndAddUser()
            ShareRoomLink(this, roomId, userId)
        }
        else {
            roomId = intent.getStringExtra("roomId").toString()
            checkIfRoomIsActive()
        }

        SharePreferenceManager.getInstance(this).save(Constants.ROOM_ID, roomId)
    }

    private fun checkIfRoomIsActive() {
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
                displayWaitingRoomJoinedUserList()
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
                displayWaitingRoomJoinedUserList()
            }
            .addOnFailureListener {
                showToastMsg("Record failed to add.")
            }
    }

    private fun saveJoinedRoom() {


        //call get otp api
        val method = "joinedRoom"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("userId", userId)
            jsonObject.put("roomId", roomId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    fun displayDecisionShortListed() {
        val bundle = Bundle()
        bundle.putString("imFrom", "WaitingRoomActivity")
        replaceFragmentWithData(
            DecisionShortListedFragment(),
            false,
            R.id.nav_host_fragment,
            "DecisionShortListedFragment",
            bundle
        )
    }

    fun displayDecisionCardListing() {
        val bundle = Bundle()
        bundle.putString("imFrom", "WaitingRoomActivity")
        replaceFragmentWithData(
            DecisionListingFragment(),
            false,
            R.id.nav_host_fragment,
            "DecisionListingFragment",
            bundle
        )
    }

    fun displayDecisionSubCategory() {
        val bundle = Bundle()
        bundle.putString("imFrom", "WaitingRoomActivity")
        replaceFragmentWithData(
            DecisionSubCategoryFragment(),
            false,
            R.id.nav_host_fragment,
            "DecisionSubCategoryFragment",
            bundle
        )
    }

    fun displayWaitingRoomJoinedUserList() {
        saveJoinedRoom()
        replaceFragment(
            WaitingRoomUserListFragment(),
            false,
            R.id.nav_host_fragment,
            "WaitingRoomUserListFragment"
        )
    }

//    override fun onBackPressed() {
//
//        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//
//        if (fragment != null && ((fragment is DecisionSubCategoryFragment) )) {
//            displayWaitingRoomJoinedUserList()
//        }
//        else if (fragment != null && ((fragment is DecisionListingFragment) )) {
//            displayDecisionSubCategory()
//        }
//        else if (fragment != null && ((fragment is DecisionShortListedFragment) )) {
//            displayDecisionCardListing()
//        }else if (fragment != null && ((fragment is WaitingRoomUserListFragment) )) {
//            openClearActivity(DecisionActivity::class.java)
//        }
//    }

    fun setToolBarTitle(title: String) {
        tvToolbarTitle.text = title
    }

    override fun onSuccess(data: Any, tag: String) {

    }

    override fun onFailure(message: String) {

    }


}
