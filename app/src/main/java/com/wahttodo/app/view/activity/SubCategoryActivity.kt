package com.wahttodo.app.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.wahttodo.app.R
import com.wahttodo.app.model.DumpMovieDetails
import com.wahttodo.app.model.DumpedMoviesList
import com.wahttodo.app.model.MovieList
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.openActivity
import com.wahttodo.app.utils.openClearActivity
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.viewModel.UserListViewModel
import kotlinx.android.synthetic.main.toolbar.*

class SubCategoryActivity : AppCompatActivity() {

    var movieListItems = ArrayList<MovieList>()

    var imFrom = ""
    lateinit var db: FirebaseFirestore
    private lateinit var waitingRoomFirebaseListener: ListenerRegistration
    var listItems = ArrayList<DumpedMoviesList>()

    private var userCount = ""
    private lateinit var roomId: String
    lateinit var rootView: View
    var selectedLanguage = ""
    var selectedLanguageCode = ""
    var languageArray = Constants.languageArray
    lateinit var spinnerLanguage: AppCompatSpinner

    var selectedType = ""
    var selectedTypeId = ""

    var typeArray = Constants.typeArray
    lateinit var spinnerType: AppCompatSpinner
    lateinit var btnSubmit: AppCompatButton

    lateinit var viewModelUser: UserListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_category)
        initView()
    }

    private fun initView() {
        db = FirebaseFirestore.getInstance()
        viewModelUser = ViewModelProvider(this).get(UserListViewModel::class.java)
        roomId = SharePreferenceManager.getInstance(this).getValueString(Constants.ROOM_ID).toString()



        getJoinedUserCount()

        spinnerLanguage = findViewById(R.id.spinnerLanguage)
        spinnerType = findViewById(R.id.spinnerType)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {


            viewModelUser.searchMovies(selectedLanguageCode,selectedTypeId)

            viewModelUser.movieList.observe(this, Observer {
                //listAdapter.updateListItems(it)
                movieListItems.addAll(it)

                for (item in it) {
                    listItems.add(DumpedMoviesList(Constants.BASE_IMAGE_PATH+""+item.poster_path, item.title, item.vote_average.toString(), item.overview, "0"))
                }
                if (listItems.size != 0) {
                    checkIfRoomExist()
                }
            })


        }
        tvToolbarTitle.text="Filters"
        imgHome.setOnClickListener{
            openClearActivity(HomeActivity::class.java)
        }

        setupLanguageSpinner()
        setupTypeSpinner()
    }


    private fun setupLanguageSpinner() {
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_layout,
            languageArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.setAdapter(adapter)
//        spinnerControllerQty.setSelection(listItem.controllerQty - 1)
        spinnerLanguage.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedLanguage = languageArray[p2]
                selectedLanguageCode = Constants.languageArrayCode[p2]
            }
        })
    }

    private fun setupTypeSpinner() {
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_layout,
            typeArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.setAdapter(adapter)
//        spinnerType.setSelection(listItem.controllerQty - 1)
        spinnerType.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedType = typeArray[p2]
                selectedTypeId = Constants.typeIdArray[p2]
            }
        })
    }

    private fun checkIfRoomExist() {
        val docRef = db.collection("dumpMoviesCollection").document(roomId)
        docRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result.exists()) {
                    // if room exist update array
                    updateRoomData()
                }
                else {
                    // if room not exist then create with existing list
                    createRoomAndAddData()
                }
            }
        }
    }

    private fun updateRoomData() {

        db.collection("dumpMoviesCollection")
            .document(roomId)
            .get()
            .addOnSuccessListener {
                val dumpedMoviesList = it.data?.getValue("dumpedMoviesList") as ArrayList<*>
                for (item in dumpedMoviesList) {
                    val movie = item as java.util.HashMap<*, *>
                    val movieImage = movie["movieImage"].toString()
                    val movieName = movie["movieName"].toString()
                    val rating = movie["rating"].toString()
                    val description = movie["description"].toString()
                    val matchedCount = movie["matchedCount"].toString()

                    var newEntry = true
                    for (i in listItems) {
                        if (i.movieName == movieName) {
                            newEntry = false
                        }
                    }
                    if (newEntry) {
                        listItems.add(DumpedMoviesList(movieImage, movieName, rating, description, matchedCount))
                    }
                }

                val dumpMovieDetails = DumpMovieDetails(listItems, userCount)

                db.collection("dumpMoviesCollection")
                    .document(roomId)
                    .set(dumpMovieDetails)
                    .addOnSuccessListener {
                        Log.d("TAG","Record added successfully.")
                        if(this::waitingRoomFirebaseListener.isInitialized){
                            waitingRoomFirebaseListener.remove()
                        }
                        listItems.clear()
                        openActivity(ListingCardActivity::class.java)
                    }
                    .addOnFailureListener {
                        this.showToastMsg("Record failed to add.")
                    }
            }
            .addOnFailureListener {
                showToastMsg("Error getting room data" + it.message)
            }

//        db.collection("dumpMoviesCollection")
//            .document(roomId)
//            .update("dumpedMoviesList", listItems, "noOfUsers", userCount)
//            .addOnSuccessListener {
//                Log.d("TAG", "Movie updated entries add")
//                openActivity(ListingCardActivity::class.java)
//                if(this::waitingRoomFirebaseListener.isInitialized){
//                    waitingRoomFirebaseListener.remove()
//                }
//            }
//            .addOnFailureListener {
//                this.showToastMsg("User updated entry failed to add")
//            }
    }

    private fun createRoomAndAddData() {
        // listItem will be the data which you will get by selecting language and type
        val dumpMovieDetails = DumpMovieDetails(listItems, userCount)

        db.collection("dumpMoviesCollection")
            .document(roomId)
            .set(dumpMovieDetails)
            .addOnSuccessListener {
                Log.d("TAG","Record added successfully.")
                if(this::waitingRoomFirebaseListener.isInitialized){
                    waitingRoomFirebaseListener.remove()
                }
                listItems.clear()
                openActivity(ListingCardActivity::class.java)
            }
            .addOnFailureListener {
                this.showToastMsg("Record failed to add.")
            }
    }

    private fun getJoinedUserCount() {

        waitingRoomFirebaseListener = db.collection("whatToDoCollection")
            .document(roomId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    this.showToastMsg("Listen failed. $error")
                }

                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data
                    val joinedUserList = data?.getValue("joinedUserList") as ArrayList<*>

                    userCount = joinedUserList.size.toString()
                }
                else{
                    this.showToastMsg("not exist failed.")
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(this::waitingRoomFirebaseListener.isInitialized){
            waitingRoomFirebaseListener.remove()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModelUser.movieList.removeObservers(this)
    }
}