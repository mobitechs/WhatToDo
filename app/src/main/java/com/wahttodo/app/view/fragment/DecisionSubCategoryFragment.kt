package com.wahttodo.app.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.wahttodo.app.model.*
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.Constants.Companion.languageArrayCode
import com.wahttodo.app.utils.Constants.Companion.typeIdArray
import com.wahttodo.app.utils.getAllMoviesList
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.view.activity.HomeActivity
import com.wahttodo.app.view.activity.WaitingRoomActivity
import com.wahttodo.app.viewModel.UserListViewModel


class DecisionSubCategoryFragment : Fragment() {

    var imFrom = ""
    lateinit var db: FirebaseFirestore
    private lateinit var waitingRoomFirebaseListener: ListenerRegistration
    var listItems = ArrayList<DumpedMoviesList>()
    var movieListItems = ArrayList<MovieList>()
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_decision_sub_category, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        db = FirebaseFirestore.getInstance()
        viewModelUser = ViewModelProvider(requireActivity()).get(UserListViewModel::class.java)
        roomId = SharePreferenceManager.getInstance(requireContext()).getValueString(Constants.ROOM_ID).toString()
        imFrom = arguments?.getString("imFrom").toString()

        if (imFrom == "HomeActivity") {
            (context as HomeActivity).setToolBarTitle("Categories")
        }
        else {
            (context as WaitingRoomActivity).setToolBarTitle("Categories")
        }

        getListOfUsers()

        spinnerLanguage = rootView.findViewById(R.id.spinnerLanguage)
        spinnerType = rootView.findViewById(R.id.spinnerType)
        btnSubmit = rootView.findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            requireContext().showToastMsg("$selectedLanguage $selectedType")
            // Please add data which will you get from selected language and type. but first you have to check if room exist


            viewModelUser.searchMovies(selectedLanguageCode,selectedTypeId)

            viewModelUser.movieList.observe(requireActivity(), Observer {
                //listAdapter.updateListItems(it)
                movieListItems.addAll(it)

                for (item in it) {
                    listItems.add(DumpedMoviesList(Constants.BASE_IMAGE_PATH+""+item.poster_path, item.title, item.vote_average.toString(), item.overview, "0"))
                }
                if (listItems.size != 0) {
                    checkIfRoomExist()
                }
            })


//            var moviesListItems = getAllMoviesList(ArrayList<AllMoviesList>())
//            for (item in moviesListItems) {
//                if (item.language == selectedLanguage && item.type == selectedType) {
//                    listItems.add(DumpedMoviesList(item.movieImage, item.movieName, item.rating, item.description, item.matchedCount))
//                }
//            }
        }

        setupLanguageSpinner()
        setupTypeSpinner()
    }

    private fun setupLanguageSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
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
                selectedLanguageCode = languageArrayCode[p2]
            }
        })
    }

    private fun setupTypeSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
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
                selectedTypeId = typeIdArray[p2]
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
        for (item in listItems) {
            db.collection("dumpMoviesCollection")
                .document(roomId)
                .update("dumpedMoviesList", FieldValue.arrayUnion(item), "noOfUsers", userCount)
                .addOnSuccessListener {
                    Log.d("TAG", "User updated entry add")
                    if (imFrom == "HomeActivity") {
                        (context as HomeActivity).displayDecisionCardListing()
                    }
                    else {
                        (context as WaitingRoomActivity).displayDecisionCardListing()
                    }
                    if(this::waitingRoomFirebaseListener.isInitialized){
                        waitingRoomFirebaseListener.remove()
                    }
                }
                .addOnFailureListener {
                    requireActivity().showToastMsg("User updated entry failed to add")
                }
        }
        if (imFrom == "HomeActivity") {
            (context as HomeActivity).displayDecisionCardListing()
        }
        else {
            (context as WaitingRoomActivity).displayDecisionCardListing()
        }
    }

    private fun createRoomAndAddData() {
        // listItem will be the data which you will get by selecting language and type
        val dumpMovieDetails = DumpMovieDetails(listItems, userCount)

        db.collection("dumpMoviesCollection")
            .document(roomId)
            .set(dumpMovieDetails)
            .addOnSuccessListener {
                Log.d("TAG","Record added successfully.")
                if (imFrom == "HomeActivity") {
                    (context as HomeActivity).displayDecisionCardListing()
                }
                else {
                    (context as WaitingRoomActivity).displayDecisionCardListing()
                }
                if(this::waitingRoomFirebaseListener.isInitialized){
                    waitingRoomFirebaseListener.remove()
                }
            }
            .addOnFailureListener {
                requireActivity().showToastMsg("Record failed to add.")
            }
    }

    private fun getListOfUsers() {

        waitingRoomFirebaseListener = db.collection("whatToDoCollection")
            .document(roomId)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    requireActivity().showToastMsg("Listen failed. $error")
                }

                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data
                    val joinedUserList = data?.getValue("joinedUserList") as ArrayList<*>

                    userCount = joinedUserList.size.toString()
                }
                else{
                    requireActivity().showToastMsg("not exist failed.")
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