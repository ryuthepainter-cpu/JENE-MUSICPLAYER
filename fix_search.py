import re

content = """package com.jene.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jene.music.ui.MainViewModel
import com.jene.music.ui.components.SongListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MainViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    // Empty flow if search query is blank
    val searchResults by if (searchQuery.isNotBlank()) {
        viewModel.repository.searchSongs("%${searchQuery}%").collectAsStateWithLifecycle(emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 48.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("What do you want to listen to?") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            items(searchResults, key = { it.id }) { song ->
                SongListItem(
                    song = song,
                    onClick = { viewModel.playSong(song, searchResults) },
                    onFavoriteClick = { viewModel.toggleFavorite(song) },
                    onAddToPlaylist = { /* Handle add to playlist */ }
                )
            }
        }
    }
}
"""

with open("app/src/main/java/com/jene/music/ui/screens/SearchScreen.kt", "w") as f:
    f.write(content)
