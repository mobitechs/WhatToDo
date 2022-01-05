package com.wahttodo.app.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class UserModel(
    var userId: String,
    var name: String,
    var userType: String,
    var email: String,
    var mobile: String,
    var address: String,
    var city: String,
    var pincode: String,
    var dob: String,
    var isVerified: String,
    var userProfilePic: String,
    var latitude: String,
    var longitude: String
) : Parcelable

