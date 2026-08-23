sed -i 's/fun PlaylistsScreen(viewModel: MainViewModel)/fun PlaylistsScreen(viewModel: MainViewModel, onNavigateToPlaylist: (Long) -> Unit)/' app/src/main/java/com/jene/music/ui/screens/PlaylistsScreen.kt
sed -i 's/clickable { \/\* TODO: Navigate to Playlist \*\/ }/clickable { onNavigateToPlaylist(playlist.id) }/' app/src/main/java/com/jene/music/ui/screens/PlaylistsScreen.kt

sed -i 's/composable("playlists") { PlaylistsScreen(viewModel) }/composable("playlists") { PlaylistsScreen(viewModel, onNavigateToPlaylist = { id -> navController.navigate("playlistDetail\/$id") }) }/' app/src/main/java/com/jene/music/ui/navigation/JeneNavigation.kt

sed -i '/composable("settings")/i \
            composable(\n                "playlistDetail/{playlistId}",\n                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })\n            ) { backStackEntry ->\n                val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L\n                PlaylistDetailScreen(\n                    viewModel = viewModel,\n                    playlistId = playlistId,\n                    onBack = { navController.popBackStack() }\n                )\n            }\n' app/src/main/java/com/jene/music/ui/navigation/JeneNavigation.kt
