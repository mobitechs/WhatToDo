package com.wahttodo.app.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@SuppressLint("ParcelCreator")
@Parcelize
data class GroupListItems(
    val id: String,
    val name: String
) : Parcelable



@SuppressLint("ParcelCreator")
@Parcelize
data class CategoryListItems(
    val id: String,
    val name: String,
    val img: Int
) : Parcelable