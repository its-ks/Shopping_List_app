package com.example.todo.ui.theme.home

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.ui.theme.Category
//import java.lang.reflect.Modifier
import androidx.compose.material.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.todo.room.Models.Item
import com.example.todo.room.Models.ItemsWithStoreAndList
import com.example.todo.ui.theme.Shapes
import com.example.todo.ui.theme.Utils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    // is a callback function that allows navigation or interaction with other parts of the app.
    onNavigate:(Int) -> Unit
){
    // responsible for managing the UI state and interactions on this screen.
    val homeViewModel = viewModel(modelClass = HomeViewModel::class.java)
    val homeState = homeViewModel.state
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {onNavigate.invoke(-1)}) {
                Icon(
                    imageVector = Icons.Default.Add ,
                    contentDescription =null
                    )
            }
        }
    ) {
        LazyColumn{
            item{
                LazyRow(){
                    items(Utils.category){
                        category: Category ->  
                        CategoryItem(iconRes = category.resId ,
                            title = category.title,
                            selected = category==homeState.category) {
                            homeViewModel.onCategoryChange(category)
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                }
            }
            items(homeState.items){
                val dismissState = rememberDismissState(
                    confirmStateChange = { value ->
                        if (value==DismissValue.DismissedToEnd){
                            homeViewModel.deleteItems(it.item)
                        }
                        true
                    }
                )
                SwipeToDismiss(
                    state = dismissState,
                    background = {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Red
                        ) {

                        }
                    },
                    dismissContent = {
                        ShoppingItems(item = it, isChecked = it.item.isChecked, onCheckedChange = homeViewModel::onItemCheckedChange ) {
                            onNavigate.invoke(it.item.id)
                        }
                    }

                )


            }

        }
    }
}


@Composable
fun CategoryItem(
    @DrawableRes iconRes:Int,
    title: String,
    selected: Boolean,
    onItemClick:() -> Unit
){
    Card(
        modifier = Modifier
            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
            //visually indicate whether the Card is selected or not.
            .selectable(
                selected = selected,
                //interactionSource create an interaction source for handling user interactions like clicks.
                interactionSource = MutableInteractionSource(),

                // provide ripple effect
                indication = rememberRipple(),
                onClick = { onItemClick.invoke() }
            ),
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colors.primary.copy(.5f)
            else MaterialTheme.colors.onSurface,
        ),
        shape = Shapes.large,
        backgroundColor = if (selected)
            MaterialTheme.colors.primary.copy(.5f)
        else MaterialTheme.colors.surface,
        contentColor = if (selected) MaterialTheme.colors.onPrimary
        else MaterialTheme.colors.onSurface
    ){
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text= title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ShoppingItems(
    item: ItemsWithStoreAndList,
    isChecked: Boolean,
    onCheckedChange: (Item,Boolean) -> Unit,
    onItemClick: ()-> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick.invoke() }
            .padding(8.dp)
    ){
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text= item.item.itemname,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(text= item.store.storeName )
                Spacer(modifier = Modifier.size(4.dp))
                //change the alpha (transparency) of the content within a specific scope.
                // or modify the behavior or appearance of Composables
                // temporarily within a specific portion of your user interface.
                //LocalContentAlpha, you can control how opaque or transparent the content appears.
                //ContentAlpha.disabled. This change affects the alpha (transparency) of the content within the scope.
                // disabled - it makes the content appear partially transparent to indicate its disabled state.
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                    Text(text = formatDate(item.item.date),
                        style = MaterialTheme.typography.subtitle1)
                }
            }
            Column(modifier = Modifier.padding(8.dp)){
                Text(text = "Qty: ${item.item.qty}",
                     style = MaterialTheme.typography.h6,
                     fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(4.dp))
                Checkbox(checked = isChecked, onCheckedChange = {
                    onCheckedChange.invoke(item.item, it)
                })
            }

        }
    }
}
//converts it into a formatted string.
fun formatDate ( date: Date):String =
    // It's important to specify the locale when formatting or parsing dates because date
    // representations can vary based on different locales. By using the default locale,
    // you ensure that the date format is appropriate for the user's device settings.
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)