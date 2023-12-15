package com.example.todo.room.Models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.todo.room.Models.converters.DateConverter

@TypeConverters(value = [DateConverter::class])
@Database(entities = [ShoppingList::class,Item::class,Store::class], version = 1 , exportSchema = false)
abstract class ShoppingListDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun storeDao(): StoreDao
    abstract fun listDao(): ListDao
    //In Kotlin, a companion object is like a special container inside a class where you can put
    // things that are related to the class as a whole, rather than to individual objects created
    // from that class. It's kind of like a toolbox that's shared among all instances of the class.
    companion object {

        // Singleton prevents multiple instances of database opening at the
        // same time.

        @Volatile
        private var INSTANCE: ShoppingListDatabase? = null

        fun getDatabase(context: Context): ShoppingListDatabase {

            if (INSTANCE == null) {

                //synchronized - This block of code ensures that only one thread at a
                // time can enter it. It helps avoid conflicts when creating the database instance concurrently.
                synchronized(ShoppingListDatabase::class.java) {

                    if (INSTANCE == null) {

                        //parameters of builder are
                        // 1. context-current state
                        // 2. database class name
                        // 3. "name
                        INSTANCE = Room.databaseBuilder(
                            context,
                            ShoppingListDatabase::class.java,
                            "ShoppingListDatabase"
                        ).build()
                    }
                }
            }

            //The !! operator in Kotlin is called the "not-null
            //assertion operator." It's used to tell the compiler that
            //a value will never be null at a certain point in the code,
            //allowing you to treat the value as non-null. However,
            //if the value is actually null at that point, a NullPointerException will be thrown.

            return INSTANCE!!
        }

    }

}