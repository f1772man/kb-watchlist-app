package com.kb.watchlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kb.watchlist.ui.screens.DetailScreen
import com.kb.watchlist.ui.screens.HomeScreen
import com.kb.watchlist.ui.screens.SearchScreen
import com.kb.watchlist.ui.theme.KBWatchlistTheme
import com.kb.watchlist.ui.theme.NavyDeep
import com.kb.watchlist.viewmodel.DetailViewModel
import com.kb.watchlist.viewmodel.HomeViewModel
import com.kb.watchlist.viewmodel.SearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KBWatchlistTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NavyDeep
                ) {
                    WatchlistAppNavHost()
                }
            }
        }
    }
}

@Composable
fun WatchlistAppNavHost() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToSearch = { navController.navigate("search") },
                onNavigateToDetail = { code -> navController.navigate("detail/$code") }
            )
        }

        composable("search") {
            val searchViewModel: SearchViewModel = viewModel()
            SearchScreen(
                viewModel = searchViewModel,
                onNavigateBack = {
                    homeViewModel.refresh()
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "detail/{code}",
            arguments = listOf(navArgument("code") { type = NavType.StringType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("code") ?: ""
            val detailViewModel: DetailViewModel = viewModel()
            DetailScreen(
                stockCode = code,
                viewModel = detailViewModel,
                onNavigateBack = {
                    homeViewModel.refresh()
                    navController.popBackStack()
                }
            )
        }
    }
}
