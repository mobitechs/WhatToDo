package com.wahttodo.app.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import com.google.android.material.textfield.TextInputEditText
import com.wahttodo.app.R
import com.wahttodo.app.callbacks.ApiResponse
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.apiPostCall
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.view.activity.AuthActivity
import org.json.JSONException
import org.json.JSONObject


class AuthForgetPasswordFragment : Fragment(), ApiResponse {

    lateinit var rootView: View
    var email = ""
    lateinit var layoutLoader: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_auth_forget_password, container, false)
        intView()
        return rootView
    }

    private fun intView() {

        layoutLoader = rootView.findViewById(R.id.layoutLoader)
        val emailSubmit: Button = rootView.findViewById(R.id.emailSubmit)!!
        val etEmail: TextInputEditText = rootView.findViewById(R.id.etEmail)!!

        emailSubmit.setOnClickListener {
            email = etEmail.text.toString()
            if (email.equals("")) {
                requireContext().showToastMsg("Enter Email Id")
            } else {
                layoutLoader.visibility = View.VISIBLE
                val method = "forgotPassword"
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("method", method)
                    jsonObject.put("email", email)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                apiPostCall(Constants.BASE_URL, jsonObject, this, method)
            }
        }
    }

    override fun onSuccess(data: Any, tag: String) {

        if (data.equals("INVALID_EMAIL")) {
            requireContext().showToastMsg("Please Enter Valid Email")
        } else {
            requireContext().showToastMsg("OTP Sent on your Email")
//            var bundle = Bundle()
//            bundle.putString("email", email)
//            var navController = activity?.let { Navigation.findNavController(it, R.id.navFragment) }
//            navController?.navigate(
//                R.id.action_forgotPasswordFragment_to_setPasswordFragment,
//                bundle
//            )
            (context as AuthActivity).openSetPassword(email)
        }
        layoutLoader.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        requireContext().showToastMsg(message)
        layoutLoader.visibility = View.GONE
    }


}