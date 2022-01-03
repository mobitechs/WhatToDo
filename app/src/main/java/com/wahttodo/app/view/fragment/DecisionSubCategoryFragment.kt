package com.wahttodo.app.view.fragment

import android.os.Bundle
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
import com.wahttodo.app.R
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.showToastMsg


class DecisionSubCategoryFragment : Fragment() {


    lateinit var rootView: View
    var selectedLanguage=""
    var languageArray = Constants.languageArray
    lateinit var spinnerLanguage: AppCompatSpinner

    var selectedType=""
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
        spinnerLanguage = rootView.findViewById(R.id.spinnerLanguage)
        spinnerType = rootView.findViewById(R.id.spinnerType)
        btnSubmit = rootView.findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            requireContext().showToastMsg(selectedLanguage+" "+selectedType)
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


}