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
import com.wahttodo.app.R
import com.wahttodo.app.adapter.GroupListAdapter
import com.wahttodo.app.model.GroupListItems
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.openActivity
import com.wahttodo.app.viewModel.UserListViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recyclerview.*

class HomeActivity : AppCompatActivity() {


    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: GroupListAdapter
    var listItems = ArrayList<GroupListItems>()
    lateinit var mLayoutManager: LinearLayoutManager
    var userId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
            ?.get(0)?.userId.toString()

        btnCreateGroupNShare.setOnClickListener{
            openActivity(DecisionActivity::class.java)
        }

        setupRecyclerView()
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

        viewModelUser.groupListItems.observe(this, Observer {
            listAdapter.updateListItems(it)
        })

    }

}