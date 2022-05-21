package com.wahttodo.app.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.wahttodo.app.R
import com.wahttodo.app.adapter.DecisionListCardStackAdapter
import com.wahttodo.app.model.DumpedMoviesList
import com.wahttodo.app.model.MatchedMoviesList
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.*
import com.wahttodo.app.viewModel.UserListViewModel
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.activity_listing_card.*
import kotlinx.android.synthetic.main.adapter_item_card_stack.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.HashMap

class ListingCardActivity : AppCompatActivity() , CardStackListener {

    private lateinit var db: FirebaseFirestore
    var imFrom = ""
    var listSize: Int = 0
    private lateinit var shortListedFirebaseListener: ListenerRegistration
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
    var noOfUsers=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing_card)

        intView()
    }

    private fun intView() {
        db = FirebaseFirestore.getInstance()
        userId = SharePreferenceManager.getInstance(this).getUserLogin(Constants.USERDATA)?.get(0)?.userId.toString()
        roomId = SharePreferenceManager.getInstance(this).getValueString(Constants.ROOM_ID).toString()


        setupCardStackView()
        getDecisionSelectedList()
        getListOfShortListed()

        btnShowShortListed.setOnClickListener {
            openActivity(ShortListedLIstActivity::class.java)
        }

        tvToolbarTitle.text="Listing"

        imgHome.setOnClickListener{
            openClearActivity(HomeActivity::class.java)
        }
    }



    private fun setupCardStackView() {
        manager = CardStackLayoutManager(this, this)
        manager.setStackFrom(StackFrom.Top)
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
        cardStackView.layoutManager = manager
        listAdapter = DecisionListCardStackAdapter(this)
        cardStackView.adapter = listAdapter
        cardStackView.itemAnimator.apply {
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
                    noOfUsers = it.data?.getValue("noOfUsers").toString()
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
                            imgMoviePosterPreview.setImage(movieImage, R.drawable.cinema)
                            textMovieNamePreview.text = movieName
                            txtDescriptionPreview.text = description
                            ratingBarPreview.rating = rating.toFloat()/2
                            //showToastMsg(movieName)

                            movieCountDeleteData = DumpedMoviesList(movieImage, movieName, rating, description, matchedCount)
                            movieCountUpdateData = DumpedMoviesList(movieImage, movieName, rating, description, updatedCount.toString())
                            listSize -= 1
                            movieDeleteData()
                            if (noOfUsers == updatedCount.toString()) {
                                //showToastMsg(noOfUsers + "= "+ updatedCount.toString())
                                layoutMatched.visibility =  View.VISIBLE
                                layoutPreview.visibility =  View.VISIBLE


                                if(noOfUsers == "1"){
                                    txtMatched.text = "Liked"
                                }else{
                                    txtMatched.text = "Its\nMatched"
                                }

                                Handler().postDelayed({
                                    layoutMatched.visibility =  View.GONE }, 3000.toLong())
                            }
                        }
                    }

                }
                .addOnFailureListener {
                    this.showToastMsg("Error getting room data" + it.message)
                }
        }
        else{
            layoutPreview.visibility =  View.GONE
            layoutMatched.visibility =  View.VISIBLE
            if(noOfUsers == "1"){
                txtMatched.text = "Not Liked"
            }else{
                txtMatched.text = "Not\nMatched"
            }

            Handler().postDelayed({
                layoutMatched.visibility =  View.GONE }, 1000.toLong())

        }
        if(listItems.lastIndex == (position - 1)){
            this.showToastMsg("Congratulations")

            openActivity(ShortListedLIstActivity::class.java)
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
                    this.showToastMsg("User left entry failed to remove")
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
                        this.showToastMsg("Congratulations")

                        openActivity(ShortListedLIstActivity::class.java)
                    }
                }
                .addOnFailureListener {
                    this.showToastMsg("User updated entry failed to add")
                }
        }
    }

    private fun getDecisionSelectedList() {
        decisionListingFirebaseListener = db.collection("dumpMoviesCollection")
            .document(roomId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    this.showToastMsg("Listen failed. $error")
                }

                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data
                    noOfUsers = data?.getValue("noOfUsers").toString()
                    val dumpedMoviesListWithoutShuffle = data?.getValue("dumpedMoviesList") as ArrayList<*>
//                    val dumpedMoviesList = data?.getValue("dumpedMoviesList") as ArrayList<*>
                    val dumpedMoviesList = dumpedMoviesListWithoutShuffle.shuffled()

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
                                if (imFrom == "HomeActivity") {
                                    if (noOfUsers != matchedCount) {
                                        listItems.add(item)
                                        listAdapter.addItemToList(item)
                                    }
                                }
                                else {
                                    listItems.add(item)
                                    listAdapter.addItemToList(item)
                                }
                            }
                            else {
                                var newEntry = true
                                for (i in listItems) {
                                    if (i.movieName == movieName) {
                                        newEntry = false
                                    }
                                }
                                if (newEntry) {
                                    if (imFrom == "HomeActivity") {
                                        if (noOfUsers != matchedCount) {
                                            listItems.add(item)
                                            listAdapter.addItemToList(item)
                                        }
                                    }
                                    else {
                                        listItems.add(item)
                                        listAdapter.addItemToList(item)
                                    }
                                }
                            }
                        }
                        if (listItems.size == 0 && imFrom == "HomeActivity") {
//                            (context as HomeActivity).displayDecisionShortListed()
                            openActivity(ShortListedLIstActivity::class.java)
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
                    openActivity(SubCategoryActivity::class.java)
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
                this.showToastMsg("User updated entry failed to add")
            }
    }

    private fun getListOfShortListed() {
        shortListedFirebaseListener = db.collection("whatToDoCollection")
            .document(roomId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    this.showToastMsg("Listen failed. $error")
                }

                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data
                    val matchedMoviesList = data?.getValue("matchedMoviesList") as ArrayList<*>

                    if (matchedMoviesList.size != 0) {
                        btnShowShortListed.visibility = View.VISIBLE
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(this::decisionListingFirebaseListener.isInitialized){
            decisionListingFirebaseListener.remove()
        }

        if(this::shortListedFirebaseListener.isInitialized){
            shortListedFirebaseListener.remove()
        }
    }
}