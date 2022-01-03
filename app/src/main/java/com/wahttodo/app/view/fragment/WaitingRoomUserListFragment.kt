package com.wahttodo.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahttodo.app.R
import com.wahttodo.app.adapter.WaitingRoomUserListAdapter
import com.wahttodo.app.model.JoinedUserList
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.setupCommonRecyclerViewsProperty
import com.wahttodo.app.view.activity.WaitingRoomActivity
import com.wahttodo.app.viewModel.UserListViewModel
import kotlinx.android.synthetic.main.fragment_waiting_room_user_list.view.*

class WaitingRoomUserListFragment : Fragment() {
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

        userId =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userId.toString()

        setupRecyclerView()

        rootView.btnStart.setOnClickListener{
            (context as WaitingRoomActivity).displayDecisionSubCategory()
        }

    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

        requireContext().setupCommonRecyclerViewsProperty(recyclerView, Constants.VERTICAL)
        listAdapter = WaitingRoomUserListAdapter(requireContext())
        recyclerView.adapter = listAdapter

        getListOfCategory()

    }

    private fun getListOfCategory() {

        listItems.add(JoinedUserList("1", "Pratik"))
        listItems.add(JoinedUserList("2", "Sanket"))
        listItems.add(JoinedUserList("3", "Amit"))
        listItems.add(JoinedUserList("4", "Kush"))
        listAdapter.updateListItems(listItems)
    }

}