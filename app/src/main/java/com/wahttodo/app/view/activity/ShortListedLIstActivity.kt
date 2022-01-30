package com.wahttodo.app.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.wahttodo.app.R
import com.wahttodo.app.adapter.DecisionShortListedAdapter
import com.wahttodo.app.model.MatchedMoviesList
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.openClearActivity
import com.wahttodo.app.utils.setupCommonRecyclerViewsProperty
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.viewModel.UserListViewModel
import kotlinx.android.synthetic.main.empty_screen.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.progressbar.view.*
import kotlinx.android.synthetic.main.recyclerview.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.HashMap

class ShortListedLIstActivity : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    var imFrom = ""
    private lateinit var roomId: String
    private lateinit var shortListedFirebaseListener: ListenerRegistration
    lateinit var rootView: View
    lateinit var viewModelUser: UserListViewModel
    lateinit var listAdapter: DecisionShortListedAdapter
    var listItems = ArrayList<MatchedMoviesList>()
    lateinit var mLayoutManager: LinearLayoutManager
    var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_short_listed_list)
        initView()
    }

    private fun initView() {
        db = FirebaseFirestore.getInstance()

        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        roomId = SharePreferenceManager.getInstance(this).getValueString(Constants.ROOM_ID).toString()

        tvToolbarTitle.text="Matches"
        imgHome.setOnClickListener{
            openClearActivity(HomeActivity::class.java)
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
//        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)!!
        progressBar.visibility = View.VISIBLE

        this.setupCommonRecyclerViewsProperty(recyclerView, Constants.VERTICAL)
        listAdapter = DecisionShortListedAdapter(this)
        recyclerView.adapter = listAdapter

        getListOfShortListed()
    }

    private fun getListOfShortListed() {
        shortListedFirebaseListener = db.collection("whatToDoCollection")
            .document(roomId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    showToastMsg("Listen failed. $error")
                    progressBar.visibility = View.GONE
                }

                if (snapshot != null && snapshot.exists()) {
                    progressBar.visibility = View.GONE
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
                    if(listItems.size ==0){
                        txtContent.text = "There is no matched yet"
                        emptyLayout.visibility = View.VISIBLE
                    }
                }
            }
    }
}