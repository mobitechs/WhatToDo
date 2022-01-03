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
    var isSelected: Boolean,
    var wallet: String,
    var isVerified: String,
    var userProfilePic: String,
    var imgPathPassbook: String,
    var imgPathAvatar: String,
    var imgPathAdhar: String,
    var imgPathEBill: String,
    var consoleImgPath: String,
    var latitude: String,
    var longitude: String,
    var firstFreeOrder: String,
) : Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
data class AvailableMerchantListItem(
    var userId: String,
    var name: String,
    var userType: String,
    var email: String,
    var mobile: String,
    var address: String,
    var city: String,
    var pincode: String,
    var dob: String,
    var isSelected: Boolean,
    var latitude: String,
    var longitude: String,
    var mpId: String,
    var totalQty: String,
    var soldQty: String,
    var isSold: String,
    var totalControllerQty: String,
    var remainingControllerQty: String,
    var willDeliver: String,
    var isVerified: String,
    var imgPathPassbook: String,
    var imgPathAvatar: String?,
    var imgPathAdhar: String,
    var imgPathEBill: String,
    var distance: String
) : Parcelable