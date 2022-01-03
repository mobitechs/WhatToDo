package com.wahttodo.app.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahttodo.app.R
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.viewModel.UserListViewModel

class DecisionListingFragment : Fragment() {

    lateinit var rootView: View
    lateinit var viewModelUser: UserListViewModel
    lateinit var mLayoutManager: LinearLayoutManager
    var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_decision_listing, container, false)
        intView()
        return rootView
    }

    private fun intView() {

        userId =
            SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userId.toString()

        setupRecyclerView()
    }

    private fun setupRecyclerView() {


    }

}