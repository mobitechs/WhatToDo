package com.wahttodo.app.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.wahttodo.app.R
import com.wahttodo.app.adapter.GroupListAdapter
import com.wahttodo.app.callbacks.GroupListCallback
import com.wahttodo.app.model.JoinedRoomListItems
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.openActivity
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.view.activity.DecisionActivity
import com.wahttodo.app.view.activity.HomeActivity
import com.wahttodo.app.view.activity.HomeActivity.Companion.db
import com.wahttodo.app.view.activity.WaitingRoomActivity
import com.wahttodo.app.viewModel.UserListViewModel
import kotlinx.android.synthetic.main.fragment_home_group_list.view.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.progressbar.view.*
import kotlinx.android.synthetic.main.recyclerview.*
import kotlinx.android.synthetic.main.recyclerview.view.*
import java.text.SimpleDateFormat

class HomeGroupListFragment : Fragment(), GroupListCallback {

    lateinit var listAdapter: GroupListAdapter
    var listItems = ArrayList<JoinedRoomListItems>()
    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var viewModelUser: UserListViewModel
    var userId = ""
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_home_group_list, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        userId = SharePreferenceManager.getInstance(requireActivity()).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()

        rootView.btnCreateGroupNShare.setOnClickListener{

            requireActivity().openActivity(DecisionActivity::class.java)
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(requireActivity()).get(UserListViewModel::class.java)
//        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
//        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

        mLayoutManager = LinearLayoutManager(requireActivity())
        rootView.recyclerView.layoutManager = mLayoutManager
        rootView.recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = GroupListAdapter(requireActivity(), this)
        rootView.recyclerView.adapter = listAdapter
        listAdapter.updateListItems(listItems)

        viewModelUser.showProgressBar.observe(requireActivity(), Observer {
            if (it) {
                rootView.progressBar.visibility = View.VISIBLE
            } else {
                rootView.progressBar.visibility = View.GONE
            }
        })

        viewModelUser.getMyGroupList(userId)

        viewModelUser.joinedRoomListItems.observe(requireActivity(), Observer {
            listAdapter.updateListItems(it)
        })

    }

    override fun getRoomId(roomId: String) {
        SharePreferenceManager.getInstance(requireActivity()).save(Constants.ROOM_ID, roomId)
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
                        requireActivity().showToastMsg("Room is not active")
                        (context as HomeActivity).displayDecisionShortListed()
                    }
                    else {
                        (context as HomeActivity).displayDecisionCardListing()
                    }
                }


            }
            .addOnFailureListener {
                requireActivity().showToastMsg("Record failed to fetch")
            }
    }
}