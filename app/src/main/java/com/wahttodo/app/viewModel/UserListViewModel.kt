package com.wahttodo.app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.wahttodo.app.model.*
import com.wahttodo.app.repository.UserListRepository

class UserListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserListRepository(application)
    val showProgressBar: LiveData<Boolean>
    val shortListedItems: LiveData<ArrayList<ShortListedItems>>
    val groupListItems: LiveData<ArrayList<GroupListItems>>
    val categoryListItems: LiveData<ArrayList<CategoryListItems>>
    val joinedRoomListItems: LiveData<ArrayList<JoinedRoomListItems>>
    val movieList: LiveData<ArrayList<MovieList>>


    init {
        this.showProgressBar = repository.showProgressBar
        this.groupListItems = repository.groupListItems
        this.categoryListItems = repository.categoryListItems
        this.shortListedItems = repository.shortListedItems
        this.joinedRoomListItems = repository.joinedRoomListItems
        this.movieList = repository.movieList
    }

    fun changeState() {
        repository.changeState()
    }


    fun getMyGroupList(userId: String) {
        repository.getMyGroupList(userId)
    }

    fun searchMovies(
        languageCode: String,
        typeId: String,
        selectedType2: String,
        selectedType3: String
    ) {
        repository.searchMovies(languageCode,typeId,selectedType2,selectedType3)
    }




}