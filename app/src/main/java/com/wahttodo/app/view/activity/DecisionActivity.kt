package com.wahttodo.app.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahttodo.app.R
import com.wahttodo.app.adapter.DecisionCategoryListAdapter
import com.wahttodo.app.model.CategoryListItems
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.*
import com.wahttodo.app.viewModel.UserListViewModel
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recyclerview.*
import kotlinx.android.synthetic.main.toolbar.*

class DecisionActivity : AppCompatActivity() {

    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: DecisionCategoryListAdapter
    var listItems = ArrayList<CategoryListItems>()
    lateinit var mLayoutManager: LinearLayoutManager
    var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decidion)

        userId =
            SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)
                ?.get(0)?.userId.toString()


        setupRecyclerView()

    }


    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
//        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!
//        val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)!!

//        mLayoutManager = LinearLayoutManager(this)
//        recyclerView.layoutManager = mLayoutManager
//        recyclerView.itemAnimator = DefaultItemAnimator()

        setupCommonGridRecyclerViewsProperty(recyclerView, 2)
        listAdapter = DecisionCategoryListAdapter(this)
        recyclerView.adapter = listAdapter


        viewModelUser.showProgressBar.observe(this, Observer {
            if (it) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })

        tvToolbarTitle.text="Decision For"
        imgHome.setOnClickListener{
            openClearActivity(HomeActivity::class.java)
        }
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