package com.example.books.composables

import android.app.Application
import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.getValue
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.books.AddBookActivity
import com.example.books.BookRepository
import com.example.books.BookViewModel
import com.example.books.GoogleRetroFitClient
import com.example.books.RetrofitClient
import com.example.books.TokenManager

@Composable
fun MainScreen(viewModel: BookViewModel, startRoute: String? = null) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val textFieldState = remember { mutableStateOf(TextFieldState()) }


//    LaunchedEffect(startRoute) {
//        if (startRoute != null && startRoute != "home") {
//            navController.navigate(startRoute) {
//                popUpTo("home")
//            }
//        }
//    }

    Scaffold(
        topBar = {

        },
        bottomBar = {
            NavigationBar { // This is the Material 3 Bottom Bar
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Home Item
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("History") }
                )

                // Wishlist Item
                NavigationBarItem(
                    selected = currentRoute == "wishlist",
                    onClick = { navController.navigate("wishlist") },
                    icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist") },
                    label = { Text("Wishlist") }
                )
            }
        },  floatingActionButton = {
            FloatingActionButton(onClick = { context.startActivity(Intent(context, AddBookActivity::class.java)) }) {
                Icon(Icons.Filled.Add, contentDescription = "Add new book")
            }
        }
    ) { innerPadding ->
        // This NavHost acts as your "Fragment Container"
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding) // Important: use the padding!
        ) {
            composable("home") { BookListScreen(viewModel) }
            composable("wishlist") { WishlistScreen(viewModel) }
        }
    }
}