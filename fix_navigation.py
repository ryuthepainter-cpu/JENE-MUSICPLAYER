import re

with open("app/src/main/java/com/jene/music/ui/navigation/JeneNavigation.kt", "r") as f:
    content = f.read()

content = content.replace(
"""            composable("library") { 
                LibraryScreen(viewModel, onNavigateToAlbum = { albumName, artistName ->
                    val encodedAlbum = Uri.encode(albumName)
                    val encodedArtist = Uri.encode(artistName)
                    navController.navigate("albumDetail/$encodedAlbum/$encodedArtist")
                }) 
            }""",
"""            composable("library") { 
                LibraryScreen(viewModel, onNavigateToAlbum = { albumName, artistName ->
                    val encodedAlbum = Uri.encode(albumName)
                    val encodedArtist = Uri.encode(artistName)
                    navController.navigate("albumDetail/$encodedAlbum/$encodedArtist")
                }, onNavigateToPlaylist = { id -> navController.navigate("playlistDetail/$id") }) 
            }""")

with open("app/src/main/java/com/jene/music/ui/navigation/JeneNavigation.kt", "w") as f:
    f.write(content)
