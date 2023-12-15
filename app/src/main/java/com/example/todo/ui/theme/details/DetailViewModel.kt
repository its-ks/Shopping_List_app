package com.example.todo.ui.theme.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todo.Graph
import com.example.todo.room.Models.Item
import com.example.todo.room.Models.ShoppingList
import com.example.todo.room.Models.Store
import com.example.todo.ui.theme.Category
import com.example.todo.ui.theme.Utils
import com.example.todo.ui.theme.repository.Repository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
//responsible for managing the state and logic for the Adding screen (detail screen)
class DetailViewModel constructor(
    //an instance of the Repository class, which is used for data operations
    private val itemId: Int,
    private val repository: Repository = Graph.repository
):ViewModel() {
    var state by mutableStateOf(DetailState())
        //This property holds an instance of the DetailState class, representing the current state of the screen.
        private set
    init {
        //An initialization block is used to perform setup tasks when an instance of the class is created.
        addListItem()
        getStores()
        if(itemId != -1){
            //viewModelScope.launch is a coroutine builder in Android used within a ViewModel.
            //It's part of the ViewModelScope, which is a special coroutine scope tied to the lifecycle of a ViewModel
            viewModelScope.launch {
                repository.getItemWithStoreAndList(itemId)
                        //used in combination with Flow to collect and process the latest emitted values. This
                    // function is commonly used for observing and reacting to changes in a Flow.
                    .collectLatest {
                        state = state.copy(
                            item= it.item.itemname,
                            store= it.store.storeName,
                            date = it.item.date,
                            //This line is using the find function on Utils.category to look for a category whose id matches
                            // it.shoppingList.id. If a matching category is found, it is assigned to the category variable; otherwise, it defaults to Category()
                            //This pattern is often used when you expect to find a matching item in a collection (in this case, a category
                            // in Utils.category) but want to provide a default value (in this case, a new Category() instance) in case no match is found. The ?:
                            category = Utils.category.find { c ->
                                c.id == it.shoppingList.id }?: Category(),
                            qty = it.item.qty


                        )
                    }
            }
        }
    }
    //Another initialization block is used to set the isUpdatingItem flag based on whether itemId is not equal to -1. This flag likely
    // indicates whether the screen is in "update" mode.
    init {
        state= if( itemId != -1){
            state.copy(isUpdatingItem = true)
        }else{
            state.copy(isUpdatingItem = false)
    }
    }




    val isFieldNotEmpty: Boolean
    //Checks if the item property inside the state object is not empty.
    // It uses the isNotEmpty() function, which returns true if the string is not empty, and false otherwise.
        get() = state.item.isNotEmpty() &&
                state.store.isNotEmpty() &&
                state.qty.isNotEmpty()

    fun onItemChange(newValue:String){
        state=state.copy(item=newValue)
    }
    fun onStoreChange(newValue:String){
        state=state.copy(store =newValue)
    }
    fun onQtyChange(newValue:String){
        state=state.copy(qty = newValue)
    }
    fun onDateChange(newValue:Date){
        state=state.copy(date = newValue)
    }
    fun onCategoryChange(newValue:Category){
        state=state.copy(category = newValue)
    }

    //keep track of whether a screen dialog, such as a popup or dialog
    // window, has been dismissed or closed. It serves as a state indicator
    // for whether the dialog is currently displayed or not.
    fun onScreenDialogDismissed(newValue:Boolean){
        state=state.copy(isScreenDialogDismissed = newValue)
    }

    private fun addListItem(){
        viewModelScope.launch {
            //This iterates over each element in the Utils.category list
            Utils.category.forEach {
                repository.insertList(
                    ShoppingList(
                        id=it.id,
                        name= it.title
                    )
                )
            }
        }
    }

    fun addShoppingItem(){
        viewModelScope.launch {
            repository.insertItem(
                Item(
                    itemname = state.item,
                    listId =  state.category.id,
                    date = state.date,
                    qty = state.qty,
                    //The ID of the store where the item is to be purchased. It attempts to find the store's
                    // ID in the state.storeList based on the store name (state.store). If a matching store is
                    // found, its ID is used; otherwise, a default value of 0 is used.
                    storeIdFK = state.storeList.find {
                        it.storeName==state.store
                    }?.id?: 0,
                    isChecked = false


                )
            )
        }
    }

    fun updateShoppingItem(id:Int){
        viewModelScope.launch {
            repository.insertItem(
                Item(
                    itemname = state.item,
                    listId =  state.category.id,
                    date = state.date,
                    qty = state.qty,
                    //The ID of the store where the item is to be purchased. It attempts to find the store's
                    // ID in the state.storeList based on the store name (state.store). If a matching store is
                    // found, its ID is used; otherwise, a default value of 0 is used.
                    storeIdFK = state.storeList.find {
                        it.storeName==state.store
                    }?.id?: 0,
                    isChecked = false,
                    id=id

                )
            )
        }
    }

    fun addStore(){
        viewModelScope.launch {
            repository.insertStore(
                Store(
                    storeName = state.store,
                    listIdFK = state.category.id
                )
            )
        }
    }

    fun getStores(){
        viewModelScope.launch {
            repository.store.collectLatest {
                state=state.copy(storeList = it)
            }
        }
    }

}
@Suppress("UNCHECKED_CAST")
//The primary purpose of DetailViewModelFactor is to serve as a factory for creating instances of the DetailViewModel class.
class DetailViewModelFactor(private val id: Int):ViewModelProvider.Factory{
    //return a particular viewModel whenever WE create or we call this ViewModelFactory
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //The cast (as T) is used to cast the DetailViewModel instance to the generic type T.
        // This cast is necessary because the function signature requires returning an instance of type T, which is a ViewModel.
        return DetailViewModel(itemId = id) as T
    }
}
data class DetailState(
    val storeList: List<Store> = emptyList(),
    val item: String = "",
    val store: String="",
    val date: Date = Date(),
    val qty: String="",
    val isScreenDialogDismissed:Boolean=true,
    val isUpdatingItem: Boolean = false,
    val category:Category = Category()
){

}