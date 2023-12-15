package com.example.todo.ui.theme.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.Graph
import com.example.todo.room.Models.Item
import com.example.todo.room.Models.ItemsWithStoreAndList
import com.example.todo.room.Models.ShoppingList
import com.example.todo.ui.theme.Category
import com.example.todo.ui.theme.repository.Repository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository = Graph.repository
):ViewModel() {
    //var state is a property that plays a crucial role in
    // managing and representing the current state of the UI for the home screen or list-based view
    var state by mutableStateOf(HomeState())
        //private set: It specifies that the state property can only be modified within the HomeViewModel class.
        private set
    init {
        getItems()
    }
    private fun getItems(){
        //This indicates that it runs within the lifecycle of the ViewModel and handles asynchronous operations gracefully.
        viewModelScope.launch {
            repository.getItemsWithListAndStore.collectLatest {
                //This is likely using Flow or LiveData to asynchronously observe changes in items.
                state = state.copy(
                    //This ensures that the UI reflects the latest data.
                    items = it
                )
            }
        }
    }

    fun deleteItems(item: Item){
        viewModelScope.launch {
            repository.deleteItem(item)

        }
    }

    fun onCategoryChange(category: Category){
        state=state.copy(category=category)
        filterBy(category.id)
    }
    fun onItemCheckedChange(item: Item , isChecked:Boolean){
        viewModelScope.launch{
            repository.updateItem(
                item=item.copy(isChecked = isChecked)
            )
        }
    }
    private fun filterBy(shoppingListId:Int) {
        if(shoppingListId!=10001){
            viewModelScope.launch {
                repository.getItemWithStoreAndListFilteredById(shoppingListId).collectLatest {
                    state=state.copy(items=it)
                }
            }
        }else{
            getItems()
        }
    }



}

data class HomeState(
    val items: List<ItemsWithStoreAndList> = emptyList(),
    val category: Category = Category(),
    val itemChecked: Boolean = false
)