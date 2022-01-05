package com.wahttodo.app.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.wahttodo.app.R
import com.wahttodo.app.adapter.DecisionShortListedAdapter
import com.wahttodo.app.adapter.WaitingRoomUserListAdapter
import com.wahttodo.app.model.JoinedUserList
import com.wahttodo.app.model.MatchedMoviesList
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


class DecisionShortListedFragment : Fragment() {

    private lateinit var roomId: String
    private lateinit var shortListedFirebaseListener: ListenerRegistration
    lateinit var rootView: View
    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: DecisionShortListedAdapter
    var listItems = ArrayList<MatchedMoviesList>()
    lateinit var mLayoutManager: LinearLayoutManager
    var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_decision_short_listed, container, false)
        initView()
        return rootView
    }

    private fun initView() {

        userId = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        roomId = SharePreferenceManager.getInstance(requireContext()).getValueString(Constants.ROOM_ID).toString()
        (context as WaitingRoomActivity).setToolBarTitle("Short Listed List")
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        rootView.progressBar.visibility = View.VISIBLE

        requireContext().setupCommonRecyclerViewsProperty(recyclerView, Constants.VERTICAL)
        listAdapter = DecisionShortListedAdapter(requireContext())
        recyclerView.adapter = listAdapter

        getListOfShortListed()
    }

    private fun getListOfShortListed() {
        shortListedFirebaseListener = db.collection("whatToDoCollection")
            .document(roomId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    requireActivity().showToastMsg("Listen failed. $error")
                    rootView.progressBar.visibility = View.GONE
                }

                if (snapshot != null && snapshot.exists()) {
                    rootView.progressBar.visibility = View.GONE
                    val data = snapshot.data
                    val matchedMoviesList = data?.getValue("matchedMoviesList") as ArrayList<*>

                    listItems.clear()
                    for (u in matchedMoviesList) {
                        val user = u as HashMap<*, *>
                        val movieImage = user["movieImage"].toString()
                        val movieName = user["movieName"].toString()
                        val rating = user["rating"].toString()
                        val description = user["description"].toString()
                        listItems.add(MatchedMoviesList(movieImage, movieName, rating, description))
                        listAdapter.updateListItems(listItems)
                    }
                }
            }
    }
}