package com.wahttodo.app.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.wahttodo.app.R
import com.wahttodo.app.adapter.DecisionListCardStackAdapter
import com.wahttodo.app.model.DumpedMoviesList
import com.wahttodo.app.model.MatchedMoviesList
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.view.activity.WaitingRoomActivity
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.view.activity.WaitingRoomActivity.Companion.db
import com.wahttodo.app.viewModel.UserListViewModel
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.fragment_decision_listing.view.*
import java.util.HashMap

class DecisionListingFragment : Fragment(), CardStackListener {

    var listSize: Int = 0
    private lateinit var decisionListingFirebaseListener: ListenerRegistration
    private lateinit var movieCountUpdateData: DumpedMoviesList
    private lateinit var movieCountDeleteData: DumpedMoviesList
    lateinit var listAdapter: DecisionListCardStackAdapter
    val listItems = ArrayList<DumpedMoviesList>()
    private lateinit var manager: CardStackLayoutManager
    lateinit var rootView: View
    lateinit var viewModelUser: UserListViewModel
    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var roomId: String
    var userId = ""
    var position=0

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

        userId = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        roomId = SharePreferenceManager.getInstance(requireContext()).getValueString(Constants.ROOM_ID).toString()

        (context as WaitingRoomActivity).setToolBarTitle("Movie List")
//        setupRecyclerView()
        setupCardStackView()
        getDecisionSelectedList()
    }

    private fun setupRecyclerView() {
    }

    private fun setupCardStackView() {
        manager = CardStackLayoutManager(requireActivity(), this)
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.Manual)
        manager.setOverlayInterpolator(LinearInterpolator())
        rootView.cardStackView.layoutManager = manager
        listAdapter = DecisionListCardStackAdapter(requireActivity())
        rootView.cardStackView.adapter = listAdapter
        rootView.cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction?.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction?) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        position = manager.topPosition
        if (direction == Direction.Right) {
            var itemMovieName = listItems[position - 1].movieName
            db.collection("dumpMoviesCollection")
                .document(roomId)
                .get()
                .addOnSuccessListener {
                    val noOfUsers = it.data?.getValue("noOfUsers").toString()
                    val dumpedMoviesList = it.data?.getValue("dumpedMoviesList") as ArrayList<*>
                    for (u in dumpedMoviesList) {
                        val movieDetails = u as HashMap<*, *>
                        val movieImage = movieDetails["movieImage"].toString()
                        val movieName = movieDetails["movieName"].toString()
                        val rating = movieDetails["rating"].toString()
                        val description = movieDetails["description"].toString()
                        val matchedCount = movieDetails["matchedCount"].toString()
                        val updatedCount = matchedCount.toInt() + 1
                        if (itemMovieName == movieName) {
                            movieCountDeleteData = DumpedMoviesList(movieImage, movieName, rating, description, matchedCount)
                            movieCountUpdateData = DumpedMoviesList(movieImage, movieName, rating, description, updatedCount.toString())
                            listSize -= 1
                            movieDeleteData()
                        }
                    }

//
                }
                .addOnFailureListener {
                    requireActivity().showToastMsg("Error getting room data" + it.message)
                }
        }
        else{
            if(listItems.lastIndex == (position - 1)){
                requireContext().showToastMsg("Congratulations")
                (context as WaitingRoomActivity).displayDecisionShortListed()
            }
        }


    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        Log.d("CardStackView", "onCardAppeared: ($position)")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        Log.d("CardStackView", "onCardDisappeared: ($position)")
    }

    private fun movieDeleteData() {
        if (this::movieCountDeleteData.isInitialized) {
            db.collection("dumpMoviesCollection")
                .document(roomId)
                .update("dumpedMoviesList", FieldValue.arrayRemove(movieCountDeleteData))
                .addOnSuccessListener {
                    listSize += 1
                    movieUpdateData()
                }
                .addOnFailureListener {
                    requireActivity().showToastMsg("User left entry failed to remove")
                }
        }
    }

    private fun movieUpdateData() {
        if (this::movieCountUpdateData.isInitialized) {
            db.collection("dumpMoviesCollection")
                .document(roomId)
                .update("dumpedMoviesList", FieldValue.arrayUnion(movieCountUpdateData))
                .addOnSuccessListener {
                    Log.d("TAG", "User updated entry add")

                    if(listItems.lastIndex == (position - 1)){
                        requireContext().showToastMsg("Congratulations")
                        (context as WaitingRoomActivity).displayDecisionShortListed()
                    }
                }
                .addOnFailureListener {
                    requireActivity().showToastMsg("User updated entry failed to add")
                }
        }
    }

    private fun getDecisionSelectedList() {

        decisionListingFirebaseListener = db.collection("dumpMoviesCollection")
            .document(roomId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    requireActivity().showToastMsg("Listen failed. $error")
                }

                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data
                    val noOfUsers = data?.getValue("noOfUsers").toString()
                    val dumpedMoviesList = data?.getValue("dumpedMoviesList") as ArrayList<*>

                    var size = listSize
                    if (dumpedMoviesList.size != listSize) {
//                        listItems.clear()
                        for (u in dumpedMoviesList) {
                            val moviesList = u as HashMap<*, *>
                            val movieImage = moviesList["movieImage"].toString()
                            val movieName = moviesList["movieName"].toString()
                            val rating = moviesList["rating"].toString()
                            val description = moviesList["description"].toString()
                            val matchedCount = moviesList["matchedCount"].toString()

                            val item = DumpedMoviesList(movieImage, movieName, rating, description, matchedCount)

                            if (listItems.size == 0) {
                                listItems.add(DumpedMoviesList(movieImage, movieName, rating, description, matchedCount))
//                                listAdapter.updateListItems(listItems)
                                listAdapter.addItemToList(DumpedMoviesList(movieImage, movieName, rating, description, matchedCount))
                            }
                            else {
                                var newEntry = true
                                for (i in listItems) {
                                    if (i.movieName == movieName) {
                                        newEntry = false
                                    }
                                }
                                if (newEntry) {
                                    listItems.add(DumpedMoviesList(movieImage, movieName, rating, description, matchedCount))
//                                listAdapter.updateListItems(listItems)
                                    listAdapter.addItemToList(DumpedMoviesList(movieImage, movieName, rating, description, matchedCount))
                                }
                            }
                        }
                        listSize = dumpedMoviesList.size
                    }

                    for (u in dumpedMoviesList) {
                        val moviesList = u as HashMap<*, *>
                        val movieImage = moviesList["movieImage"].toString()
                        val movieName = moviesList["movieName"].toString()
                        val rating = moviesList["rating"].toString()
                        val description = moviesList["description"].toString()
                        val matchedCount = moviesList["matchedCount"].toString()
                        if (matchedCount == noOfUsers) {
                            var matchedMoviesList = MatchedMoviesList(movieImage, movieName, rating, description)
                            addMatchedMovieToWhatToDoList(matchedMoviesList)
                        }
                    }
                }
                else{
                    requireActivity().showToastMsg("not exist failed.")
                }
            }
    }

    private fun addMatchedMovieToWhatToDoList(matchedMoviesList: MatchedMoviesList) {
        db.collection("whatToDoCollection")
            .document(roomId)
            .update("matchedMoviesList", FieldValue.arrayUnion(matchedMoviesList))
            .addOnSuccessListener {
                Log.d("TAG", "User updated entry add")
            }
            .addOnFailureListener {
                requireActivity().showToastMsg("User updated entry failed to add")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(this::decisionListingFirebaseListener.isInitialized){
            decisionListingFirebaseListener.remove()
        }
    }
}