package com.wahttodo.app.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.wahttodo.app.R
import com.wahttodo.app.adapter.GroupListAdapter
import com.wahttodo.app.model.JoinedRoomListItems
import com.wahttodo.app.model.JoinedUserList
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.openActivity
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.viewModel.UserListViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.progressbar.view.*
import kotlinx.android.synthetic.main.recyclerview.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.HashMap

class HomeActivity : AppCompatActivity() {


    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: GroupListAdapter
    var listItems = ArrayList<JoinedRoomListItems>()
    lateinit var mLayoutManager: LinearLayoutManager
    var userId = ""

    private lateinit var waitingRoomFirebaseListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
            ?.get(0)?.userId.toString()

        tvToolbarTitle.text = "Home"

        btnCreateGroupNShare.setOnClickListener{

            openActivity(DecisionActivity::class.java)
        }

        setupRecyclerView()
       // getMyGroupsList()
    }



    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
//        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
//        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = GroupListAdapter(this)
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
            listAdapter.updateListItems(it)
        })

    }



}