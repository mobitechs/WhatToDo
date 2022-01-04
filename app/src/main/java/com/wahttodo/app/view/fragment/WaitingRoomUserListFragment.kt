package com.wahttodo.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.wahttodo.app.R
import com.wahttodo.app.adapter.WaitingRoomUserListAdapter
import com.wahttodo.app.model.JoinedUserList
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.setupCommonRecyclerViewsProperty
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.view.activity.WaitingRoomActivity
import com.wahttodo.app.view.activity.WaitingRoomActivity.Companion.db
import com.wahttodo.app.viewModel.UserListViewModel
import kotlinx.android.synthetic.main.fragment_waiting_room_user_list.view.*
import kotlinx.android.synthetic.main.progressbar.view.*
import java.util.HashMap

class WaitingRoomUserListFragment : Fragment() {
    private lateinit var roomId: String
    private lateinit var waitingRoomFirebaseListener: ListenerRegistration
    lateinit var rootView: View
    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: WaitingRoomUserListAdapter
    var listItems = ArrayList<JoinedUserList>()
    lateinit var mLayoutManager: LinearLayoutManager
    var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_waiting_room_user_list, container, false)
        initView()
        return rootView
    }

    private fun initView() {

        userId = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        roomId = SharePreferenceManager.getInstance(requireContext()).getValueString(Constants.ROOM_ID).toString()

        setupRecyclerView()

        rootView.btnStart.setOnClickListener{
            (context as WaitingRoomActivity).displayDecisionSubCategory(listItems.size)
            waitingRoomFirebaseListener.remove()
        }

    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        rootView.progressBar.visibility = View.VISIBLE

        requireContext().setupCommonRecyclerViewsProperty(recyclerView, Constants.VERTICAL)
        listAdapter = WaitingRoomUserListAdapter(requireContext())
        recyclerView.adapter = listAdapter

        getListOfUsers()
    }

    private fun getListOfUsers() {
//        listItems.add(JoinedUserList("1", "Pratik"))
//        listItems.add(JoinedUserList("2", "Sanket"))
//        listItems.add(JoinedUserList("3", "Amit"))
//        listItems.add(JoinedUserList("4", "Kush"))
//        listAdapter.updateListItems(listItems)

        waitingRoomFirebaseListener = db.collection("room")
            .document(roomId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    requireActivity().showToastMsg("Listen failed. $error")
                    rootView.progressBar.visibility = View.GONE
                }

                if (snapshot != null && snapshot.exists()) {
                    rootView.progressBar.visibility = View.GONE
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
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(this::waitingRoomFirebaseListener.isInitialized){
            waitingRoomFirebaseListener.remove()
        }
    }
}