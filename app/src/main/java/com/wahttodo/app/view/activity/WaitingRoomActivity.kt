package com.wahttodo.app.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.wahttodo.app.R
import com.wahttodo.app.model.CategoryListItems
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.ShareRoomLink

class WaitingRoomActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    lateinit var categoryListItems: CategoryListItems
    var roomId = "1234" //userId+currentTimestamp
    var userId = ""
    var hostuser = ""
    var imFrom = "HomeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)
        categoryListItems = intent.getParcelableExtra("DecisionFor")!!

        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
            ?.get(0)?.userId.toString()

        ShareRoomLink(this, roomId, userId)

        db = FirebaseFirestore.getInstance()

        if (hostuser == userId && imFrom == "HomeActivity") {
            createRoomAndAddUser()
        }
        else {
            addUser()
        }
    }

    private fun addUser() {

    }

    private fun createRoomAndAddUser() {

    }
}
