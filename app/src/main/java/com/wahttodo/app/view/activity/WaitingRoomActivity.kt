package com.wahttodo.app.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wahttodo.app.R
import com.wahttodo.app.model.CategoryListItems
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.ShareRoomLink

class WaitingRoomActivity : AppCompatActivity() {
    lateinit var categoryListItems: CategoryListItems
    var roomId = "1234"
    var userId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_room)
        categoryListItems = intent.getParcelableExtra("DecisionFor")!!

        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
            ?.get(0)?.userId.toString()

        ShareRoomLink(this, roomId, userId)
    }
}
