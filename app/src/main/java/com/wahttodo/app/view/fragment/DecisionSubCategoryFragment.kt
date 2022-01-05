package com.wahttodo.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.wahttodo.app.R
import com.wahttodo.app.model.*
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.getAllMoviesList
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.view.activity.WaitingRoomActivity
import com.wahttodo.app.view.activity.WaitingRoomActivity.Companion.db
import kotlinx.android.synthetic.main.loader.*


class DecisionSubCategoryFragment : Fragment() {
    var listItems = ArrayList<DumpedMoviesList>()
    private var userCount: Int = 0
    private lateinit var roomId: String
    lateinit var rootView: View
    var selectedLanguage = ""
    var languageArray = Constants.languageArray
    lateinit var spinnerLanguage: AppCompatSpinner

    var selectedType = ""
    var typeArray = Constants.typeArray
    lateinit var spinnerType: AppCompatSpinner
    lateinit var btnSubmit: AppCompatButton

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
        userCount = arguments?.getInt("user_count")!!
        roomId = SharePreferenceManager.getInstance(requireContext()).getValueString(Constants.ROOM_ID).toString()

        (context as WaitingRoomActivity).setToolBarTitle("Categories")

        spinnerLanguage = rootView.findViewById(R.id.spinnerLanguage)
        spinnerType = rootView.findViewById(R.id.spinnerType)
        btnSubmit = rootView.findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            requireContext().showToastMsg("$selectedLanguage $selectedType")
            // Please add data which will you get from selected language and type. but first you have to check if room exist

            var moviesListItems = getAllMoviesList(ArrayList<AllMoviesList>())
            for (item in moviesListItems) {
                if (item.language == selectedLanguage && item.type == selectedType) {
                    listItems.add(DumpedMoviesList(item.movieImage, item.movieName, item.rating, item.description, item.matchedCount))
                }
            }
            checkIfRoomExist()
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
                .update("dumpedMoviesList", FieldValue.arrayUnion(item))
                .addOnSuccessListener {
                    Log.d("TAG", "User updated entry add")
                    (context as WaitingRoomActivity).displayDecisionCardListing()
                }
                .addOnFailureListener {
                    requireActivity().showToastMsg("User updated entry failed to add")
                }
        }
        (context as WaitingRoomActivity).displayDecisionCardListing()
    }

    private fun createRoomAndAddData() {
        // listItem will be the data which you will get by selecting language and type
        val dumpMovieDetails = DumpMovieDetails(listItems, userCount)

        db.collection("dumpMoviesCollection")
            .document(roomId)
            .set(dumpMovieDetails)
            .addOnSuccessListener {
                Log.d("TAG","Record added successfully.")
                (context as WaitingRoomActivity).displayDecisionCardListing()
            }
            .addOnFailureListener {
                requireActivity().showToastMsg("Record failed to add.")
            }
    }
}