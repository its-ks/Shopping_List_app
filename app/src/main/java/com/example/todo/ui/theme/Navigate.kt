package com.example.todo.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todo.ui.theme.details.DetailScreen
import com.example.todo.ui.theme.home.HomeScreen

enum class Routes{
    Home,
    Detail
}

@Composable
fun TodoNavi(
    navHostController: NavHostController = rememberNavController()
){
    //NavHost that serves as the container for the navigation system.
    NavHost(navController = navHostController, startDestination = Routes.Home.name){
        // Composable for the Home screen
        composable(route = Routes.Home.name){
            HomeScreen(onNavigate = { id ->
                // Navigate to the Detail screen with the provided id
                navHostController.navigate(route = "${Routes.Detail.name}?id=$id")
            })
        }

        // Composable for the Detail screen
        composable(
            // Route with dynamic id parameter
            route = "${Routes.Detail.name}?id={id}",
            arguments = listOf(navArgument("id"){type = NavType.IntType})
        ){
            // Extract the id parameter from the arguments
            val id = it.arguments?.getInt("id") ?: -1

            // Render the DetailScreen with the extracted id
            DetailScreen(id = id) {
                // Navigate up when the action is triggered in the DetailScreen
                navHostController.navigateUp()
            }
        }
    }
}
