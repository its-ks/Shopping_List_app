package com.example.todo

import android.content.Context
import com.example.todo.room.Models.ShoppingListDatabase
import com.example.todo.ui.theme.repository.Repository

object Graph {
    lateinit var db:ShoppingListDatabase
        private set
    val repository  by lazy{
        Repository(
            listDao = db.listDao(),
            storeDao = db.storeDao(),
            itemDao = db.itemDao()
        )
    }

    fun provide(context: Context){
        db = ShoppingListDatabase.getDatabase(context)
    }
}