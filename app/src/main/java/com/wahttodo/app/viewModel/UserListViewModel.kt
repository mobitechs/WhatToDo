package com.wahttodo.app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.wahttodo.app.model.CategoryListItems
import com.wahttodo.app.model.GroupListItems
import com.wahttodo.app.model.ProductListItems
import com.wahttodo.app.model.ShortListedItems
import com.wahttodo.app.repository.UserListRepository

class UserListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserListRepository(application)
    val showProgressBar: LiveData<Boolean>
    val shortListedItems: LiveData<ArrayList<ShortListedItems>>
    val groupListItems: LiveData<ArrayList<GroupListItems>>
    val categoryListItems: LiveData<ArrayList<CategoryListItems>>


    init {
        this.showProgressBar = repository.showProgressBar


        this.groupListItems = repository.groupListItems
        this.categoryListItems = repository.categoryListItems
        this.shortListedItems = repository.shortListedItems
    }

    fun changeState() {
        repository.changeState()
    }


    fun getMyGroupList(userId: String) {
        repository.getMyGroupList(userId)
    }

    fun getAllProduct() {
        repository.getAllProduct()
    }




}