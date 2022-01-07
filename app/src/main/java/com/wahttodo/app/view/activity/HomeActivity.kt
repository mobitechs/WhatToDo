package com.wahttodo.app.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.wahttodo.app.R
import com.wahttodo.app.adapter.GroupListAdapter
import com.wahttodo.app.callbacks.GroupListCallback
import com.wahttodo.app.model.JoinedRoomListItems
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.*
import com.wahttodo.app.view.fragment.*
import com.wahttodo.app.viewModel.UserListViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recyclerview.*
import kotlinx.android.synthetic.main.toolbar.*
import java.text.SimpleDateFormat

class HomeActivity : AppCompatActivity() {

    companion object {
        lateinit var db: FirebaseFirestore
    }
    private var doubleBackToExitPressedOnce = false

    private lateinit var waitingRoomFirebaseListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = FirebaseFirestore.getInstance()
        tvToolbarTitle.text = "Home"

    }

    override fun onResume() {
        super.onResume()
        displayGroupList()
    }
    fun displayGroupList() {
        replaceFragment(
            HomeGroupListFragment(),
            false,
            R.id.nav_host_fragment,
            "WaitingRoomUserListFragment"
        )
    }

    fun displayDecisionSubCategory() {
        val bundle = Bundle()
        bundle.putString("imFrom", "HomeActivity")
        replaceFragmentWithData(
            DecisionSubCategoryFragment(),
            false,
            R.id.nav_host_fragment,
            "DecisionSubCategoryFragment",
            bundle
        )
    }

    fun displayDecisionCardListing() {
        val bundle = Bundle()
        bundle.putString("imFrom", "HomeActivity")
        replaceFragmentWithData(
            DecisionListingFragment(),
            false,
            R.id.nav_host_fragment,
            "DecisionListingFragment",
            bundle
        )
    }

    fun displayDecisionShortListed() {
        val bundle = Bundle()
        bundle.putString("imFrom", "HomeActivity")
        replaceFragmentWithData(
            DecisionShortListedFragment(),
            false,
            R.id.nav_host_fragment,
            "DecisionShortListedFragment",
            bundle
        )
    }

    fun setToolBarTitle(title: String) {
        tvToolbarTitle.text = title
    }

//    override fun onBackPressed() {
//        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//
//        if (fragment != null && ((fragment is DecisionSubCategoryFragment) )) {
//            displayGroupList()
//        }
//        else if (fragment != null && ((fragment is DecisionListingFragment) )) {
//            displayGroupList()
//        }
//        else if (fragment != null && ((fragment is DecisionShortListedFragment) )) {
//            displayDecisionCardListing()
//        }
//        else{
//            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed()
//                return
//            }
//            this.doubleBackToExitPressedOnce = true
//            showToastMsg("Double tap to exit")
//            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
//        }
//    }
}