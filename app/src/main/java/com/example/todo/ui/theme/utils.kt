package com.example.todo.ui.theme

import androidx.annotation.DrawableRes
import com.example.todo.R


object Utils {
    val category = listOf(
        Category(title = "Drinks", resId = R.drawable.ic_drinks, id = 0),
        Category(title = "Vegetable", resId = R.drawable.ic_vegitables, id = 1),
        Category(title = "Fruits", resId = R.drawable.ic_fruits, id = 2),
        Category(title = "Cleaning", resId = R.drawable.ic_cleaning, id = 3),
        Category(title = "Electronic", resId = R.drawable.ic_electronic, id = 4),
        Category(title = "Grocery", resId =R.drawable.ic_grocery ,id = 5),
        Category(title = "none", resId = R.drawable.none,id = 10001)
    )
}

data class Category(
    @DrawableRes val resId: Int = -1,
    val title: String = "",
    val id: Int = -1,)