package com.wahttodo.app.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wahttodo.app.callbacks.ApiResponse
import com.wahttodo.app.model.*
import com.wahttodo.app.utils.Constants
import com.wahttodo.app.utils.apiGetCall
import com.wahttodo.app.utils.apiGetCall2
import com.wahttodo.app.utils.showToastMsg


class UserListRepository(val application: Application) : ApiResponse {

    val showProgressBar = MutableLiveData<Boolean>()
    val groupListItems = MutableLiveData<ArrayList<GroupListItems>>()
    val categoryListItems = MutableLiveData<ArrayList<CategoryListItems>>()
    val shortListedItems = MutableLiveData<ArrayList<ShortListedItems>>()
    val joinedRoomListItems = MutableLiveData<ArrayList<JoinedRoomListItems>>()
    val movieList = MutableLiveData<ArrayList<MovieList>>()

    var method = ""
    var userId = ""

    fun changeState() {
        showProgressBar.value = !(showProgressBar != null && showProgressBar.value!!)
    }

    fun searchMovies(
        languageCode: String,
        typeId: String,
        selectedType2: String,
        selectedType3: String
    ) {
        method = "searchMovies"
        var url = Constants.BASE_TMDB_URL + "&with_original_language=$languageCode&with_genres=$typeId"
        var url2 = Constants.BASE_TMDB_URL + "&with_original_language=$languageCode&with_genres=$selectedType2"
        var url3 = Constants.BASE_TMDB_URL + "&with_original_language=$languageCode&with_genres=$selectedType3"
        if(typeId !="None"){
            apiGetCall2(url, this, method)
        }
        if(selectedType2 !="None"){
            apiGetCall2(url2, this, method)
        }
        if(selectedType3 !="None"){
            apiGetCall2(url3, this, method)
        }
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
            else if (method == "searchMovies") {
                val type = object : TypeToken<ArrayList<MovieList>>() {}.type
                var listItems: ArrayList<MovieList>? =
                    gson.fromJson(data.toString(), type)
                movieList.value = listItems
            }

        }
    }

    override fun onFailure(message: String) {
        showProgressBar.value = false
    }


}