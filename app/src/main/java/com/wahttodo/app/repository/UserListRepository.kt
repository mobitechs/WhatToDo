package com.wahttodo.app.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wahttodo.app.callbacks.ApiResponse
import com.wahttodo.app.model.CategoryListItems
import com.wahttodo.app.model.GroupListItems
import com.wahttodo.app.model.JoinedRoomListItems
import com.wahttodo.app.model.ShortListedItems
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.apiGetCall
import com.wahttodo.app.utils.showToastMsg


class UserListRepository(val application: Application) : ApiResponse {

    val showProgressBar = MutableLiveData<Boolean>()
    val groupListItems = MutableLiveData<ArrayList<GroupListItems>>()
    val categoryListItems = MutableLiveData<ArrayList<CategoryListItems>>()
    val shortListedItems = MutableLiveData<ArrayList<ShortListedItems>>()
    val joinedRoomListItems = MutableLiveData<ArrayList<JoinedRoomListItems>>()

    var method = ""
    var userId = ""

    fun changeState() {
        showProgressBar.value = !(showProgressBar != null && showProgressBar.value!!)
    }

    fun getAllProduct() {
        method = "getAllProductList"
        var url = Constants.BASE_URL + "?method=$method"
        apiGetCall(url, this, method)
    }


    fun getMyGroupList(userId: String) {
        showProgressBar.value = true
        method = "getJoinedRoom"
        var url = Constants.BASE_URL + "?method=$method&userId=$userId"
        apiGetCall(url, this, method)
    }


    override fun onSuccess(data: Any, tag: String) {
        showProgressBar.value = false

        if (data.equals("List not available")) {
            application.showToastMsg(data.toString())
        } else {
            val gson = Gson()

            if (method == "getJoinedRoom") {
                val type = object : TypeToken<ArrayList<JoinedRoomListItems>>() {}.type
                var listItems: ArrayList<JoinedRoomListItems>? =
                    gson.fromJson(data.toString(), type)
                joinedRoomListItems.value = listItems
            }

        }
    }

    override fun onFailure(message: String) {
        showProgressBar.value = false
    }


}