package com.example.todo.ui.theme.repository

import com.example.todo.room.Models.*

//The repository is not part of the
// Architecture Component libraries but
// is a suggested best practice for code separation and architecture.
//A Repository manages queries and allows you to use multiple backends.
// In the most common example, the Repository implements the logic for
// deciding whether to fetch data from a network or use results cached in a local database.
class Repository(
    private val listDao: ListDao,
    private val storeDao: StoreDao,
    private val itemDao: ItemDao
) {
    val store = storeDao.getAllStores()
    val getItemsWithListAndStore = listDao.getItemsWithStoreAndList()

    fun getItemWithStoreAndList(id:Int) = listDao.getItemWithStoreAndListFilteredById(id)

    fun getItemWithStoreAndListFilteredById(id:Int) = listDao.getItemsWithStoreAndListFilteredById(id)

    suspend fun insertList(shoppingList: ShoppingList){
        listDao.insertShoppingList(shoppingList)
    }
    suspend fun insertStore(store: Store){
        storeDao.insert(store)
    }
    suspend fun insertItem(item: Item){
        itemDao.insert(item)
    }
    suspend fun deleteItem(item: Item){
        itemDao.delete(item)
    }
    suspend fun updateItem(item: Item){
        itemDao.update(item)
    }

}