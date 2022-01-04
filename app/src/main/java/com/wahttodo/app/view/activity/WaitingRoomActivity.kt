package com.wahttodo.app.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.wahttodo.app.R
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

class WaitingRoomActivity : AppCompatActivity() {

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
        categoryListItems = intent.getParcelableExtra("DecisionFor")!!
        hostuser = intent.getStringExtra("hostUserId").toString()
        imFrom = intent.getStringExtra("imFrom").toString()

        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        userName = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)?.get(0)?.name.toString()

        SharePreferenceManager.getInstance(this).save(Constants.ROOM_ID, roomId)

        db = FirebaseFirestore.getInstance()

        if (hostuser == userId && imFrom == "DecisionCategory") {
            createRoomAndAddUser()
            ShareRoomLink(this, roomId, userId)
        }
        else {
            checkIfRoomIsActive()
        }
    }

    private fun checkIfRoomIsActive() {
        db.collection("whatToDoCollection")
            .document(roomId)
            .get()
            .addOnSuccessListener {
                var currentDate = Timestamp.now().toDate().time
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

    fun displayDecisionShortListed() {
        replaceFragment(
            DecisionShortListedFragment(),
            false,
            R.id.nav_host_fragment,
            "DecisionShortListedFragment"
        )
    }

    fun displayDecisionCardListing() {
        replaceFragment(
            DecisionListingFragment(),
            false,
            R.id.nav_host_fragment,
            "DecisionListingFragment"
        )
    }

    fun displayDecisionSubCategory(count: Int) {
        var bundle = Bundle()
        bundle.putInt("user_count", count)
        replaceFragmentWithData(
            DecisionSubCategoryFragment(),
            false,
            R.id.nav_host_fragment,
            "DecisionSubCategoryFragment",
            bundle
        )
    }

    fun displayWaitingRoomJoinedUserList() {
        replaceFragment(
            WaitingRoomUserListFragment(),
            false,
            R.id.nav_host_fragment,
            "WaitingRoomUserListFragment"
        )
    }
}
