package com.wahttodo.app.view.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.wahttodo.app.R
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.openClearActivity
import com.wahttodo.app.utils.showToastMsg

class SplashActivity : AppCompatActivity() {

    var roomId = ""
    var hostUserId = ""

    companion object {
        private const val SPLASH_TIME_OUT = 2000
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler().postDelayed({ getReferralLinkDetails() }, SPLASH_TIME_OUT.toLong())
    }


    private fun getReferralLinkDetails() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(
                this
            ) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
//                    showToastMsg(deepLink.toString())
                    var referredLink = deepLink.toString()
                    Log.e("Received Referral", "ptkk Link " + deepLink)
                    try {
//                        roomId = referredLink.substring(referredLink.lastIndexOf("=") + 1)
//                        showToastMsg(roomId)
//
                        var roomIdAndUserId = referredLink.substring(referredLink.lastIndexOf("=") + 1)
                        roomId = roomIdAndUserId.split("_a_")[0]
                        hostUserId = roomIdAndUserId.split("_a_")[1]
                    } catch (e: Exception) {
                        showToastMsg("errrr: "+e.message.toString())
                    }
                    redirectToWaitingRoom()
                }
                else{
                    checkLogin()
                }
            }
            .addOnFailureListener(
                this
            ) { e ->
                Log.w("Referred Link ", "getDynamicLink:onFailure", e)
            }
    }

    fun checkLogin() {
        if (SharePreferenceManager.getInstance(this).getValueBoolean(Constants.ISLOGIN)) {
            openClearActivity(HomeActivity::class.java)
        }
        else {
            openClearActivity(AuthActivity::class.java)
        }
    }

    private fun redirectToWaitingRoom() {
        if (SharePreferenceManager.getInstance(this).getValueBoolean(Constants.ISLOGIN)) {
            openClearActivity(WaitingRoomActivity::class.java) {
                putString("roomId", roomId)
                putString("hostUserId", hostUserId)
                putString("imFrom", "GroupMeditationOldUser")
            }
        }
        else {
            openClearActivity(AuthActivity::class.java) {
                putString("roomId", roomId)
                putString("hostUserId", hostUserId)
                putString("imFrom", "GroupMeditationNewUser")
            }
        }
    }
}