package com.wahttodo.app.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wahttodo.app.R
import com.wahttodo.app.utils.addFragmentWithData
import com.wahttodo.app.utils.replaceFragment
import com.wahttodo.app.view.fragment.*

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)


        displayView(1)
    }

    fun displayView(pos: Int) {
        when (pos) {
            1 -> {
                replaceFragment(
                    AuthLoginFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "AuthLoginFragment"
                )
            }
            2 -> {
                replaceFragment(
                    AuthLoginFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "AuthLoginFragment"
                )
            }
            3 -> {
                replaceFragment(
                    AuthRegisterFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "AuthRegisterFragment"
                )
            }
            4 -> {
                replaceFragment(
                    AuthForgetPasswordFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "AuthForgetPasswordFragment"
                )
            }
        }
    }

    fun openOTPPage(email: String) {
        val bundle = Bundle()
        bundle.putString("email", email)
        addFragmentWithData(
            EnterOTPFragment(),
            false,
            R.id.nav_host_fragment,
            "EnterOTPFragment", bundle
        )
    }

    fun openLoginPage() {
        displayView(2)
    }
    fun openRegisterPage() {
        displayView(3)
    }
    fun openForgotPasswordPage() {
        displayView(4)
    }



    fun openSetPassword(email: String) {
        val bundle = Bundle()
        bundle.putString("email", email)
        addFragmentWithData(
            AuthSetPasswordFragment(),
            false,
            R.id.nav_host_fragment,
            "AuthSetPasswordFragment", bundle
        )
    }
    fun openRegistrationFrag(email: String) {
        val bundle = Bundle()
        bundle.putString("email", email)
        addFragmentWithData(
            AuthRegisterFragment(),
            false,
            R.id.nav_host_fragment,
            "AuthRegisterFragment", bundle
        )
    }



}