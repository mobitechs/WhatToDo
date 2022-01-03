package com.wahttodo.app.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahttodo.app.R
import com.wahttodo.app.adapter.DecisionCategoryListAdapter
import com.wahttodo.app.model.CategoryListItems
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.viewModel.UserListViewModel

class DecisionCategoryFragment : Fragment() {

    lateinit var rootView: View
    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: DecisionCategoryListAdapter
    var listItems = ArrayList<CategoryListItems>()
    lateinit var mLayoutManager: LinearLayoutManager
    var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_decision_category, container, false)
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
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

        mLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        listAdapter = DecisionCategoryListAdapter(requireActivity())
        recyclerView.adapter = listAdapter
        listAdapter.updateListItems(listItems)

        viewModelUser.showProgressBar.observe(requireActivity(), Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        getListOfCategory()


    }

    private fun getListOfCategory() {

        listItems.add(CategoryListItems("1", "Movie"))
        listItems.add(CategoryListItems("2", "Food"))
        listItems.add(CategoryListItems("3", "Games"))
        listItems.add(CategoryListItems("4", "Dance"))
        listAdapter.updateListItems(listItems)
    }


}