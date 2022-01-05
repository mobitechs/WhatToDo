package com.wahttodo.app.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wahttodo.app.R
import com.wahttodo.app.callbacks.ApiResponse
import com.wahttodo.app.model.UserModel
import com.wahttodo.app.session.SharePreferenceManager
import com.wahttodo.app.utils.*
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class AuthRegisterFragment : Fragment() , ApiResponse {

    lateinit var rootView: View
    lateinit var layoutLoader: RelativeLayout
    var email = ""
    lateinit var etName: AppCompatEditText
    lateinit var etEmail: AppCompatEditText
    lateinit var etMobileNo: AppCompatEditText
//    lateinit var etPassword: TextInputEditText
//    lateinit var etConfirmPassword: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_auth_register, container, false)
        initView()
        return rootView
    }

    private fun initView() {

        layoutLoader = rootView.findViewById(R.id.layoutLoader)
        val btnSignUp: Button = rootView.findViewById(R.id.btnSignUp)!!
        etName = rootView.findViewById(R.id.etName)
        etEmail = rootView.findViewById(R.id.etEmail)
        etMobileNo = rootView.findViewById(R.id.etMobileNo)

//        etPassword = rootView.findViewById(R.id.etPassword)
//        etConfirmPassword = rootView.findViewById(R.id.etConfirmPassword)




        btnSignUp.setOnClickListener {

            if (etName.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Name")
            } else if (etEmail.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Email Id")
            } else if (etMobileNo.text.toString().equals("")) {
                requireActivity().showToastMsg("Enter Mobile No")
            }
//            else if (etPassword.text.toString().equals("")) {
//                requireActivity().showToastMsg("Enter Password")
//            }
//            else if (etConfirmPassword.text.toString().equals("")) {
//                requireActivity().showToastMsg("Enter Confirm Password ")
//            }
            else {
                if (isEmailValid(etEmail.text.toString()) != true) {
                    requireActivity().showToastMsg("Email is not valid")
                }
//                else if (!etPassword.text.toString().equals(etConfirmPassword.text.toString())) {
//                    requireActivity().showToastMsg("Passwords are not matched")
//                }
                else {
                    callRegisterAPI()

                }
            }
        }
    }

    private fun callRegisterAPI() {
        val method = "userRegister"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("method", method)
            jsonObject.put("name", etName.text.toString())
            jsonObject.put("mobile", etMobileNo.text.toString())
            jsonObject.put("email", etEmail.text.toString())
//            jsonObject.put("password", etPassword.text.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        apiPostCall(Constants.BASE_URL, jsonObject, this, method)
    }

    override fun onSuccess(data: Any, tag: String) {

        if (data.equals("USER_ALREADY_EXIST")) {
            requireActivity().showToastMsg("User details already exist")
        } else if (data.equals("FAILED")) {
            requireActivity().showToastMsg("Registration failed")
        } else {
            requireActivity().showToastMsg("Registration successfully done")

            //do logout
            SharePreferenceManager.getInstance(requireContext()).clearSharedPreference(requireContext())
            requireActivity().finish()

            val gson = Gson()
            val type = object : TypeToken<ArrayList<UserModel>>() {}.type
            var user: ArrayList<UserModel>? = gson.fromJson(data.toString(), type)

            Log.d("user", "" + user)
            SharePreferenceManager.getInstance(requireActivity()).save(Constants.ISLOGIN, true)
            SharePreferenceManager.getInstance(requireActivity())
                .saveUserLogin(Constants.USERDATA, user)


            requireActivity().checkLogin()
//            requireActivity().openActivity(UserHomeActivity::class.java)
        }
        layoutLoader.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        requireActivity().showToastMsg(message)
        layoutLoader.visibility = View.GONE
    }
}