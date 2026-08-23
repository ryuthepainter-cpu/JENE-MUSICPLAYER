package com.jene.music.ui.navigation

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jene.music.ui.MainViewModel
import com.jene.music.ui.screens.*
import com.jene.music.ui.components.GlassBottomNavigation
import com.jene.music.ui.components.MiniPlayer

@Composable
fun JeneNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val showBottomBar = currentRoute !in listOf("nowPlaying", "settings")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Column {
                    MiniPlayer(
                        viewModel = viewModel, 
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        onNavigateToNowPlaying = { navController.navigate("nowPlaying") }
                    )
                    GlassBottomNavigation(navController = navController)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(if (showBottomBar) innerPadding else PaddingValues(0.dp)).fillMaxSize()
        ) {
            composable("home") { 
                HomeScreen(
                    viewModel = viewModel, 
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToAlbum = { albumName, artistName ->
                        val encodedAlbum = Uri.encode(albumName)
                        val encodedArtist = Uri.encode(artistName)
                        navController.navigate("albumDetail/$encodedAlbum/$encodedArtist")
                    }
                ) 
            }
            composable("library") { 
                LibraryScreen(viewModel, onNavigateToAlbum = { albumName, artistName ->
                    val encodedAlbum = Uri.encode(albumName)
                    val encodedArtist = Uri.encode(artistName)
                    navController.navigate("albumDetail/$encodedAlbum/$encodedArtist")
                }) 
            }
            composable("search") { SearchScreen(viewModel) }
            composable("playlists") { PlaylistsScreen(viewModel, onNavigateToPlaylist = { id -> navController.navigate("playlistDetail/$id") }) }
            
            composable(
                "albumDetail/{albumName}/{artistName}",
                arguments = listOf(
                    navArgument("albumName") { type = NavType.StringType },
                    navArgument("artistName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val albumName = Uri.decode(backStackEntry.arguments?.getString("albumName") ?: "")
                val artistName = Uri.decode(backStackEntry.arguments?.getString("artistName") ?: "")
                AlbumDetailScreen(
                    viewModel = viewModel,
                    albumName = albumName,
                    artistName = artistName,
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(
                "nowPlaying",
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down) }
            ) {
                NowPlayingScreen(viewModel, onBack = { navController.popBackStack() })
            }
            
            composable(
                "playlistDetail/{playlistId}",
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { backStackEntry ->
                val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
                PlaylistDetailScreen(
                    viewModel = viewModel,
                    playlistId = playlistId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("settings") {
                SettingsScreen(viewModel, onBack = { navController.popBackStack() })
            }
        }
    }
}
