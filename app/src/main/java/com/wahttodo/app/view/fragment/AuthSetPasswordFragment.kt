package com.wahttodo.app.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import com.wahttodo.app.R
import com.wahttodo.app.callbacks.ApiResponse
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.apiPostCall
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.view.activity.AuthActivity
import kotlinx.android.synthetic.main.fragment_auth_set_passworg.*
import org.json.JSONException
import org.json.JSONObject


class AuthSetPasswordFragment : Fragment(), ApiResponse {

    var email = ""
    lateinit var rootView: View
    lateinit var layoutLoader: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_auth_set_passworg, container, false)

        initView()
        return rootView
    }

    private fun initView() {
        val newPasswordSubmit: Button = rootView.findViewById(R.id.newPasswordSubmit)
        layoutLoader = rootView.findViewById(R.id.layoutLoader)
        email = arguments?.getString("email").toString()
        newPasswordSubmit.setOnClickListener {
            if (etOtp.text.toString().equals("")) {
                requireContext().showToastMsg("Enter OTP")
            } else if (etPassword.text.toString().equals("")) {
                requireContext().showToastMsg("Enter Password")
            } else if (etConfirmPassword.text.toString().equals("")) {
                requireContext().showToastMsg("Enter Confirm Password ")
            } else {

                if (!etPassword.text.toString().equals(etConfirmPassword.text.toString())) {
                    requireContext().showToastMsg("Passwords are not matched")
                } else {
                    layoutLoader.visibility = View.VISIBLE
                    val method = "setNewPassword"
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("method", method)
                        jsonObject.put("otp", etOtp.text.toString())
                        jsonObject.put("email", email)
                        jsonObject.put("password", etPassword.text.toString())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    apiPostCall(Constants.BASE_URL, jsonObject, this, method)
                }

            }
        }

    }

    override fun onSuccess(data: Any, tag: String) {

        if (data.equals("NEW_PASSWORD_SUCCESSFULLY_SET")) {
            requireContext().showToastMsg("Password Set Successfully")
//            val navController = activity?.let { Navigation.findNavController(it, R.id.navFragment) }
//            navController?.navigate(R.id.action_setPasswordFragment_to_login)
            (context as AuthActivity).openLoginPage()
        } else {
            requireContext().showToastMsg(data.toString())
        }
        layoutLoader.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        requireContext().showToastMsg(message)
        layoutLoader.visibility = View.GONE
    }
}