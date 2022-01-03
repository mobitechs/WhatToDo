package com.wahttodo.app.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@SuppressLint("ParcelCreator")
@Parcelize
data class ShortListedItems(
    val id: String,
    val name: String
) : Parcelable