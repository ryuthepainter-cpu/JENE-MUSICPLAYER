package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SongsScreen
import com.example.ui.screens.AlbumsScreen
import com.example.ui.screens.PlaylistsScreen
import com.example.ui.components.GlassBottomNavigation
import com.example.ui.components.MiniPlayer

@Composable
fun AuraNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            Column {
                MiniPlayer(viewModel = viewModel, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                GlassBottomNavigation(navController = navController)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            composable("home") { HomeScreen(viewModel) }
            composable("songs") { SongsScreen(viewModel) }
            composable("albums") { AlbumsScreen(viewModel) }
            composable("playlists") { PlaylistsScreen(viewModel) }
        }
    }
}
