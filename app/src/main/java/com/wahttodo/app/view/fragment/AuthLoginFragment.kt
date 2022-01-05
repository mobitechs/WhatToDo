package com.wahttodo.app.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wahttodo.app.R
import com.wahttodo.app.callbacks.ApiResponse
import com.wahttodo.app.model.UserModel
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.checkLogin
import com.wahttodo.app.utils.showToastMsg
import com.wahttodo.app.view.activity.AuthActivity
import java.util.ArrayList

class AuthLoginFragment : Fragment(), ApiResponse {

    lateinit var rootView: View
    lateinit var txtEmail: AppCompatEditText
    lateinit var btnSubmit: AppCompatButton
    lateinit var layoutLoader: RelativeLayout
    var email = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_auth_login, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        txtEmail = rootView.findViewById(R.id.txtEmail)
        btnSubmit = rootView.findViewById(R.id.btnSubmit)
        layoutLoader = rootView.findViewById(R.id.layoutLoader)

        btnSubmit.setOnClickListener {
            gotoNext()
        }

        txtEmail.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.getAction() === KeyEvent.ACTION_DOWN) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            gotoNext()
                            return true
                        }
                        else -> {
                        }
                    }
                }
                return false
            }
        })
    }
    private fun gotoNext() {
        email = txtEmail.text.toString()
        if (email.equals("") ) {
            requireContext().showToastMsg("Please Enter Email.")
        } else {
            (context as AuthActivity).openOTPPage(email)
        }
    }

    override fun onSuccess(data: Any, tag: kotlin.String) {
        if (data.equals("NEW_USER")) {
            (context as AuthActivity).openRegistrationFrag(email)
        } else {
            val gson = Gson()
            val type = object : TypeToken<ArrayList<UserModel>>() {}.type
            var user: ArrayList<UserModel>? = gson.fromJson(data.toString(), type)

            Log.d("user", "" + user)
            SharePreferenceManager.getInstance(requireContext()).saveUserLogin(Constants.USERDATA, user)
//            userType = SharePreferenceManager.getInstance(requireContext()).getUserLogin(Constants.USERDATA)?.get(0)!!.userType

            SharePreferenceManager.getInstance(requireContext()).save(Constants.ISLOGIN, true)
            requireContext().checkLogin()
        }
    }

    override fun onFailure(message: kotlin.String) {

    }
}