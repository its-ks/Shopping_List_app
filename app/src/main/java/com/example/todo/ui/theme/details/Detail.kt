package com.example.todo.ui.theme.details

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.ui.theme.Category
import com.example.todo.ui.theme.Shapes
import com.example.todo.ui.theme.Utils
import com.example.todo.ui.theme.home.CategoryItem
import com.example.todo.ui.theme.home.formatDate
import java.lang.reflect.Modifier
import java.util.*



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DetailScreen(
    id: Int,
    navigateUp:() -> Unit
){
    var viewModel =  viewModel<DetailViewModel>(factory = DetailViewModelFactor(id))
    Scaffold{
        DetailEntry(
            state = viewModel.state,
            onDateSelected = viewModel::onDateChange,
            onStoreChange = viewModel::onStoreChange,
            onItemChange = viewModel::onItemChange,
            onQtyChange = viewModel::onQtyChange,
            onCategoryChange = viewModel::onCategoryChange,
            onDialogDismissed = viewModel::onScreenDialogDismissed,
            onSaveStore = {viewModel::addStore},
            updateItem = {viewModel.updateShoppingItem(id) },
            saveItem = {viewModel::addShoppingItem}) {
            navigateUp.invoke()
        }
    }
}

@Composable
private fun DetailEntry(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    state : DetailState,
    //Unit, which is similar to void in other languages. It means the function doesn't return any meaningful value.
    onDateSelected:(Date) -> Unit,
    onStoreChange:(String) -> Unit,
    onItemChange:(String) -> Unit,
    onQtyChange:(String) -> Unit,
    onCategoryChange:(Category) -> Unit,
    onDialogDismissed:(Boolean) ->Unit,
    onSaveStore:() ->Unit,
    updateItem:() ->Unit,
    saveItem:() ->Unit,
    navigateUp:() ->Unit,
){
    var isNewEnabled by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier.padding(16.dp)
    ){
        TextField(
            value = state.item,
            onValueChange = { onItemChange(it) },
            label = { Text(text = "Item")},
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = Shapes.large
        )
        Spacer(modifier = androidx.compose.ui.Modifier.Companion.size(12.dp))
        
        Row {
            TextField(
                value = state.store ,
                onValueChange = {
                    if(isNewEnabled) onStoreChange.invoke(it)
                },
            modifier = modifier.weight(1f),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = Shapes.large,
            label = {Text(text = "Store")},
            leadingIcon = {
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null,
                modifier = androidx.compose.ui.Modifier.clickable {
                    onDialogDismissed.invoke(!state.isScreenDialogDismissed)
                })
            })
            if(!state.isScreenDialogDismissed){
                Popup(
                    onDismissRequest = {
                        onDialogDismissed.invoke(!state.isScreenDialogDismissed)
                    }
                ){
                    Surface(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
                        Column {
                            state.storeList.forEach{
                                Text(text = it.storeName,
                                modifier = androidx.compose.ui.Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        onStoreChange.invoke(it.storeName)
                                        onDialogDismissed(!state.isScreenDialogDismissed)
                                    },
                                )
                            }
                            
                        }

                    }
                }
            }
            TextButton(onClick = {
                isNewEnabled = if (isNewEnabled) {
                    onSaveStore.invoke()
                    !isNewEnabled
                } else {
                    !isNewEnabled
                }
            }) {
                Text(text = if (isNewEnabled) "Save" else "New")
            }


        }

        Spacer(modifier = androidx.compose.ui.Modifier.size(12.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.DateRange , contentDescription = null)
                Spacer(modifier = androidx.compose.ui.Modifier.size(4.dp))
                Text(text = formatDate(state.date))
                Spacer(modifier = androidx.compose.ui.Modifier.size(4.dp))
                val mDatePicker = datePickerDialog(context = LocalContext.current ,
                    onDateSelected ={
                        date -> onDateSelected.invoke(date)
                    })
                IconButton(onClick = { mDatePicker.show()}) {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
            }
            TextField(
                value = state.qty,
                onValueChange = { newQty -> onQtyChange(newQty) },
                label = { Text("Qty") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                shape = Shapes.large
            )

        }

        Spacer(modifier = androidx.compose.ui.Modifier.size(4.dp))
        LazyRow {
            items(Utils.category){
                category : Category ->  CategoryItem(
                iconRes = category.resId,
                title = category.title,
                selected = category == state.category)
            {
                onCategoryChange(category)
            }
                Spacer(modifier = androidx.compose.ui.Modifier.size(16.dp))
            }
        }
        val buttonTitle = if (state.isUpdatingItem) "Update Item"
        else "Add Item"
        Button(onClick = {
                         when(state.isUpdatingItem){
                             true -> {
                                 updateItem.invoke()
                             }
                             false -> {
                                 saveItem.invoke()
                             }
                         }
            navigateUp.invoke()
        }, modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        enabled = state.item.isNotEmpty() && state.store.isNotEmpty() && state.qty.isNotEmpty(),
            shape=Shapes.large)

        {
            Text(text = buttonTitle)
        } 

    }
}

@Composable
fun datePickerDialog(
    context: Context,
    onDateSelected: (Date) -> Unit
):DatePickerDialog{
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val mDatePickerDialog = DatePickerDialog(context,
        { _: DatePicker, mYear:Int, mMonth:Int, mDayofMonth:Int ->
            // Create a new Calendar instance and set it to the selected date
            val calendar = Calendar.getInstance()
            calendar.set(mYear,mMonth,mDayofMonth)

            // Invoke the provided onDateSelected callback with the selected date
            onDateSelected.invoke(calendar.time)
        },year,month,day)
    return mDatePickerDialog
}

@Preview(showSystemUi = true)
@Composable
fun prewDetail(){
    DetailEntry(
        state = DetailState(),
        onDateSelected = {},
        onStoreChange = {},
        onItemChange = {},
        onQtyChange = {},
        onCategoryChange = {},
        onDialogDismissed = {},
        onSaveStore = { /*TODO*/ },
        updateItem = { /*TODO*/ },
        saveItem = { /*TODO*/ }) {
        
    }
}
